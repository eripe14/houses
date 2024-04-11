package com.eripe14.houses.command.handler;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.permission.MissingPermissions;
import org.bukkit.command.CommandSender;

public class MissingPermissionsHandler implements dev.rollczi.litecommands.permission.MissingPermissionsHandler<CommandSender> {

    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;

    public MissingPermissionsHandler(MessageConfiguration messageConfiguration, NotificationAnnouncer notificationAnnouncer) {
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, MissingPermissions missingPermissions, ResultHandlerChain<CommandSender> chain) {
        CommandSender commandSender = invocation.sender();

        this.notificationAnnouncer.sendMessage(commandSender, this.messageConfiguration.wrongUsage.noPermission);
    }

}