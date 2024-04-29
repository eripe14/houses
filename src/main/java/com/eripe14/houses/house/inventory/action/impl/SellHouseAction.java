package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.purchase.HouseSellService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class SellHouseAction implements InventoryClickAction {

    private final HouseSellService houseSellService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;

    public SellHouseAction(HouseSellService houseSellService, NotificationAnnouncer notificationAnnouncer, MessageConfiguration messageConfiguration) {
        this.houseSellService = houseSellService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        return (uuid) -> {
            Formatter formatter = new Formatter();
            formatter.register("{SELL_PRICE}", this.houseSellService.sellHouse(player, house));

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.soldHouse, formatter);
            gui.close(player);
        };
    }

}