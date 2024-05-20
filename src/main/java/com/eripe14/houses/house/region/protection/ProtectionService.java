package com.eripe14.houses.house.region.protection;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;

import java.util.Optional;

public class ProtectionService {

    private final WorldGuard worldGuard;

    public ProtectionService(WorldGuard worldGuard) {
        this.worldGuard = worldGuard;
    }

    public Optional<ProtectedRegion> findFirstRegion(org.bukkit.Location location) {
        Optional<ProtectedRegion> houseRegion = this.findHouseRegion(location);
        Optional<ProtectedRegion> plotRegion = this.findPlotRegion(location);

        if (houseRegion.isPresent()) {
            return houseRegion;
        } else if (plotRegion.isPresent()) {
            return plotRegion;
        }

        return Optional.empty();
    }

    public Optional<ProtectedRegion> findHouseRegion(org.bukkit.Location location) {
        ApplicableRegionSet locationRegions = this.getLocationRegions(location);

        return locationRegions.getRegions().stream().filter(region -> region.getId().startsWith("house_")).findFirst();
    }

    public Optional<ProtectedRegion> findPlotRegion(org.bukkit.Location location) {
        ApplicableRegionSet locationRegions = this.getLocationRegions(location);

        return locationRegions.getRegions().stream().filter(region -> region.getId().startsWith("plot_")).findFirst();
    }

    public org.bukkit.Location getCenterOfRegion(ProtectedRegion region) {
        BlockVector3 minimumPoint = region.getMinimumPoint();
        BlockVector3 maximumPoint = region.getMaximumPoint();

        double x = (double) (minimumPoint.getX() + maximumPoint.getX()) / 2;
        double y = minimumPoint.getY() + 1;
        double z = (double) (minimumPoint.getZ() + maximumPoint.getZ()) / 2;

        return new org.bukkit.Location(Bukkit.getWorld(PluginConfiguration.HOUSES_WORLD_NAME), x, y, z);
    }

    public ProtectedRegion getRegion(String id) {
        RegionContainer container = this.worldGuard.getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(Bukkit.getWorld(PluginConfiguration.HOUSES_WORLD_NAME)));

        return regionManager.getRegion(id);
    }

    public ApplicableRegionSet getLocationRegions(org.bukkit.Location location) {
        Location wgLocation = BukkitAdapter.adapt(location);

        RegionContainer container = this.worldGuard.getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.getApplicableRegions(wgLocation, RegionQuery.QueryOption.COMPUTE_PARENTS);
    }

}