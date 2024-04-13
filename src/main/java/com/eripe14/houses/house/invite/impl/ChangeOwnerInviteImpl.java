package com.eripe14.houses.house.invite.impl;

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

public class ChangeOwnerInviteImpl implements Invite {

    private final House house;
    private final HouseMemberService houseMemberService;
    private final HouseInviteService houseInviteService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration.House houseMessages;

    public ChangeOwnerInviteImpl(
            House house,
            HouseMemberService houseMemberService,
            HouseInviteService houseInviteService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            PluginConfiguration pluginConfiguration,
            MessageConfiguration.House houseMessages
    ) {
        this.house = house;
        this.houseMemberService = houseMemberService;
        this.houseInviteService = houseInviteService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.pluginConfiguration = pluginConfiguration;
        this.houseMessages = houseMessages;
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

            Consumer<UUID> confirmAction = (confirmPlayer) -> {
                Option<HouseMember> targetHouseMemberOption = this.houseMemberService.getHouseMember(this.house, confirmPlayer);
                Option<HouseMember> senderHouseMemberOption = this.houseMemberService.getHouseMember(this.house, sender.getUniqueId());

                if (targetHouseMemberOption.isEmpty() || senderHouseMemberOption.isEmpty()) {
                    return;
                }

                HouseMember targetHouseMember = targetHouseMemberOption.get();
                HouseMember senderHouseMember = senderHouseMemberOption.get();

                this.houseMemberService.changeOwner(this.house, targetHouseMember);
                this.houseMemberService.addHouseMember(this.house, senderHouseMember);
                target.closeInventory();

                this.notificationAnnouncer.sendMessage(sender, this.houseMessages.changedOwner, formatter);
                this.notificationAnnouncer.sendMessage(target, this.houseMessages.becomeOwner, formatter);
            };

            Consumer<Player> cancelAction = (cancelPlayer) -> {
                target.closeInventory();

                this.houseInviteService.removeInvite(sender.getUniqueId());
                this.notificationAnnouncer.sendMessage(sender, this.houseMessages.playerCancelledOwnerInvitation, formatter);
                this.notificationAnnouncer.sendMessage(target, this.houseMessages.cancelledOwnerInvitation, formatter);
            };

            this.confirmInventory.openInventory(target, Option.of(target.getUniqueId()), confirmAction, cancelAction);
        };
    }

}