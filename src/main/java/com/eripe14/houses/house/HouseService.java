package com.eripe14.houses.house;

import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.FinalRegionResult;
import com.eripe14.houses.house.region.HouseDistrict;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.house.rent.Rent;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HouseService {

    private final Map<String, House> houses = new HashMap<>();

    public House createHouse(
            String houseId,
            String defaultSchematicName,
            HouseDistrict district,
            HouseType type,
            FinalRegionResult result,
            Location purchaseFurnitureLocation,
            CustomFurniture purchaseFurniture,
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
                purchaseFurnitureLocation,
                purchaseFurniture
        );

        if (buyPrice != 0) {
            return new House(houseId, Option.none(), region, buyPrice, dailyRentalPrice);
        }

        return new House(houseId, Option.none(), region, dailyRentalPrice);
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

    public void resetHouse(House house) {
        house.setRent(null);
        house.setOwner(null);
        house.getMembers().clear();

        this.houses.put(house.getHouseId(), house);
    }

    public void addHouse(House house) {
        this.houses.put(house.getHouseId(), house);
    }

    public void removeHouse(String houseId) {
        this.houses.remove(houseId);
    }

    public Option<House> getHouse(String houseId) {
        return Option.of(this.houses.get(houseId));
    }

    public Option<House> getHouse(Location purchaseFurnitureLocation) {
        for (House value : this.houses.values()) {
            HouseRegion region = value.getRegion();

            CustomFurniture customFurniture = region.getPurchaseFurniture();
            Entity entity = customFurniture.getArmorstand();

            if (entity == null) {
                continue;
            }

            Location entityLocation = entity.getLocation();

            if (!entityLocation.equals(purchaseFurnitureLocation)) {
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

            return Option.none();
        }

        return Option.none();
    }

    public Collection<House> getAllHouses() {
        return Collections.unmodifiableCollection(this.houses.values());
    }

}