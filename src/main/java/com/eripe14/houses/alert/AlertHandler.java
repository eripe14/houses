package com.eripe14.houses.alert;

import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.UUID;

public interface AlertHandler {

    void sendAlert(Player player, Alert alert);

    void sendAlertAfterTime(Player player, Alert alert, Duration duration);

    void sendAlertIfPlayerNotOnline(UUID uuid, Alert alert);

}