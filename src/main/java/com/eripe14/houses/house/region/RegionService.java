package com.eripe14.houses.house.region;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface RegionService {

    void saveRegions(World world, FinalRegionResult result, String schematicFileName);

    CompletableFuture<FinalRegionResult> getRegions(Player player, String houseId, HouseDistrict houseDistrict, HouseType houseType);

    RegionResult getRegion(Player player, String regionName);

    String getRegionName(String prefix, String houseId, HouseDistrict houseDistrict, HouseType houseType);

}