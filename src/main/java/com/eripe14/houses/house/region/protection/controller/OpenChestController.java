package com.eripe14.houses.house.region.protection.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.region.protection.ProtectionHandler;
import com.eripe14.houses.notification.NotificationAnnouncer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class OpenChestController implements Listener {

    private final ProtectionHandler protectionHandler;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public OpenChestController(
            ProtectionHandler protectionHandler,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.protectionHandler = protectionHandler;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        List<Material> chests = this.pluginConfiguration.chests;

        this.protectionHandler.canInteractWithBlocks(event, chests, player, HouseMemberPermission.OPEN_CHESTS).subscribe(result -> {
            if (!result.cancelEvent()) {
                return;
            }

            event.setCancelled(true);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToOpenChests);
        });
    }

}