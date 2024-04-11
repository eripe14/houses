package com.eripe14.houses.house.region.protection;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import java.util.Optional;

public class ProtectionService {

    private final WorldGuard worldGuard;

    public ProtectionService(WorldGuard worldGuard) {
        this.worldGuard = worldGuard;
    }

    public Optional<ProtectedRegion> findFirstRegion(org.bukkit.Location location) {
        ApplicableRegionSet locationRegions = this.getLocationRegions(location);

        return locationRegions.getRegions().stream().findFirst();
    }

    public ApplicableRegionSet getLocationRegions(org.bukkit.Location location) {
        Location wgLocation = BukkitAdapter.adapt(location);

        RegionContainer container = this.worldGuard.getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();

        return query.getApplicableRegions(wgLocation, RegionQuery.QueryOption.COMPUTE_PARENTS);
    }



}