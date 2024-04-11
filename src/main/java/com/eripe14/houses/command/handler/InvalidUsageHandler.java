package com.eripe14.houses.command.handler;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.rollczi.litecommands.handler.result.ResultHandlerChain;
import dev.rollczi.litecommands.invalidusage.InvalidUsage;
import dev.rollczi.litecommands.invocation.Invocation;
import dev.rollczi.litecommands.schematic.Schematic;
import org.bukkit.command.CommandSender;
import panda.utilities.text.Formatter;

public class InvalidUsageHandler implements dev.rollczi.litecommands.invalidusage.InvalidUsageHandler<CommandSender> {

    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;

    public InvalidUsageHandler(MessageConfiguration messageConfiguration, NotificationAnnouncer notificationAnnouncer) {
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    @Override
    public void handle(Invocation<CommandSender> invocation, InvalidUsage<CommandSender> result, ResultHandlerChain<CommandSender> chain) {
        CommandSender commandSender = invocation.sender();
        Schematic schematic = result.getSchematic();

        MessageConfiguration.WrongUsage wrongUsage = this.messageConfiguration.wrongUsage;

        Formatter formatter = new Formatter();
        formatter.register("{COMMAND}", schematic.first());

        if (schematic.isOnlyFirst()) {
            this.notificationAnnouncer.sendMessage(commandSender, wrongUsage.invalidUsage, formatter);
            return;
        }

        this.notificationAnnouncer.sendMessage(commandSender, wrongUsage.invalidUsageHeader);

        for (String scheme : schematic.all()) {
            this.notificationAnnouncer.sendMessage(commandSender, wrongUsage.invalidUsageEntry + scheme);
        }

    }

}