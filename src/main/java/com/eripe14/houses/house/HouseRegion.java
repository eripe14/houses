package com.eripe14.houses.house;

import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import panda.std.Option;

public class HouseRegion {

    private final String houseId;
    private final HouseType type;
    private final HouseDistrict district;
    private final ProtectedPolygonalRegion plot;
    private final ProtectedPolygonalRegion house;
    private final Option<CustomFurniture> purchaseFurniture;

    public HouseRegion(String houseId, HouseType type, HouseDistrict district, ProtectedPolygonalRegion plot, ProtectedPolygonalRegion house, Option<CustomFurniture> purchaseFurniture) {
        this.houseId = houseId;
        this.type = type;
        this.district = district;
        this.plot = plot;
        this.house = house;
        this.purchaseFurniture = purchaseFurniture;
    }

    public String getHouseId() {
        return this.houseId;
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

    public Option<CustomFurniture> getPurchaseFurniture() {
        return this.purchaseFurniture;
    }

}