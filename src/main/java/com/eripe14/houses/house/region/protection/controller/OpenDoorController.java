package com.eripe14.houses.house.region.protection.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.region.protection.ProtectionCause;
import com.eripe14.houses.house.region.protection.ProtectionHandler;
import com.eripe14.houses.notification.NotificationAnnouncer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class OpenDoorController implements Listener {

    private final ProtectionHandler protectionHandler;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public OpenDoorController(
            ProtectionHandler protectionHandler,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.protectionHandler = protectionHandler;
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        List<Material> doors = this.pluginConfiguration.doors;

        this.protectionHandler.canInteractWithBlocks(event, doors, player, HouseMemberPermission.OPEN_DOORS).subscribe(result -> {
            if (result.result() != ProtectionCause.CANCEL_EVENT_WITH_MESSAGE) {
                return;
            }

            event.setCancelled(true);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToOpenDoors);
        });
    }

}