package com.eripe14.houses.house.region.protection.controller;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.region.protection.ProtectionHandler;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.house.renovation.RenovationData;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import panda.std.Option;

import java.util.Optional;

public class RenovationController implements Listener {

    private final ProtectionHandler protectionHandler;
    private final ProtectionService protectionService;
    private final HouseService houseService;
    private final PluginConfiguration pluginConfiguration;

    public RenovationController(
            ProtectionHandler protectionHandler,
            ProtectionService protectionService,
            HouseService houseService, PluginConfiguration pluginConfiguration
    ) {
        this.protectionHandler = protectionHandler;
        this.protectionService = protectionService;
        this.houseService = houseService;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockplace(BlockPlaceEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location blockLocation = block.getLocation();

        Optional<ProtectedRegion> firstRegion = this.protectionService.findFirstRegion(block.getLocation());

        if (firstRegion.isEmpty()) {
            return;
        }

        ProtectedRegion region = firstRegion.get();
        Option<House> houseOption = this.houseService.getHouse(region);

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        HouseRegion houseRegion = house.getRegion();

        this.protectionHandler.canPlaceBlock(event, player).subscribe(result -> {
            switch (result.result()) {
                case RENOVATE_COMPLETE -> {
                    if (houseRegion.getPlot().equals(region) || houseRegion.getHouse().equals(region)) {
                        event.setCancelled(false);
                    }
                }
                case RENOVATE_MAJOR -> {
                    Optional<ProtectedRegion> plotRegion = this.protectionService.findPlotRegion(blockLocation);
                    Optional<ProtectedRegion> houseRegionSecond = this.protectionService.findHouseRegion(blockLocation);

                    if (plotRegion.isEmpty()) {
                        event.setCancelled(true);
                        return;
                    }

                    if (houseRegionSecond.isPresent()) {
                        event.setCancelled(true);
                        return;
                    }

                    BlockVector3 minimumPoint = houseRegion.getPlot().getMinimumPoint();
                    int minimumPointBlockY = minimumPoint.getBlockY();
                    int maxHeight = minimumPointBlockY + this.pluginConfiguration.diggingHeight;

                    if (blockLocation.getY() > maxHeight) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setCancelled(false);
                }
                case RENOVATE_NON_INTERFERING -> {
                    RenovationData renovationData = house.getRenovationData();
                    renovationData.addLocation(blockLocation);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        Location blockLocation = block.getLocation();

        Optional<ProtectedRegion> firstRegion = this.protectionService.findFirstRegion(block.getLocation());

        if (firstRegion.isEmpty()) {
            return;
        }

        ProtectedRegion region = firstRegion.get();
        Option<House> houseOption = this.houseService.getHouse(region);

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        HouseRegion houseRegion = house.getRegion();

        this.protectionHandler.canBreakBlock(event, player).subscribe(result -> {
            switch (result.result()) {
                case RENOVATE_COMPLETE -> {
                    if (houseRegion.getPlot().equals(region) || houseRegion.getHouse().equals(region)) {
                        event.setCancelled(false);
                    }
                }
                case RENOVATE_MAJOR -> {
                    Optional<ProtectedRegion> plotRegion = this.protectionService.findPlotRegion(blockLocation);
                    Optional<ProtectedRegion> houseRegionSecond = this.protectionService.findHouseRegion(blockLocation);

                    if (plotRegion.isEmpty()) {
                        event.setCancelled(true);
                        return;
                    }

                    if (houseRegionSecond.isPresent()) {
                        event.setCancelled(true);
                        return;
                    }

                    BlockVector3 minimumPoint = houseRegion.getPlot().getMinimumPoint();
                    int minimumPointBlockY = minimumPoint.getBlockY();
                    int maxHeight = minimumPointBlockY + this.pluginConfiguration.diggingHeight;

                    if (blockLocation.getY() > maxHeight) {
                        event.setCancelled(true);
                        return;
                    }

                    event.setCancelled(false);
                }
                case RENOVATE_NON_INTERFERING -> {
                    RenovationData renovationData = house.getRenovationData();

                    if (renovationData.containsLocation(blockLocation)) {
                        event.setCancelled(false);
                        renovationData.removeLocation(blockLocation);
                        return;
                    }

                    event.setCancelled(true);
                }
            }
        });
    }

}