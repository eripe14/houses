package com.eripe14.houses.house.region.protection.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.region.protection.ProtectionHandler;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.position.PositionAdapter;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import panda.std.Option;

import java.util.Optional;

public class PlaceFurnitureController implements Listener {

    private final ProtectionHandler protectionHandler;
    private final ProtectionService protectionService;
    private final HouseService houseService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public PlaceFurnitureController(
            ProtectionHandler protectionHandler,
            ProtectionService protectionService,
            HouseService houseService,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.protectionHandler = protectionHandler;
        this.protectionService = protectionService;
        this.houseService = houseService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFurniturePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        this.protectionHandler.canPlaceBlock(event, player).subscribe(result -> {
            switch (result.result()) {
                case CANCEL_EVENT_WITH_MESSAGE -> {
                    event.setCancelled(true);
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToPlaceFurniture);
                }
                case CANCEL_EVENT_WITHOUT_MESSAGE -> {
                    event.setCancelled(true);
                }
                case NOT_CANCEL_EVENT_WITHOUT_MESSAGE -> {
                    event.setCancelled(false);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onFurniturePlace(FurniturePlaceSuccessEvent event) {
        CustomFurniture furniture = event.getFurniture();

        if (furniture == null) {
            return;
        }

        Entity entity = furniture.getArmorstand();

        if (entity == null) {
            return;
        }

        Location location = entity.getLocation();

        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(location);

        if (regionOption.isEmpty()) {
            return;
        }

        ProtectedRegion region = regionOption.get();
        Option<House> houseOption = this.houseService.getHouse(region);

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();

        if (house.getRegion().getPlacedFurnitureLocations().contains(PositionAdapter.convert(location))) {
            return;
        }

        house.getRegion().addFurnitureLocation(location);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        CustomFurniture furniture = event.getFurniture();

        if (furniture == null) {
            return;
        }

        Entity entity = furniture.getArmorstand();

        if (entity == null) {
            return;
        }

        Location location = entity.getLocation();

        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(location);

        if (regionOption.isEmpty()) {
            return;
        }

        ProtectedRegion region = regionOption.get();
        Option<House> houseOption = this.houseService.getHouse(region);

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();

        if (!house.getRegion().getPlacedFurnitureLocations().contains(PositionAdapter.convert(location))) {
            return;
        }

        house.getRegion().removeFurnitureLocation(location);
    }
}