package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.rent.Rent;
import com.eripe14.houses.house.rent.RentService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class RentInventory extends Inventory {

    private final Scheduler scheduler;
    private final HouseService houseService;
    private final RentService rentService;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;
    private final PluginConfiguration pluginConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;

    public RentInventory(Scheduler scheduler, HouseService houseService, RentService rentService, MessageConfiguration messageConfiguration, InventoryConfiguration inventoryConfiguration, PluginConfiguration pluginConfiguration, NotificationAnnouncer notificationAnnouncer) {
        this.scheduler = scheduler;
        this.houseService = houseService;
        this.rentService = rentService;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
        this.pluginConfiguration = pluginConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            InventoryConfiguration.Rent rentInventory = this.inventoryConfiguration.rent;
            MessageConfiguration.House houseMessage = this.messageConfiguration.house;
            UUID uuid = player.getUniqueId();

            AtomicInteger days = new AtomicInteger();
            AtomicInteger price = new AtomicInteger();

            Formatter formatter = new Formatter();
            formatter.register("{DAYS}", days.get());
            formatter.register("{PRICE}", price.get());
            formatter.register("{MIN_RENT_TIME}", this.pluginConfiguration.minRentDays);

            Gui gui = Gui.gui()
                    .title(Legacy.title(rentInventory.title))
                    .rows(rentInventory.rows)
                    .disableAllInteractions()
                    .create();

            if (rentInventory.fillEmptySlots) {
                gui.getFiller().fill(rentInventory.filler.asGuiItem());
            }

            GuiAction<InventoryClickEvent> rentAction = (event) -> {
                if (days.get() <= 0) {
                    this.notificationAnnouncer.sendMessage(player, houseMessage.requiredRentalTime);
                    return;
                }

                Rent rent = this.rentService.createRent(uuid, house, house.getDailyRentalPrice(), days.get());

                this.rentService.addRent(rent);
                this.houseService.rentHouse(house, rent);

                this.notificationAnnouncer.sendMessage(player, houseMessage.rentedHouse, formatter);
            };

            this.setItem(gui, rentInventory.rentItem, rentAction, formatter);

            this.setItem(gui, rentInventory.addDayItem, (event) -> {
                days.getAndIncrement();
                price.set(house.getDailyRentalPrice() * days.get());

                this.setItem(gui, rentInventory.rentItem, rentAction, formatter);
                gui.update();
            });

            this.setItem(gui, rentInventory.removeDayItem, (event) -> {
                if (days.get() == this.pluginConfiguration.minRentDays) {
                    this.notificationAnnouncer.sendMessage(player, houseMessage.requiredRentalTime, formatter);
                    return;
                }

                days.getAndDecrement();
                price.set(house.getDailyRentalPrice() * days.get());

                this.setItem(gui, rentInventory.rentItem, rentAction, formatter);
                gui.update();
            });

            this.setItem(gui, rentInventory.closeInventoryItem, (event) -> gui.close(player));

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}