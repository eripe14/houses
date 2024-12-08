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
import com.eripe14.houses.house.rent.Rent;
import com.eripe14.houses.house.rent.RentService;
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
    private final RentService rentService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration.House houseMessages;
    private final InventoryConfiguration inventoryConfiguration;

    public ChangeOwnerInviteImpl(
            House house,
            HouseMemberService houseMemberService,
            HouseInviteService houseInviteService,
            RentService rentService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            PluginConfiguration pluginConfiguration,
            MessageConfiguration.House houseMessages,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.house = house;
        this.houseMemberService = houseMemberService;
        this.houseInviteService = houseInviteService;
        this.rentService = rentService;
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
            formatter.register("{PREVIOUS_OWNER}", this.house.getOwner().get().getName());

            Option<HouseMember> memberOption = this.houseMemberService.getHouseMember(this.house, target.getUniqueId());

            if (memberOption.isEmpty()) {
                this.notificationAnnouncer.sendMessage(sender, this.houseMessages.playerMustMemberToBecomeOwner);
                return;
            }

            this.notificationAnnouncer.sendMessage(sender, this.houseMessages.inviteSent);

            Consumer<UUID> confirmAction = (confirmPlayer) -> {
                Option<HouseMember> targetHouseMemberOption = this.houseMemberService.getHouseMember(this.house, confirmPlayer);
                Option<Rent> rentOption = this.house.getRent();

                HouseMember targetHouseMember = targetHouseMemberOption.get();
                HouseMember senderHouseMember = new HouseMember(
                        sender.getName(),
                        sender.getUniqueId(),
                        this.house.getHouseId(),
                        this.pluginConfiguration.defaultHouseMemberPermission,
                        false
                );

                if (rentOption.isPresent()) {
                    Rent rent = rentOption.get();
                    rent.setRenter(targetHouseMember.getMemberUuid());

                    this.house.setRent(rent);
                    this.rentService.addRent(rent);
                }

                this.houseInviteService.removeInvite(sender.getUniqueId());
                this.houseInviteService.removeInvitedPlayer(target.getUniqueId());
                this.houseMemberService.changeOwner(this.house, targetHouseMember);
                this.houseMemberService.addDefaultMember(this.house, senderHouseMember);
                target.closeInventory();

                this.notificationAnnouncer.sendMessage(sender, this.houseMessages.changedOwner, formatter);
                this.notificationAnnouncer.sendMessage(target, this.houseMessages.becomeOwner, formatter);
            };

            Consumer<Player> cancelAction = (cancelPlayer) -> {
                target.closeInventory();

                this.houseInviteService.removeInvite(sender.getUniqueId());
                this.houseInviteService.removeInvitedPlayer(target.getUniqueId());
                this.notificationAnnouncer.sendMessage(sender, this.houseMessages.playerCancelledOwnerInvitation, formatter);
                this.notificationAnnouncer.sendMessage(target, this.houseMessages.cancelledOwnerInvitation, formatter);
            };

            this.confirmInventory.openInventory(
                    target,
                    this.inventoryConfiguration.confirm.confirmBecomeOwnerTitle,
                    Option.of(target.getUniqueId()),
                    this.inventoryConfiguration.confirm.changingOwnerAdditionalItem,
                    confirmAction,
                    cancelAction,
                    formatter
            );
        };
    }

}