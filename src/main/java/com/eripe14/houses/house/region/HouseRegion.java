package com.eripe14.houses.house.region;

import com.eripe14.houses.house.furniture.HouseCustomFurniture;
import com.eripe14.houses.position.Position;
import com.eripe14.houses.position.PositionAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.Location;
import org.bukkit.World;
import pl.craftcityrp.developerapi.data.DataBit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HouseRegion extends DataBit {

    private final String houseId;
    private final String defaultSchematicName;
    private final World world;
    private final HouseType type;
    private final HouseDistrict district;
    private final ProtectedPolygonalRegion plot;
    private final ProtectedPolygonalRegion house;
    private final Set<Position> placedFurnitureLocations;
    private HouseCustomFurniture purchaseFurniture;
    private String latestSchematicName;

    public HouseRegion(
            String houseId,
            String defaultSchematicName,
            World world,
            HouseType type,
            HouseDistrict district,
            ProtectedPolygonalRegion plot,
            ProtectedPolygonalRegion house,
            HouseCustomFurniture purchaseFurniture
    ) {
        super(null);
        this.houseId = houseId;
        this.defaultSchematicName = defaultSchematicName;
        this.world = world;
        this.type = type;
        this.district = district;
        this.plot = plot;
        this.house = house;
        this.placedFurnitureLocations = new HashSet<>();
        this.purchaseFurniture = purchaseFurniture;
        this.latestSchematicName = "";
    }

    public HouseRegion(
            String houseId,
            String defaultSchematicName,
            World world,
            HouseType type,
            HouseDistrict district,
            ProtectedPolygonalRegion plot,
            ProtectedPolygonalRegion house,
            HouseCustomFurniture purchaseFurniture,
            String latestSchematicName
    ) {
        super(null);
        this.houseId = houseId;
        this.defaultSchematicName = defaultSchematicName;
        this.world = world;
        this.type = type;
        this.district = district;
        this.plot = plot;
        this.house = house;
        this.placedFurnitureLocations = new HashSet<>();
        this.purchaseFurniture = purchaseFurniture;
        this.latestSchematicName = latestSchematicName;
    }

    public HouseRegion(
            String houseId,
            String defaultSchematicName,
            World world,
            HouseType type,
            HouseDistrict district,
            ProtectedPolygonalRegion plot,
            ProtectedPolygonalRegion house,
            HouseCustomFurniture purchaseFurniture,
            Set<Position> placedFurnitureLocations,
            String latestSchematicName
    ) {
        super(null);
        this.houseId = houseId;
        this.defaultSchematicName = defaultSchematicName;
        this.world = world;
        this.type = type;
        this.district = district;
        this.plot = plot;
        this.house = house;
        this.purchaseFurniture = purchaseFurniture;
        this.placedFurnitureLocations = placedFurnitureLocations;
        this.latestSchematicName = latestSchematicName;
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

    public HouseCustomFurniture getPurchaseFurniture() {
        return this.purchaseFurniture;
    }

    public Set<Position> getPlacedFurnitureLocations() {
        return this.placedFurnitureLocations;
    }

    public String getLatestSchematicName() {
        return this.latestSchematicName;
    }

    public void addFurnitureLocation(Location location) {
        this.placedFurnitureLocations.add(PositionAdapter.convert(location));
    }

    public void removeFurnitureLocation(Location location) {
        this.placedFurnitureLocations.remove(PositionAdapter.convert(location));
    }

    public void setHouseCustomFurniture(HouseCustomFurniture houseCustomFurniture) {
        this.purchaseFurniture = houseCustomFurniture;
    }

    public void setLatestSchematicName(String latestSchematicName) {
        this.latestSchematicName = latestSchematicName;
    }

    @Override
    public Object asJson() {
        return Map.of(
                "houseId", this.houseId,
                "defaultSchematicName", this.defaultSchematicName,
                "world", this.world.getName(),
                "type", this.type,
                "district", this.district,
                "plot", this.plot.getId(),
                "house", this.house.getId(),
                "purchaseFurniture", this.purchaseFurniture.asJson(),
                "placedFurnitureLocations", this.placedFurnitureLocations,
                "latestSchematicName", this.latestSchematicName
        );
    }
}