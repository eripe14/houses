package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.UUID;

public class ChangePermissionsInventory extends Inventory {

    private final Scheduler scheduler;
    private final HouseMemberService houseMemberService;
    private final AlertHandler alertHandler;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;

    public ChangePermissionsInventory(
            Scheduler scheduler,
            HouseMemberService houseMemberService,
            AlertHandler alertHandler,
            NotificationAnnouncer notificationAnnouncer, MessageConfiguration messageConfiguration, InventoryConfiguration inventoryConfiguration
    ) {
        this.scheduler = scheduler;
        this.houseMemberService = houseMemberService;
        this.alertHandler = alertHandler;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(Player player, House house, HouseMember houseMember) {
        this.scheduler.async(() -> {
            InventoryConfiguration.ChangePermission changePermission = this.inventoryConfiguration.changePermission;

            Gui gui = Gui.gui()
                    .title(Legacy.title(changePermission.title))
                    .rows(changePermission.rows)
                    .disableAllInteractions()
                    .create();

            if (changePermission.fillEmptySlots) {
                gui.getFiller().fill(changePermission.filler.asGuiItem());
            }

            Formatter formatter = new Formatter();
            formatter.register("{OWNER}", house.getOwner().get().getName());
            formatter.register("{PLAYER}", houseMember.getMemberName());
            formatter.register(
                    "{STATUS_OPEN_DOOR}",
                    this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.OPEN_DOORS)
            );
            formatter.register(
                    "{STATUS_OPEN_CHEST}",
                    this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.OPEN_CHESTS)
            );
            formatter.register(
                    "{STATUS_PLACE_FURNITURE}",
                    this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.PLACE_FURNITURE)
            );

            this.setItem(gui, changePermission.changeOpenDoorPermissionItem, event -> {
                this.houseMemberService.changePermissionStatus(house, houseMember, HouseMemberPermission.OPEN_DOORS);

                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.changedPermission, formatter);
                this.sendPermissionAlert(houseMember, formatter);

                this.openInventory(player, house, houseMember);
            }, formatter);

            this.setItem(gui, changePermission.changeOpenChestPermissionItem, event -> {
                this.houseMemberService.changePermissionStatus(house, houseMember, HouseMemberPermission.OPEN_CHESTS);

                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.changedPermission, formatter);
                this.sendPermissionAlert(houseMember, formatter);

                this.openInventory(player, house, houseMember);
            }, formatter);

            this.setItem(gui, changePermission.changePlaceFurniturePermissionItem, event -> {
                this.houseMemberService.changePermissionStatus(house, houseMember, HouseMemberPermission.PLACE_FURNITURE);

                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.changedPermission, formatter);
                this.sendPermissionAlert(houseMember, formatter);

                this.openInventory(player, house, houseMember);
            }, formatter);

            this.setItem(gui, changePermission.closeInventoryItem, event -> gui.close(player));

            this.scheduler.sync(() -> gui.open(player));
        });
    }

    private void sendPermissionAlert(HouseMember houseMember, Formatter formatter) {
        UUID targetUuid = houseMember.getMemberUuid();
        Alert alert = new Alert(
                targetUuid,
                this.messageConfiguration.house.changedPermissionSubject,
                this.messageConfiguration.house.changedPermissionMessage,
                formatter
        );

        this.alertHandler.sendAlertIfPlayerNotOnline(targetUuid, alert);
    }

}