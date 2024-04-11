package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ConfirmInventory;
import com.eripe14.houses.house.invite.HouseInviteService;
import com.eripe14.houses.house.invite.impl.HouseMemberInviteImpl;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class AddPlayerAction implements InventoryClickAction {

    private final HouseMemberService houseMemberService;
    private final HouseInviteService houseInviteService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public AddPlayerAction(
            HouseMemberService houseMemberService,
            HouseInviteService houseInviteService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.houseMemberService = houseMemberService;
        this.houseInviteService = houseInviteService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        return (inviteSenderUuid) -> {
            HouseMemberInviteImpl houseMemberInvite = new HouseMemberInviteImpl(
                    house,
                    this.houseMemberService,
                    this.houseInviteService,
                    this.confirmInventory,
                    this.notificationAnnouncer,
                    this.pluginConfiguration,
                    this.messageConfiguration.house
            );

            this.houseInviteService.addInvite(inviteSenderUuid, houseMemberInvite);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.createdInvite);

            gui.close(player);
        };
    }

}