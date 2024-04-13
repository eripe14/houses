package com.eripe14.houses.house.purchase;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.impl.RentInventory;
import com.eripe14.houses.house.inventory.impl.SelectPurchaseInventory;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import panda.std.Option;

public class PurchaseFurnitureInteractController implements Listener {

    private final SelectPurchaseInventory selectPurchaseInventory;
    private final RentInventory rentInventory;
    private final HouseService houseService;
    private final PluginConfiguration pluginConfiguration;

    public PurchaseFurnitureInteractController(
            SelectPurchaseInventory selectPurchaseInventory,
            RentInventory rentInventory,
            HouseService houseService,
            PluginConfiguration pluginConfiguration
    ) {
        this.selectPurchaseInventory = selectPurchaseInventory;
        this.rentInventory = rentInventory;
        this.houseService = houseService;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CustomFurniture furniture = event.getFurniture();
        Entity bukkitEntity = event.getBukkitEntity();
        Location location = bukkitEntity.getLocation();

        if (furniture == null) {
            return;
        }

        if (!furniture.getNamespacedID().equalsIgnoreCase(this.pluginConfiguration.itemsAdderPurchaseNamespacedId)) {
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(location);

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();

        if (house.getBuyPrice() == 0) {
            this.rentInventory.openInventory(player, house);
            return;
        }

        this.selectPurchaseInventory.openInventory(player, house);
    }

}