package com.eripe14.houses.house.renovation;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.inventory.impl.ListOfHousesInventory;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RenovationInventoryController implements Listener {

    private final ListOfHousesInventory listOfHousesInventory;
    private final PluginConfiguration pluginConfiguration;

    public RenovationInventoryController(ListOfHousesInventory listOfHousesInventory, PluginConfiguration pluginConfiguration) {
        this.listOfHousesInventory = listOfHousesInventory;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        String namespacedID = event.getNamespacedID();

        if (!namespacedID.equals(this.pluginConfiguration.renovationNamespacedId)) {
            return;
        }

        this.listOfHousesInventory.openInventory(player);
    }
}