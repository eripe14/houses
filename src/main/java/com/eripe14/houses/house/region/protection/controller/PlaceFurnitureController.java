package com.eripe14.houses.house.region.protection.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.hook.implementation.ItemsAdderHook;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.region.protection.ProtectionHandler;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

import java.util.UUID;

public class PlaceFurnitureController implements Listener {

    private final ItemsAdderHook itemsAdderHook;
    private final ProtectionHandler protectionHandler;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;

    public PlaceFurnitureController(
            ItemsAdderHook itemsAdderHook,
            ProtectionHandler protectionHandler,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration
    ) {
        this.itemsAdderHook = itemsAdderHook;
        this.protectionHandler = protectionHandler;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
    }

    @EventHandler
    public void onFurniturePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack itemInUse = player.getInventory().getItemInMainHand();

        Option<CustomStack> customStackOption = this.itemsAdderHook.getCustomStack(itemInUse);

        if  (customStackOption.isEmpty()) {
            return;
        }

        this.protectionHandler.canBuild(event, player, HouseMemberPermission.PLACE_FURNITURE).subscribe(result -> {
            if (!result.cancelEvent()) {
                return;
            }

            event.setCancelled(true);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToPlaceFurniture);
        });

    }

}