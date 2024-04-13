package com.eripe14.houses.house.controller;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.impl.PurchasedPanelInventory;
import com.eripe14.houses.house.inventory.impl.RentedPanelInventory;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import panda.std.Option;

import java.util.Optional;

public class HousePanelController implements Listener {

    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final RentedPanelInventory rentedPanelInventory;
    private final PurchasedPanelInventory purchasePanelInventory;
    private final PluginConfiguration pluginConfiguration;

    public HousePanelController(HouseService houseService, ProtectionService protectionService, RentedPanelInventory rentedPanelInventory, PurchasedPanelInventory purchasePanelInventory, PluginConfiguration pluginConfiguration) {
        this.houseService = houseService;
        this.protectionService = protectionService;
        this.rentedPanelInventory = rentedPanelInventory;
        this.purchasePanelInventory = purchasePanelInventory;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        String namespacedID = event.getNamespacedID();
        CustomFurniture furniture = event.getFurniture();
        Entity armorstand = furniture.getArmorstand();

        if (namespacedID == null) {
            return;
        }

        if (armorstand == null) {
            return;
        }

        Location location = armorstand.getLocation();

        if (!namespacedID.equalsIgnoreCase(this.pluginConfiguration.itemsAdderHousePanelNamespacedId)) {
            return;
        }

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

        if (house.getRent().isEmpty()) {
            this.purchasePanelInventory.openInventory(player, house);
            return;
        }

        this.rentedPanelInventory.openInventory(player, house);
    }

}