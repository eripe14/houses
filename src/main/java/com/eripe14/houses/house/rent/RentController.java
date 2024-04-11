package com.eripe14.houses.house.rent;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.util.DurationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public class RentController implements Listener {

    private final RentService rentService;
    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;

    public RentController(RentService rentService, PluginConfiguration pluginConfiguration, MessageConfiguration messageConfiguration, NotificationAnnouncer notificationAnnouncer) {
        this.rentService = rentService;
        this.pluginConfiguration = pluginConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Optional<Rent> playersRent = this.rentService.getPlayersRent(uuid);

        if (playersRent.isEmpty()) {
            return;
        }

        Rent rent = playersRent.get();
        Duration timeBeforeRentEnd = this.pluginConfiguration.timeBeforeRentEndToReminder;

        if (!this.rentService.isTimeToRemind(rent, timeBeforeRentEnd)) {
            return;
        }

        Formatter formatter = new Formatter();
        formatter.register("{DAYS}", DurationUtil.format(this.pluginConfiguration.timeBeforeRentEndToReminder));

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.rent.rentEndSoon);
    }

}