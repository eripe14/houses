package com.eripe14.houses.house;

import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import dev.lone.itemsadder.api.CustomFurniture;

public class HouseRegion {

    private final String houseId;
    private final String defaultSchematicName;
    private final HouseType type;
    private final HouseDistrict district;
    private final ProtectedPolygonalRegion plot;
    private final ProtectedPolygonalRegion house;
    private final CustomFurniture purchaseFurniture;

    public HouseRegion(
            String houseId,
            String defaultSchematicName,
            HouseType type,
            HouseDistrict district,
            ProtectedPolygonalRegion plot,
            ProtectedPolygonalRegion house,
            CustomFurniture purchaseFurniture
    ) {
        this.houseId = houseId;
        this.defaultSchematicName = defaultSchematicName;
        this.type = type;
        this.district = district;
        this.plot = plot;
        this.house = house;
        this.purchaseFurniture = purchaseFurniture;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public String getDefaultSchematicName() {
        return this.defaultSchematicName;
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

    public CustomFurniture getPurchaseFurniture() {
        return this.purchaseFurniture;
    }

}