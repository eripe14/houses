package com.eripe14.houses.house.region.protection.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.region.protection.ProtectionHandler;
import com.eripe14.houses.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BreakFurnitureController implements Listener {

    private final ProtectionHandler protectionHandler;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;

    public BreakFurnitureController(
            ProtectionHandler protectionHandler,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration
    ) {
        this.protectionHandler = protectionHandler;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
    }

    @EventHandler
    public void onFurnitureBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        this.protectionHandler.canActionWithBlock(event, player, HouseMemberPermission.PLACE_FURNITURE).subscribe(result -> {
            switch (result.result()) {
                case CANCEL_EVENT_WITH_MESSAGE -> {
                    event.setCancelled(true);
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToBreakFurniture);
                }
                case CANCEL_EVENT_WITHOUT_MESSAGE -> {
                    event.setCancelled(true);
                }
            }
        });
    }

}