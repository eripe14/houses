package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ListOfCoOwnersInventory;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class RemoveCoOwnerAction implements InventoryClickAction {

    private final HouseMemberService houseMemberService;
    private final AlertHandler alertHandler;
    private final ListOfCoOwnersInventory listOfCoOwnersInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;

    public RemoveCoOwnerAction(
            HouseMemberService houseMemberService,
            AlertHandler alertHandler,
            ListOfCoOwnersInventory listOfCoOwnersInventory,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.houseMemberService = houseMemberService;
        this.alertHandler = alertHandler;
        this.listOfCoOwnersInventory = listOfCoOwnersInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        Consumer<UUID> removeCoOwnerConsumer = (coOwnerUuid) -> {
            Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, coOwnerUuid);

            if (houseMemberOption.isEmpty()) {
                return;
            }

            HouseMember houseMember = houseMemberOption.get();

            Formatter formatter = new Formatter();
            formatter.register("{OWNER}", house.getOwner().get().getName());
            formatter.register("{PLAYER}", houseMember.getMemberName());

            Alert alert = new Alert(coOwnerUuid, this.messageConfiguration.house.removedCoOwnerSubject, this.messageConfiguration.house.removedCoOwnerMessage, formatter);

            this.houseMemberService.removeCoOwner(house, houseMember);

            this.alertHandler.sendAlertIfPlayerNotOnline(coOwnerUuid, alert);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.removedCoOwner, formatter);
            gui.close(player);
        };

        return (playerAction) -> {
            this.listOfCoOwnersInventory.openInventory(
                    player,
                    house,
                    this.inventoryConfiguration.coOwner.removeCoOwnerTitle,
                    removeCoOwnerConsumer
            );
        };
    }

}