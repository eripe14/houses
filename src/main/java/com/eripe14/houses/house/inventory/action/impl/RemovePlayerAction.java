package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.alert.Alert;
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
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class RemovePlayerAction implements InventoryClickAction {

    private final HouseMemberService houseMemberService;
    private final AlertHandler alertHandler;
    private final ListOfHouseMembersInventory listOfHouseMembersInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;

    public RemovePlayerAction(
            HouseMemberService houseMemberService,
            AlertHandler alertHandler,
            ListOfHouseMembersInventory listOfHouseMembersInventory,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.houseMemberService = houseMemberService;
        this.alertHandler = alertHandler;
        this.listOfHouseMembersInventory = listOfHouseMembersInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        Consumer<UUID> removeConsumer = (uuidToRemove) -> {
            Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuidToRemove);

            if (houseMemberOption.isEmpty()) {
                return;
            }

            HouseMember houseMember = houseMemberOption.get();

            Formatter formatter = new Formatter();
            formatter.register("{OWNER}", house.getOwner().get().getName());
            formatter.register("{PLAYER}", houseMember.getMemberName());

            Alert alert = new Alert(
                    uuidToRemove,
                    this.messageConfiguration.house.removedFromHouseSubject,
                    this.messageConfiguration.house.removedFromHouseMessage,
                    formatter
            );

            this.houseMemberService.removeHouseMember(house, uuidToRemove);

            this.alertHandler.sendAlertIfPlayerNotOnline(uuidToRemove, alert);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.playerRemovedFromHouse);
            gui.close(player);
        };

        return (playerAction) -> {
            this.listOfHouseMembersInventory.openInventory(
                    player,
                    house,
                    this.inventoryConfiguration.removePlayer.title,
                    removeConsumer
            );
        };
    }

}