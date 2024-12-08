package com.eripe14.houses.house;

import com.eripe14.database.Database;
import com.eripe14.database.document.Document;
import com.eripe14.database.document.DocumentCollection;
import com.eripe14.houses.house.furniture.HouseCustomFurniture;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.FinalRegionResult;
import com.eripe14.houses.house.region.HouseDistrict;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.house.renovation.Renovation;
import com.eripe14.houses.house.renovation.request.RenovationRequest;
import com.eripe14.houses.house.rent.Rent;
import com.eripe14.houses.position.Position;
import com.eripe14.houses.position.PositionAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HouseService {

    private final Map<String, House> houses = new HashMap<>();
    private final DocumentCollection documentCollection;
    private final ProtectionService protectionService;

    public HouseService(
            Database database,
            ProtectionService protectionService
    ) {
        this.documentCollection = database.getOrCreateCollection("houses_houses");
        for (Document document : this.documentCollection.getAllDocuments()) {
            House house = (House) document;
            this.houses.put(house.getHouseId(), house);
        }

        this.protectionService = protectionService;
    }

    public House createHouse(
            String houseId,
            String defaultSchematicName,
            HouseDistrict district,
            HouseType type,
            FinalRegionResult result,
            Location purchaseFurnitureLocation,
            String blockOfFlatsId,
            int dailyRentalPrice,
            int buyPrice
    ) {
        ProtectedRegion plotRegion = result.plot().get();
        ProtectedRegion houseRegion = result.house().get();

        HouseRegion region = new HouseRegion(
                houseId,
                defaultSchematicName,
                purchaseFurnitureLocation.getWorld(),
                type,
                district,
                (ProtectedPolygonalRegion) plotRegion,
                (ProtectedPolygonalRegion) houseRegion,
                new HouseCustomFurniture("-", new Position(0, 0, 0, 0, 0, ""))
        );

        if (buyPrice != 0) {
            return new House(houseId, Option.none(), region, blockOfFlatsId, buyPrice, dailyRentalPrice);
        }

        return new House(houseId, Option.none(), region, blockOfFlatsId, dailyRentalPrice);
    }

    public House editHouse(
            House house,
            String houseId,
            HouseDistrict district,
            HouseType type,
            String blockOfFlatsId,
            int dailyRentalPrice,
            int buyPrice
    ) {
        HouseRegion houseRegion = new HouseRegion(
                houseId,
                house.getRegion().getDefaultSchematicName(),
                house.getRegion().getWorld(),
                type,
                district,
                house.getRegion().getPlot(),
                house.getRegion().getHouse(),
                house.getRegion().getPurchaseFurniture(),
                house.getRegion().getLatestSchematicName()
        );

        return new House(
                houseId,
                house.getMembers(),
                houseRegion,
                buyPrice,
                dailyRentalPrice,
                house.getRenovationData(),
                blockOfFlatsId,
                house.getOwner(),
                house.getRent(),
                house.getRenovationRequest(),
                house.getCurrentRenovation(),
                house.hasAlarm()
        );
    }

    public boolean isHouseExists(String houseId) {
        return this.houses.values()
                .stream()
                .anyMatch(house -> house.getHouseId().equalsIgnoreCase(houseId));
    }

    public void rentHouse(Player player, House house, Rent rent) {
        Owner owner = new Owner(player.getUniqueId(), player.getName());

        house.setRent(rent);
        house.setOwner(owner);
        this.addHouse(house);
    }

    public void removeRent(House house) {
        house.setRent(null);
        this.addHouse(house);
    }

    public void requestRenovation(House house, RenovationRequest renovationRequest) {
        house.setRenovationRequest(renovationRequest);
        this.addHouse(house);
    }

    public void removeRenovationRequest(String houseId) {
        Option<House> houseOption = this.getHouse(houseId);

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        house.setRenovationRequest(null);
        this.addHouse(house);
    }

    public void renovateHouse(Renovation renovation) {
        String houseId = renovation.getHouseId();

        Option<House> houseOption = this.getHouse(houseId);

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        house.setCurrentRenovation(renovation);

        this.addHouse(house);
    }

    public void setLatestSchematicName(House house, String schematicName) {
        HouseRegion region = house.getRegion();
        region.setLatestSchematicName(schematicName);

        this.addHouse(house);
    }

    public void removeRenovation(Renovation renovation) {
        String houseId = renovation.getHouseId();

        Option<House> houseOption = this.getHouse(houseId);

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        house.setCurrentRenovation(null);

        this.addHouse(house);
    }

    public void resetHouse(House house) {
        house.setRent(null);
        house.setOwner(null);
        house.getMembers().clear();

        this.addHouse(house);
    }

    public void addHouse(House house) {
        this.houses.put(house.getHouseId(), house);
        this.documentCollection.addDocument(house.getHouseId(), house);
    }

    public void removeHouse(String houseId) {
        this.houses.remove(houseId);
        this.documentCollection.removeDocument(houseId);
    }

    public Option<House> getHouse(String houseId) {
        return Option.of(this.houses.get(houseId));
    }

    public Optional<House> getHouseByBlockOfFlatsId(String blockOfFlatsId) {
        return this.houses.values().stream()
                .filter(house -> house.getBlockOfFlatsId().equalsIgnoreCase(blockOfFlatsId))
                .findFirst();
    }

    public Option<House> getHouse(Location purchaseFurnitureLocation) {
        for (House value : this.houses.values()) {
            HouseRegion region = value.getRegion();

            HouseCustomFurniture customFurniture = region.getPurchaseFurniture();
            Location entityLocation = PositionAdapter.convertFurniture(customFurniture.getPosition());

            if (!PositionAdapter.compareLocations(purchaseFurnitureLocation, entityLocation)) {
                return Option.none();
            }

            return Option.of(value);
        }

        return Option.none();
    }

    public Option<House> getHouse(ProtectedRegion locationRegion) {
        for (House value : this.houses.values()) {
            HouseRegion region = value.getRegion();

            if (region.getHouse().equals(locationRegion) || region.getPlot().equals(locationRegion)) {
                return Option.of(value);
            }
        }

        return Option.none();
    }

    public Option<House> getHouseFromFurnitureLocation(String namespacedId, CustomFurniture customFurniture) {
        Entity armorstand = customFurniture.getArmorstand();
        String customFurnitureNamespacedID = customFurniture.getNamespacedID();

        if (namespacedId == null) {
            return Option.none();
        }

        if (armorstand == null) {
            return Option.none();
        }

        Location location = armorstand.getLocation();

        if (!customFurnitureNamespacedID.equalsIgnoreCase(namespacedId)) {
            return Option.none();
        }

        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(location);

        if (regionOption.isEmpty()) {
            return Option.none();
        }

        ProtectedRegion region = regionOption.get();
        Option<House> houseOption = this.getHouse(region);

        if (houseOption.isEmpty()) {
            return Option.none();
        }

        return houseOption;
    }

    public List<UUID> getStuffInHouse(House house) {
        List<UUID> stuff = house.getMembers().values().stream().filter(HouseMember::isCoOwner).map(HouseMember::getMemberUuid).toList();
        List<UUID> stuffResult = new ArrayList<>(stuff);
        stuffResult.add(house.getOwner().get().getUuid());

        return stuffResult;
    }

    public List<Player> getOnlineStuffInHouse(Server server, House house) {
        List<UUID> stuff = this.getStuffInHouse(house);
        List<Player> onlineStuff = new ArrayList<>();

        for (Player onlinePlayer : server.getOnlinePlayers()) {
            if (!stuff.contains(onlinePlayer.getUniqueId())) {
                continue;
            }

            onlineStuff.add(onlinePlayer);
        }

        return onlineStuff;
    }

    public Collection<House> getHousesByOwner(String ownerName) {
        return this.houses.values().stream()
                .filter(house -> house.getOwner().isPresent())
                .filter(house -> house.getOwner().get().getName().equalsIgnoreCase(ownerName))
                .toList();
    }

    public Collection<House> getHousesWithUser(String userName) {
        Collection<House> housesByOwner = this.getHousesByOwner(userName);
        List<House> result = new ArrayList<>(housesByOwner);
        result.addAll(this.getHousesByMember(userName));

        return result;
    }

    public Collection<House> getHousesByMember(String memberName) {
        List<House> result = new ArrayList<>();

        for (House house : this.houses.values()) {
            for (HouseMember houseMember : house.getMembers().values()) {
                if (!houseMember.getMemberName().equalsIgnoreCase(memberName)) {
                   continue;
                }

                result.add(house);
            }
        }

        return result;
    }

    public Collection<House> getHousesThatUserCanRenovate(Player player) {
        Collection<House> housesByOwner = this.getHousesByOwner(player.getName());
        Collection<House> housesByCoOwner = this.getHousesByMember(player.getName()).stream().filter(house -> house.getMembers().values().stream().anyMatch(HouseMember::isCoOwner)).toList();

        List<House> result = new ArrayList<>(housesByOwner);
        result.addAll(housesByCoOwner);

        return result;
    }

    public Collection<House> getHouseInDistrict(Collection<House> houses, HouseDistrict district) {
        return houses.stream().filter(house -> house.getRegion().getDistrict().equals(district)).toList();
    }

    public Collection<House> getNpcHouses() {
        return this.houses.values().stream().filter(house -> house.getOwner().isEmpty()).toList();
    }

    public Collection<String> getAllBlockOfFlatsIds() {
        return this.houses.values().stream()
                .map(House::getBlockOfFlatsId)
                .toList();
    }

    public Collection<House> getApartmentsInBlockOfFlats(House house) {
        return this.houses.values().stream()
                .filter(value -> value.getRegion().getHouseType().equals(HouseType.APARTMENT))
                .filter(value -> value.getBlockOfFlatsId().equalsIgnoreCase(house.getBlockOfFlatsId()))
                .filter(value -> value.getOwner().isEmpty())
                .toList();
    }

    public Collection<House> getAllToBuyApartments() {
        return this.houses.values().stream()
                .filter(house -> house.getRegion().getHouseType().equals(HouseType.APARTMENT))
                .filter(house -> house.getOwner().isEmpty())
                .toList();
    }

    public Collection<House> getAllHouses() {
        return Collections.unmodifiableCollection(this.houses.values());
    }

    public List<House> getAllHousesAsList() {
        return new ArrayList<>(this.houses.values());
    }

    public Collection<House> getBoughtHouses() {
        return this.houses.values().stream().filter(house -> house.getOwner().isPresent()).toList();
    }

}