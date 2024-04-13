package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.rent.Rent;
import com.eripe14.houses.house.rent.RentService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.purchase.PurchaseService;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ExtendRentInventory extends Inventory {

    private final Scheduler scheduler;
    private final PurchaseService purchaseService;
    private final RentService rentService;
    private final HouseService houseService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final InventoryConfiguration inventoryConfiguration;
    private final MessageConfiguration messageConfiguration;

    public ExtendRentInventory(
            Scheduler scheduler,
            PurchaseService purchaseService,
            RentService rentService,
            HouseService houseService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            InventoryConfiguration inventoryConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.scheduler = scheduler;
        this.purchaseService = purchaseService;
        this.rentService = rentService;
        this.houseService = houseService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.inventoryConfiguration = inventoryConfiguration;
        this.messageConfiguration = messageConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            InventoryConfiguration.ExtendRent extendRentInventory = this.inventoryConfiguration.extendRent;

            Gui gui = Gui.gui()
                    .title(Legacy.title(extendRentInventory.title))
                    .rows(extendRentInventory.rows)
                    .disableAllInteractions()
                    .create();

            Rent rent = house.getRent().get();
            Duration timeBeforeRentEnd = Duration.between(Instant.now(), rent.getEndOfRent());

            int currentDays = (int) timeBeforeRentEnd.toDays() + 1;
            AtomicInteger days = new AtomicInteger();

            Formatter formatter = new Formatter();
            formatter.register("{DAYS_LEFT}", currentDays);
            formatter.register("{DAYS}", days);
            formatter.register("{PRICE}", days.get() * rent.getPricePerDay());

            if (extendRentInventory.fillEmptySlots) {
                gui.getFiller().fill(extendRentInventory.filler.asGuiItem());
            }

            GuiAction<InventoryClickEvent> extendRentAction = (event) -> {
                if (!this.purchaseService.hasEnoughMoney(player, days.get() * rent.getPricePerDay())) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.notEnoughMoneyToRent, formatter);
                    gui.close(player);

                    return;
                }

                Consumer<UUID> confirm = (confirmPlayerUuid) -> {
                    Rent extendRent = this.rentService.createRent(confirmPlayerUuid, house, days.get());

                    this.rentService.addRent(extendRent);
                    this.houseService.rentHouse(player, house, extendRent);
                    this.purchaseService.withdrawMoney(player, days.get() * rent.getPricePerDay());

                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.extendRentTime, formatter);
                    gui.close(player);
                };

                this.confirmInventory.openInventory(player, Option.none(), confirm, gui::close);
            };

            this.setItem(gui, extendRentInventory.extendRentItem, extendRentAction, formatter);

            this.setItem(gui, extendRentInventory.addDayItem, event -> {
                this.updateFormatter(formatter, days.incrementAndGet(), rent);
                this.setItem(gui, extendRentInventory.extendRentItem, extendRentAction, formatter);
                gui.update();
            });

            this.setItem(gui, extendRentInventory.removeDayItem, event -> {
                if (days.get() <= 0) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.requiredExtendRentTime);
                    return;
                }

                this.updateFormatter(formatter, days.decrementAndGet(), rent);
                this.setItem(gui, extendRentInventory.extendRentItem, extendRentAction, formatter);
                gui.update();
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

    private void updateFormatter(Formatter formatter, int days, Rent rent) {
        formatter.register("{DAYS}", days);
        formatter.register("{PRICE}", days * rent.getPricePerDay());
    }

}