package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ListOfHouseMembersInventory;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.UUID;
import java.util.function.Consumer;

public class AddCoOwnerAction implements InventoryClickAction {

    private final HouseMemberService houseMemberService;
    private final AlertHandler alertHandler;
    private final ListOfHouseMembersInventory listOfHouseMembersInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;

    public AddCoOwnerAction(HouseMemberService houseMemberService, AlertHandler alertHandler,
                            ListOfHouseMembersInventory listOfHouseMembersInventory,
                            NotificationAnnouncer notificationAnnouncer, MessageConfiguration messageConfiguration,
                            InventoryConfiguration inventoryConfiguration) {
        this.houseMemberService = houseMemberService;
        this.alertHandler = alertHandler;
        this.listOfHouseMembersInventory = listOfHouseMembersInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        Consumer<UUID> addCoOwnerConsumer = (coOwnerUuid) -> {
            Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, coOwnerUuid);

            if (houseMemberOption.isEmpty()) {
                return;
            }

            HouseMember houseMember = houseMemberOption.get();

            AlertFormatter formatter = new AlertFormatter();
            formatter.register("{OWNER}", house.getOwner().get().getName());
            formatter.register("{PLAYER}", houseMember.getMemberName());

            Alert alert = new Alert(
                    coOwnerUuid,
                    this.messageConfiguration.house.addedCoOwnerSubject,
                    this.messageConfiguration.house.addedCoOwnerMessage,
                    formatter
            );

            this.houseMemberService.addCoOwner(house, houseMember);

            this.alertHandler.sendAlertIfPlayerNotOnline(coOwnerUuid, alert);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.addedCoOwner, formatter);

            gui.close(player);
        };

        return (playerAction) -> {
            this.listOfHouseMembersInventory.openInventory(
                    player,
                    house,
                    this.inventoryConfiguration.coOwner.addCoOwnerTitle,
                    addCoOwnerConsumer
            );
        };
    }

}