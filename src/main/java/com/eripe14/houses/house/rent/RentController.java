package com.eripe14.houses.house.rent;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.util.DurationUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public class RentController implements Listener {

    private final RentService rentService;
    private final PluginConfiguration pluginConfiguration;
    private final AlertHandler alertHandler;
    private final MessageConfiguration messageConfiguration;

    public RentController(
            RentService rentService,
            PluginConfiguration pluginConfiguration,
            AlertHandler alertHandler,
            MessageConfiguration messageConfiguration
    ) {
        this.rentService = rentService;
        this.pluginConfiguration = pluginConfiguration;
        this.alertHandler = alertHandler;
        this.messageConfiguration = messageConfiguration;
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

        AlertFormatter formatter = new AlertFormatter();
        formatter.register("{TIME}", DurationUtil.format(this.pluginConfiguration.timeBeforeRentEndToReminder));

        Alert alert = new Alert(
                uuid,
                this.messageConfiguration.rent.rentEndSoonSubject,
                this.messageConfiguration.rent.rentEndSoonMessage,
                formatter
        );

        this.alertHandler.sendAlertAfterTime(player, alert, this.pluginConfiguration.alertDelay);
    }

}