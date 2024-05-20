package com.eripe14.houses.house.renovation;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.inventory.impl.RenovationApplicationsInventory;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RenovationApplicationsController implements Listener {

    private final RenovationApplicationsInventory renovationApplicationsInventory;
    private final PluginConfiguration pluginConfiguration;

    public RenovationApplicationsController(
            RenovationApplicationsInventory renovationApplicationsInventory,
            PluginConfiguration pluginConfiguration
    ) {
        this.renovationApplicationsInventory = renovationApplicationsInventory;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CustomFurniture furniture = event.getFurniture();
        String namespacedID = event.getNamespacedID();

        if (furniture == null) {
            return;
        }

        if (namespacedID == null || namespacedID.isEmpty()) {
            return;
        }

        if (!namespacedID.equalsIgnoreCase(this.pluginConfiguration.renovationApplicationsNamespacedId)) {
            return;
        }

        if (!player.hasPermission(this.pluginConfiguration.renovationApplicationsPermission)) {
            return;
        }

        this.renovationApplicationsInventory.openInventory(player);
    }

}