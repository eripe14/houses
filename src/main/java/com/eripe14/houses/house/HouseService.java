package com.eripe14.houses.house;

import com.eripe14.houses.house.furniture.HouseCustomFurniture;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.FinalRegionResult;
import com.eripe14.houses.house.region.HouseDistrict;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.house.renovation.Renovation;
import com.eripe14.houses.house.renovation.RenovationData;
import com.eripe14.houses.house.renovation.RenovationType;
import com.eripe14.houses.house.renovation.request.RenovationRequest;
import com.eripe14.houses.house.rent.Rent;
import com.eripe14.houses.position.Position;
import com.eripe14.houses.position.PositionAdapter;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import panda.std.Option;
import pl.craftcityrp.developerapi.data.DataBit;
import pl.craftcityrp.developerapi.data.DataChunk;
import pl.craftcityrp.developerapi.data.DataManager;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class HouseService {

    private final Map<String, House> houses = new HashMap<>();
    private final ProtectionService protectionService;
    private final DataManager dataManager;
    private final DataChunk dataChunk;
    private final Server server;

    public HouseService(
            ProtectionService protectionService,
            DataManager dataManager,
            Server server
    ) {
        this.protectionService = protectionService;
        this.dataManager = dataManager;
        this.server = server;

        if (!this.dataManager.getData().containsKey("houses")) {
            this.dataManager.getData().put("houses", new DataBit(new DataChunk()));
        }

        this.dataChunk = this.dataManager.getData().get("houses").asChunk();

        for (String key : this.dataChunk.getData().keySet()) {
            DataChunk houseDataChunk = this.dataChunk.getBit(key).asChunk();
            String houseId = houseDataChunk.getBit("houseId").asString();

            Map<UUID, HouseMember> members = new HashMap<>();

            for (DataBit value : houseDataChunk.getBit("members").asChunk().getData().values()) {
                DataChunk memberDataChunk = value.asChunk();

                Map<HouseMemberPermission, Boolean> permissions = new HashMap<>();

                DataChunk permissionChunk = memberDataChunk.getBit("permissions").asChunk();

                if (permissionChunk == null) {
                    continue;
                }

                for (DataBit permission : permissionChunk.getData().values()) {
                    DataChunk permissionDataChunk = permission.asChunk();

                    if (permissionDataChunk == null) {
                        continue;
                    }

                    permissionDataChunk.getData().forEach((permissionName, isAllowed) -> permissions.put(
                            HouseMemberPermission.valueOf(permissionName),
                            isAllowed.asBoolean()
                    ));
                }

                Option<Instant> coOwnerJoinAtOption;

                if (memberDataChunk.getBit("coOwnerAt").asString().equalsIgnoreCase("-")) {
                    coOwnerJoinAtOption = Option.none();
                } else {
                    coOwnerJoinAtOption = Option.of(Instant.parse(memberDataChunk.getBit("coOwnerAt").asString()));
                }

                HouseMember houseMember = new HouseMember(
                        Instant.parse(memberDataChunk.getBit("joinedAt").asString()),
                        memberDataChunk.getBit("memberName").asString(),
                        UUID.fromString(memberDataChunk.getBit("memberUuid").asString()),
                        houseId,
                        permissions,
                        memberDataChunk.getBit("isCoOwner").asBoolean(),
                        coOwnerJoinAtOption
                );

                members.put(houseMember.getMemberUuid(), houseMember);
            }

            DataChunk regionChunk = houseDataChunk.getBit("region").asChunk();

            World world = this.server.getWorld(regionChunk.getBit("world").asString());
            RegionContainer regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regionManager = regionContainer.get(BukkitAdapter.adapt(world));

            ProtectedPolygonalRegion plotProtectedRegion =
                    (ProtectedPolygonalRegion) regionManager.getRegion(regionChunk.getBit("plot").asString());
            ProtectedPolygonalRegion houseProtectedRegion =
                    (ProtectedPolygonalRegion) regionManager.getRegion(regionChunk.getBit("house").asString());

            DataChunk purchaseFurnitureChunk = regionChunk.getBit("purchaseFurniture").asChunk();
            HouseCustomFurniture purchaseFurniture = new HouseCustomFurniture(
                    purchaseFurnitureChunk.getBit("namespacedId").asString(),
                    new Position(
                            purchaseFurnitureChunk.getBit("position").asChunk().getBit("x").asDouble(),
                            purchaseFurnitureChunk.getBit("position").asChunk().getBit("y").asDouble(),
                            purchaseFurnitureChunk.getBit("position").asChunk().getBit("z").asDouble(),
                            (float) purchaseFurnitureChunk.getBit("position").asChunk().getBit("yaw").asDouble(),
                            (float) purchaseFurnitureChunk.getBit("position").asChunk().getBit("pitch").asDouble(),
                            purchaseFurnitureChunk.getBit("position").asChunk().getBit("world").asString()
                    )
            );

            Set<Position> placedFurnitureLocations = new HashSet<>();
            Set<DataBit> placedFurnitureLocationsChunk = regionChunk.getBit("placedFurnitureLocations").asBitSet();

            for (DataBit value : placedFurnitureLocationsChunk) {
                Map<String, Object> map = (Map<String, Object>) value.getValue();
                DataChunk chunk = new DataChunk();

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    chunk.addBit(entry.getKey(), new DataBit(entry.getValue()));
                }

                Position position = new Position(
                        chunk.getBit("x").asDouble(),
                        chunk.getBit("y").asDouble(),
                        chunk.getBit("z").asDouble(),
                        (float) chunk.getBit("yaw").asDouble(),
                        (float) chunk.getBit("pitch").asDouble(),
                        chunk.getBit("world").asString()
                );

                placedFurnitureLocations.add(position);
            }

            HouseRegion houseRegion = new HouseRegion(
                    houseId,
                    regionChunk.getBit("defaultSchematicName").asString(),
                    world,
                    HouseType.valueOf(regionChunk.getBit("type").asString()),
                    HouseDistrict.valueOf(regionChunk.getBit("district").asString()),
                    plotProtectedRegion,
                    houseProtectedRegion,
                    purchaseFurniture,
                    placedFurnitureLocations,
                    regionChunk.getBit("latestSchematicName").asString()
            );

            int buyPrice = houseDataChunk.getBit("buyPrice").asInt();
            int dailyRentalPrice = houseDataChunk.getBit("dailyRentalPrice").asInt();

            DataChunk renovationDataChunk = houseDataChunk.getBit("renovationData").asChunk();

            Set<Position> placedBlocks = new HashSet<>();
            Set<DataBit> placedBlocksChunk = renovationDataChunk.getBit("placedBlocks").asBitSet();

            for (DataBit value : placedBlocksChunk) {
                Map<String, Object> map = (Map<String, Object>) value.getValue();
                DataChunk chunk = new DataChunk();

                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    chunk.addBit(entry.getKey(), new DataBit(entry.getValue()));
                }

                Position position = new Position(
                        chunk.getBit("x").asDouble(),
                        chunk.getBit("y").asDouble(),
                        chunk.getBit("z").asDouble(),
                        (float) chunk.getBit("yaw").asDouble(),
                        (float) chunk.getBit("pitch").asDouble(),
                        chunk.getBit("world").asString()
                );

                placedBlocks.add(position);
            }

            RenovationData renovationData = new RenovationData(houseId, placedBlocks);

            Option<Owner> ownerOption;

            if (houseDataChunk.getBit("owner").getValue() instanceof String) {
                ownerOption = Option.none();
            } else {
                DataChunk ownerChunk = houseDataChunk.getBit("owner").asChunk();
                ownerOption = Option.of(new Owner(
                        UUID.fromString(ownerChunk.getBit("uuid").asString()),
                        ownerChunk.getBit("name").asString(),
                        Instant.parse(ownerChunk.getBit("ownerSince").asString()
                        )));
            }

            Option<Rent> rentOption;

            if (houseDataChunk.getBit("rent").getValue() instanceof String) {
                rentOption = Option.none();
            } else {
                DataChunk rentChunk = houseDataChunk.getBit("rent").asChunk();

                Rent rent = new Rent(
                        houseId,
                        UUID.fromString(rentChunk.getBit("renter").asString()),
                        rentChunk.getBit("pricePerDay").asInt(),
                        Duration.parse(rentChunk.getBit("rentDuration").asString()),
                        Instant.parse(rentChunk.getBit("endOfRent").asString())
                );

                rentOption = Option.of(rent);
            }

            Option<RenovationRequest> renovationRequestOption;

            if (houseDataChunk.getBit("renovationRequest").getValue() instanceof String) {
                renovationRequestOption = Option.none();
            } else {
                DataChunk renovationRequestChunk = houseDataChunk.getBit("renovationRequest").asChunk();

                RenovationRequest renovationRequest = new RenovationRequest(
                        UUID.fromString(renovationRequestChunk.getBit("sender").asString()),
                        renovationRequestChunk.getBit("senderName").asString(),
                        houseId,
                        RenovationType.valueOf(renovationRequestChunk.getBit("renovationType").asString()),
                        renovationRequestChunk.getBit("renovationDays").asInt(),
                        renovationRequestChunk.getBit("request").asString()
                );

                renovationRequestOption = Option.of(renovationRequest);
            }

            Option<Renovation> currentRenovationOption;

            if (houseDataChunk.getBit("currentRenovation").getValue() instanceof String) {
                currentRenovationOption = Option.none();
            } else {
                DataChunk currentRenovationChunk = houseDataChunk.getBit("currentRenovation").asChunk();

                Renovation currentRenovation = new Renovation(
                        UUID.fromString(currentRenovationChunk.getBit("sender").asString()),
                        houseId,
                        RenovationType.valueOf(currentRenovationChunk.getBit("renovationType").asString()),
                        Instant.parse(currentRenovationChunk.getBit("endMoment").asString())
                );

                currentRenovationOption = Option.of(currentRenovation);
            }

            boolean hasAlarm = houseDataChunk.getBit("hasAlarm").asBoolean();
            String blockOfFlatsId = houseDataChunk.getBit("blockOfFlatsId").asString();

            House house = new House(
                    houseId,
                    members,
                    houseRegion,
                    buyPrice,
                    dailyRentalPrice,
                    renovationData,
                    blockOfFlatsId,
                    ownerOption,
                    rentOption,
                    renovationRequestOption,
                    currentRenovationOption,
                    hasAlarm
            );

            this.houses.put(houseId, house);
            this.dataChunk.updateBit(houseId, house);
        }
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
        return this.houses.containsKey(houseId);
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
        this.dataChunk.updateBit(house.getHouseId(), house);
    }

    public void removeHouse(String houseId) {
        this.houses.remove(houseId);
        this.dataChunk.removeBit(houseId);
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
                .filter(value -> value.getBlockOfFlatsId().equalsIgnoreCase(house.getBlockOfFlatsId()))
                .filter(value -> value.getOwner().isEmpty())
                .toList();
    }

    public Collection<House> getAllToBuyApartments() {
        return this.houses.values().stream()
                .filter(house -> house.getRegion().getType().equals(HouseType.APARTMENT))
                .filter(house -> house.getOwner().isEmpty())
                .toList();
    }

    public Collection<House> getAllHouses() {
        return Collections.unmodifiableCollection(this.houses.values());
    }

    public Collection<House> getBoughtHouses() {
        return this.houses.values().stream().filter(house -> house.getOwner().isPresent()).toList();
    }

}