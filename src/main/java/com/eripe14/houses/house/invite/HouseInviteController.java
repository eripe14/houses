package com.eripe14.houses.house.invite;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.notification.NotificationAnnouncer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.UUID;

public class HouseInviteController implements Listener {

    private final Server server;
    private final HouseInviteService houseInviteService;
    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;

    public HouseInviteController(
            Server server, HouseInviteService houseInviteService,
            MessageConfiguration messageConfiguration,
            NotificationAnnouncer notificationAnnouncer
    ) {
        this.server = server;
        this.houseInviteService = houseInviteService;
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!(event.getRightClicked() instanceof Player clickedPlayer)) {
            return;
        }

        if (!this.houseInviteService.hasSentInvite(player.getUniqueId())) {
            return;
        }

        Invite invite = this.houseInviteService.getInvite(uuid);
        invite.resultAction().accept(player, clickedPlayer);
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