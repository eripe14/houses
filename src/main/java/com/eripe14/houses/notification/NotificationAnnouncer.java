package com.eripe14.houses.notification;

import com.eripe14.houses.alert.AlertFormatter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

public final class NotificationAnnouncer {

    private final AudienceProvider audienceProvider;
    private final MiniMessage miniMessage;

    public NotificationAnnouncer(AudienceProvider audienceProvider, MiniMessage miniMessage) {
        this.audienceProvider = audienceProvider;
        this.miniMessage = miniMessage;
    }

    public void sendMessage(CommandSender sender, String message, Formatter... formatters) {
        Audience audience = this.audience(sender);

        for (Formatter formatter : formatters) {
            message = formatter.format(message);
        }

        audience.sendMessage(this.miniMessage.deserialize(message));
    }

    public void sendMessage(CommandSender sender, String message, AlertFormatter... formatters) {
        Audience audience = this.audience(sender);

        for (AlertFormatter formatter : formatters) {
            message = formatter.format(message);
        }

        audience.sendMessage(this.miniMessage.deserialize(message));
    }

    public void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        player.sendTitle(
                title,
                subtitle,
                fadeIn,
                stay,
                fadeOut
        );
    }

    public void sendMessage(CommandSender sender, String message) {
        Audience audience = this.audience(sender);

        audience.sendMessage(this.miniMessage.deserialize(message));
    }

    public void sendActionBar(Player player, String message, Formatter formatter) {
        Audience audience = this.audience(player);

        audience.sendActionBar(this.miniMessage.deserialize(formatter.format(message)));
    }

    public void sendActionBar(Player player, String message) {
        Audience audience = this.audience(player);

        audience.sendActionBar(this.miniMessage.deserialize(message));
    }

    public void sendMessage(Player player, Component component) {
        Audience audience = this.audience(player);

        audience.sendMessage(component);
    }

    private Audience audience(CommandSender sender) {
        if (sender instanceof Player player) {
            return this.audienceProvider.player(player.getUniqueId());
        }

        return this.audienceProvider.console();
    }

}