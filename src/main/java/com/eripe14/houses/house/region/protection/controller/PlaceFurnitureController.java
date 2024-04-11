package com.eripe14.houses.house.region.protection.controller;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurniturePlaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlaceFurnitureController implements Listener {


    @EventHandler
    public void onFurniturePlace(FurniturePlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack itemInUse = player.getInventory().getItemInMainHand();

        CustomStack instance = CustomFurniture.getInstance(event.getNamespacedID());

        if (instance != null) {
            if (instance instanceof CustomFurniture customFurniture) {
                player.sendMessage("You placed a " + customFurniture.getDisplayName());

            }
        } else {
            player.sendMessage("You placed a furniture");
            return;
        }

    }

}