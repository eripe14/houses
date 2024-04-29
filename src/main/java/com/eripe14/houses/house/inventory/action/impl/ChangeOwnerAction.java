package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ConfirmInventory;
import com.eripe14.houses.house.invite.HouseInviteService;
import com.eripe14.houses.house.invite.impl.ChangeOwnerInviteImpl;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.rent.RentService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class ChangeOwnerAction implements InventoryClickAction {

    private final HouseMemberService houseMemberService;
    private final HouseInviteService houseInviteService;
    private final HouseService houseService;
    private final RentService rentService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration messageConfiguration;

    public ChangeOwnerAction(
            HouseMemberService houseMemberService,
            HouseInviteService houseInviteService,
            HouseService houseService,
            RentService rentService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            PluginConfiguration pluginConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.houseMemberService = houseMemberService;
        this.houseInviteService = houseInviteService;
        this.houseService = houseService;
        this.rentService = rentService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.pluginConfiguration = pluginConfiguration;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        return (inviteSenderUuid) -> {
            ChangeOwnerInviteImpl changeOwnerInvite = new ChangeOwnerInviteImpl(
                    house,
                    this.houseService,
                    this.houseMemberService,
                    this.houseInviteService,
                    this.rentService,
                    this.confirmInventory,
                    this.notificationAnnouncer,
                    this.pluginConfiguration,
                    this.messageConfiguration.house
            );

            this.houseInviteService.addInvite(inviteSenderUuid, changeOwnerInvite);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.createdOwnerInvite);

            gui.close(player);
        };
    }

}