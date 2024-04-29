package com.eripe14.houses.house.region;

import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashSet;
import java.util.Set;

public class HouseRegion {

    private final String houseId;
    private final String defaultSchematicName;
    private final World world;
    private final HouseType type;
    private final HouseDistrict district;
    private final ProtectedPolygonalRegion plot;
    private final ProtectedPolygonalRegion house;
    private final Location purchaseFurnitureLocation;
    private final CustomFurniture purchaseFurniture;
    private final Set<Location> placedFurnitureLocations;

    public HouseRegion(
            String houseId,
            String defaultSchematicName, World world,
            HouseType type,
            HouseDistrict district,
            ProtectedPolygonalRegion plot,
            ProtectedPolygonalRegion house,
            Location purchaseFurnitureLocation,
            CustomFurniture purchaseFurniture
    ) {
        this.houseId = houseId;
        this.defaultSchematicName = defaultSchematicName;
        this.world = world;
        this.type = type;
        this.district = district;
        this.plot = plot;
        this.house = house;
        this.purchaseFurnitureLocation = purchaseFurnitureLocation;
        this.purchaseFurniture = purchaseFurniture;
        this.placedFurnitureLocations = new HashSet<>();
    }

    public String getHouseId() {
        return this.houseId;
    }

    public String getDefaultSchematicName() {
        return this.defaultSchematicName;
    }

    public World getWorld() {
        return this.world;
    }

    public HouseType getType() {
        return this.type;
    }

    public HouseDistrict getDistrict() {
        return this.district;
    }

    public ProtectedPolygonalRegion getPlot() {
        return this.plot;
    }

    public ProtectedPolygonalRegion getHouse() {
        return this.house;
    }

    public Location getPurchaseFurnitureLocation() {
        return this.purchaseFurnitureLocation;
    }

    public CustomFurniture getPurchaseFurniture() {
        return this.purchaseFurniture;
    }

    public Set<Location> getPlacedFurnitureLocations() {
        return this.placedFurnitureLocations;
    }

    public void addFurnitureLocation(Location location) {
        this.placedFurnitureLocations.add(location);
    }

    public void removeFurniture(Location location) {
        this.placedFurnitureLocations.remove(location);
    }

}