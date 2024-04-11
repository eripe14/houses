package com.eripe14.houses.alert;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Set;
import java.util.UUID;

public class AlertController implements Listener {

    private final AlertService alertService;
    private final AlertHandler alertHandler;
    private final PluginConfiguration pluginConfiguration;

    public AlertController(AlertService alertService, AlertHandler alertHandler, PluginConfiguration pluginConfiguration) {
        this.alertService = alertService;
        this.alertHandler = alertHandler;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Set<Alert> allPlayerAlerts = this.alertService.getAllPlayerAlerts(uuid);

        if (allPlayerAlerts.isEmpty()) {
            return;
        }

        for (Alert allPlayerAlert : allPlayerAlerts) {
            this.alertHandler.sendAlertAfterTime(player, allPlayerAlert, this.pluginConfiguration.alertDelay);
        }
    }

}