package com.eripe14.houses.alert;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.util.UUID;

public class AlertHandlerImpl implements AlertHandler {

    private final Server server;
    private final AlertService alertService;
    private final Scheduler scheduler;
    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;

    public AlertHandlerImpl(Server server, AlertService alertService, Scheduler scheduler, MessageConfiguration messageConfiguration, NotificationAnnouncer notificationAnnouncer) {
        this.server = server;
        this.alertService = alertService;
        this.scheduler = scheduler;
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @Override
    public void sendAlert(Player player, Alert alert) {
        Formatter alertFormatter = new Formatter();
        alertFormatter.register("{SUBJECT}", alert.subject());
        alertFormatter.register("{MESSAGE}", alert.message());

        for (String alertMessage : this.messageConfiguration.alert.alertMessage) {
            this.notificationAnnouncer.sendMessage(player, alertMessage, alertFormatter, alert.formatter());
        }

        this.alertService.removeAlert(alert);
    }


    @Override
    public void sendAlertAfterTime(Player player, Alert alert, Duration duration) {
        this.scheduler.laterAsync(() -> this.sendAlert(player, alert), duration);
    }

    @Override
    public void sendAlertIfPlayerNotOnline(UUID uuid, Alert alert) {
        Player player = this.server.getPlayer(uuid);

        if (player == null) {
            this.alertService.addAlert(alert);
            return;
        }

        this.alertService.addAlert(alert);
        this.sendAlert(player, alert);
    }

}