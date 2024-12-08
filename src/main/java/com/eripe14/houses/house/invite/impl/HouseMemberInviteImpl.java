package com.eripe14.houses.house.invite.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.impl.ConfirmInventory;
import com.eripe14.houses.house.invite.HouseInviteService;
import com.eripe14.houses.house.invite.Invite;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class HouseMemberInviteImpl implements Invite {

    private final House house;
    private final HouseMemberService houseMemberService;
    private final HouseInviteService houseInviteService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration.House houseMessages;
    private final InventoryConfiguration inventoryConfiguration;

    public HouseMemberInviteImpl(
            House house,
            HouseMemberService houseMemberService,
            HouseInviteService houseInviteService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            PluginConfiguration pluginConfiguration,
            MessageConfiguration.House houseMessages,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.house = house;
        this.houseMemberService = houseMemberService;
        this.houseInviteService = houseInviteService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.pluginConfiguration = pluginConfiguration;
        this.houseMessages = houseMessages;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    @Override
    public House getHouse() {
        return this.house;
    }

    @Override
    public BiConsumer<Player, Player> resultAction() {
        return (sender, target) -> {
            Formatter formatter = new Formatter();
            formatter.register("{PLAYER}", target.getName());
            formatter.register("{INVITER}", sender.getName());
            formatter.register("{HOUSE_ID}", this.house.getHouseId().replace("_", " "));

            Consumer<UUID> confirmAction = (confirmPlayer) -> {
                HouseMember houseMember = new HouseMember(target.getName(), target.getUniqueId(),
                        this.house.getHouseId(), this.pluginConfiguration.defaultHouseMemberPermission, false);

                Invite invite = this.houseInviteService.getInvite(sender.getUniqueId());

                if (invite == null) {
                    target.closeInventory();
                    this.notificationAnnouncer.sendMessage(target, this.houseMessages.inviteExpired, formatter);
                    return;
                }

                this.houseMemberService.addHouseMember(this.house, houseMember);
                this.houseInviteService.removeInvite(sender.getUniqueId());
                this.houseInviteService.removeInvitedPlayer(target.getUniqueId());
                target.closeInventory();

                this.notificationAnnouncer.sendMessage(sender, this.houseMessages.playerAddedToHouse, formatter);
                this.notificationAnnouncer.sendMessage(target, this.houseMessages.joinedHouse, formatter);
            };

            Consumer<Player> cancelAction = (cancelPlayer) -> {
                target.closeInventory();

                this.houseInviteService.removeInvite(sender.getUniqueId());
                this.houseInviteService.removeInvitedPlayer(target.getUniqueId());
                this.notificationAnnouncer.sendMessage(sender, this.houseMessages.playerCancelledInvitation, formatter);
                this.notificationAnnouncer.sendMessage(target, this.houseMessages.cancelledInvitation, formatter);
            };

            this.confirmInventory.openInventory(
                    target,
                    this.inventoryConfiguration.confirm.confirmJoinTitle,
                    Option.of(target.getUniqueId()),
                    this.inventoryConfiguration.confirm.houseMemberJoin,
                    confirmAction,
                    cancelAction,
                    formatter
            );
        };
    }

}