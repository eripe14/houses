package com.eripe14.houses.house.alarm;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import panda.std.Option;

import java.util.Optional;

public class AlarmController implements Listener {

    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final PluginConfiguration pluginConfiguration;

    public AlarmController(HouseService houseService, ProtectionService protectionService, PluginConfiguration pluginConfiguration) {
        this.houseService = houseService;
        this.protectionService = protectionService;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onFurniturePlace(FurniturePlaceSuccessEvent event) {
        Player player = event.getPlayer();

        if (!event.getNamespacedID().equalsIgnoreCase(this.pluginConfiguration.alarmNamespacedId)) {
            return;
        }

        CustomFurniture furniture = event.getFurniture();
        Entity armorstand = furniture.getArmorstand();

        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(armorstand.getLocation());

        if (regionOption.isEmpty()) {
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(regionOption.get());

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        house.setHasAlarm(true);
        this.houseService.addHouse(house);
    }

    @EventHandler
    public void onFurniturePlace(FurnitureBreakEvent event) {
        Player player = event.getPlayer();

        if (!event.getNamespacedID().equalsIgnoreCase(this.pluginConfiguration.alarmNamespacedId)) {
            return;
        }

        CustomFurniture furniture = event.getFurniture();
        Entity armorstand = furniture.getArmorstand();

        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(armorstand.getLocation());

        if (regionOption.isEmpty()) {
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(regionOption.get());

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        house.setHasAlarm(false);
        this.houseService.addHouse(house);
    }

}