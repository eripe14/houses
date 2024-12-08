package com.eripe14.houses.house.invite;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.impl.ConfirmInventory;
import com.eripe14.houses.house.invite.impl.ChangeOwnerInviteImpl;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class HouseInviteController implements Listener {

    private final Server server;
    private final HouseInviteService houseInviteService;
    private final HouseMemberService houseMemberService;
    private final ConfirmInventory confirmInventory;
    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;
    private final PluginConfiguration pluginConfiguration;
    private final InventoryConfiguration inventoryConfiguration;

    public HouseInviteController(
            Server server,
            HouseInviteService houseInviteService,
            HouseMemberService houseMemberService,
            ConfirmInventory confirmInventory,
            MessageConfiguration messageConfiguration,
            NotificationAnnouncer notificationAnnouncer,
            PluginConfiguration pluginConfiguration,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.server = server;
        this.houseInviteService = houseInviteService;
        this.houseMemberService = houseMemberService;
        this.confirmInventory = confirmInventory;
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
        this.pluginConfiguration = pluginConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!(event.getRightClicked() instanceof Player clickedPlayer)) {
            return;
        }

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (!this.houseInviteService.hasSentInvite(player.getUniqueId())) {
            return;
        }

        if (clickedPlayer.hasMetadata("NPC")) {
            return;
        }

        Invite invite = this.houseInviteService.getInvite(uuid);
        House house = invite.getHouse();

        if (!this.houseMemberService.isCoOwner(house, player.getUniqueId()) && !house.getOwner().get().getUuid().equals(player.getUniqueId())) {
            return;
        }

        Formatter formatter = new Formatter();
        formatter.register("{HOUSE}", house.getHouseId());

        if (invite instanceof ChangeOwnerInviteImpl) {
            Consumer<UUID> acceptAction = (secondUuid) -> {
                this.houseInviteService.addInvitedPlayer(clickedPlayer.getUniqueId());
                invite.resultAction().accept(player, clickedPlayer);

                player.playSound(player, this.pluginConfiguration.inviteSentSound, 1f, 1f);
                player.closeInventory();
            };

            Consumer<Player> declineAction = (secondPlayer) -> {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.cancelledSendingInviteToChangeOwner);
                this.houseInviteService.removeInvite(uuid);
                player.closeInventory();
            };

            this.confirmInventory.openSkullInventory(
                    player,
                    this.inventoryConfiguration.confirm.confirmChangeOwnerTitle,
                    Option.of(clickedPlayer.getUniqueId()),
                    clickedPlayer,
                    this.inventoryConfiguration.confirm.skullAdditionalItem,
                    acceptAction,
                    declineAction,
                    formatter
            );
            return;
        }

        if (house.getOwner().get().getUuid().equals(clickedPlayer.getUniqueId())) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.cannotInviteOwner);
            return;
        }

        invite.resultAction().accept(player, clickedPlayer);
        this.houseInviteService.addInvitedPlayer(clickedPlayer.getUniqueId());

        player.playSound(player, this.pluginConfiguration.inviteSentSound, 1f, 1f);
        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.inviteSent);
    }

    @EventHandler
    public void onInviteExpire(InviteExpireEvent event) {
        UUID senderUuid = event.getSender();

        Player sender = this.server.getPlayer(senderUuid);

        if (sender == null) {
            return;
        }

        this.notificationAnnouncer.sendMessage(sender, this.messageConfiguration.house.inviteExpired);
    }

}