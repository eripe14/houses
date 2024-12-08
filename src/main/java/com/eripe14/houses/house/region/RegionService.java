package com.eripe14.houses.house.region;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface RegionService {

    void saveRegions(World world, FinalRegionResult result, String schematicFileName);

    void resetRegion(HouseRegion houseRegion);

    void killAllFurniture(HouseRegion houseRegion);

    CompletableFuture<FinalRegionResult> getRegions(Player player, String houseId, HouseDistrict houseDistrict, HouseType houseType);

    CompletableFuture<FinalRegionResult> getApartmentRegion(Player player, String houseId, ProtectedRegion blockOfFlatsRegion, HouseDistrict houseDistrict);

    RegionResult getRegion(Player player, String regionName);

    String getRegionName(String prefix, String houseId, HouseDistrict houseDistrict, HouseType houseType);

}