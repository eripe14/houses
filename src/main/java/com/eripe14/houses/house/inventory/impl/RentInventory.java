package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.purchase.HousePurchaseService;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.house.region.RegionService;
import com.eripe14.houses.house.rent.Rent;
import com.eripe14.houses.house.rent.RentService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.purchase.PurchaseService;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.schematic.SchematicService;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class RentInventory extends Inventory {

    private final Scheduler scheduler;
    private final PurchaseService purchaseService;
    private final HousePurchaseService housePurchaseService;
    private final HouseService houseService;
    private final RentService rentService;
    private final RegionService regionService;
    private final ConfirmInventory confirmInventory;
    private final SchematicService schematicService;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;
    private final PluginConfiguration pluginConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;

    public RentInventory(
            Scheduler scheduler,
            PurchaseService purchaseService,
            HousePurchaseService housePurchaseService,
            HouseService houseService,
            RentService rentService,
            RegionService regionService,
            ConfirmInventory confirmInventory,
            SchematicService schematicService,
            MessageConfiguration messageConfiguration,
            InventoryConfiguration inventoryConfiguration,
            PluginConfiguration pluginConfiguration,
            NotificationAnnouncer notificationAnnouncer
    ) {
        this.scheduler = scheduler;
        this.purchaseService = purchaseService;
        this.housePurchaseService = housePurchaseService;
        this.houseService = houseService;
        this.rentService = rentService;
        this.regionService = regionService;
        this.confirmInventory = confirmInventory;
        this.schematicService = schematicService;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
        this.pluginConfiguration = pluginConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            InventoryConfiguration.Rent rentInventory = this.inventoryConfiguration.rent;
            MessageConfiguration.House houseMessage = this.messageConfiguration.house;

            int minRentDays = this.pluginConfiguration.minRentDays;
            AtomicInteger days = new AtomicInteger(minRentDays);

            Formatter formatter = new Formatter();
            formatter.register("{HOUSE_ID}", house.getHouseId().replace("_", " "));
            formatter.register("{DAYS}", days.get());
            formatter.register("{PRICE}", house.getDailyRentalPrice() * days.get());
            formatter.register("{MIN_RENT_TIME}", minRentDays);
            formatter.register("{RENT_TIME}", days.get());
            formatter.register("{BLOCK}", house.getBlockOfFlatsId());

            Gui gui = Gui.gui()
                    .title(Legacy.title(formatter.format(rentInventory.title)))
                    .rows(rentInventory.rows)
                    .disableAllInteractions()
                    .create();

            if (rentInventory.fillEmptySlots) {
                gui.getFiller().fill(rentInventory.filler.asGuiItem());
            }

            GuiAction<InventoryClickEvent> rentAction = (event) -> {
                if (days.get() < minRentDays) {
                    this.notificationAnnouncer.sendActionBar(player, houseMessage.requiredRentalTime, formatter);
                    return;
                }

                if (!this.purchaseService.hasEnoughMoney(player, house.getDailyRentalPrice() * days.get())) {
                    this.notificationAnnouncer.sendMessage(player, houseMessage.notEnoughMoneyToRent, formatter);
                    gui.close(player);

                    return;
                }

                Consumer<UUID> confirm = (confirmPlayerUuid) -> {
                    if (house.getOwner().isPresent()) {
                        return;
                    }

                    Rent rent = this.rentService.createRent(confirmPlayerUuid, house, days.get());

                    this.rentService.addRent(rent);
                    this.houseService.rentHouse(player, house, rent);

                    this.housePurchaseService.addPanelItem(player);
                    this.housePurchaseService.killPurchaseFurniture(house);
                    this.purchaseService.withdrawMoney(player, house.getDailyRentalPrice() * days.get());
                    this.regionService.resetRegion(house.getRegion());
                    this.schematicService.saveSchematic(house.getRegion(), "_rent");

                    if (house.getRegion().getHouseType() == HouseType.APARTMENT) {
                        this.notificationAnnouncer.sendMessage(player, houseMessage.rentedApartment, formatter);
                    } else {
                        this.notificationAnnouncer.sendMessage(player, houseMessage.rentedHouse, formatter);
                    }

                    this.scheduler.sync(() -> gui.close(player));
                };

                Consumer<Player> cancel = (cancelPlayer) -> {
                    gui.close(player);
                };

                this.confirmInventory.openInventory(
                        player,
                        rentInventory.confirmTitle,
                        Option.none(),
                        this.inventoryConfiguration.confirm.rentHouseAdditionalItem,
                        confirm,
                        cancel,
                        formatter
                );
            };

            this.setItem(gui, rentInventory.rentItem, rentAction, formatter);

            this.setItem(gui, rentInventory.addDayItem, (event) -> {
                if (event.isShiftClick()) {
                    this.updateFormatter(formatter, days.addAndGet(10), house);
                    this.setItem(gui, rentInventory.rentItem, rentAction, formatter);

                    gui.update();
                    return;
                }

                this.updateFormatter(formatter, days.incrementAndGet(), house);
                this.setItem(gui, rentInventory.rentItem, rentAction, formatter);

                gui.update();
            });

            this.setItem(gui, rentInventory.removeDayItem, (event) -> {
                if (days.get() <= minRentDays) {
                    this.notificationAnnouncer.sendActionBar(player, houseMessage.requiredRentalTime, formatter);
                    return;
                }

                if (event.isShiftClick()) {
                    if (days.get() - 10 <= minRentDays) {
                        this.updateFormatter(formatter, minRentDays, house);
                        this.setItem(gui, rentInventory.rentItem, rentAction, formatter);
                        this.notificationAnnouncer.sendActionBar(player, houseMessage.requiredRentalTime, formatter);

                        gui.update();
                        return;
                    }

                    this.updateFormatter(formatter, days.addAndGet(-10), house);
                    this.setItem(gui, rentInventory.rentItem, rentAction, formatter);

                    gui.update();
                    return;
                }

                this.updateFormatter(formatter, days.decrementAndGet(), house);
                this.setItem(gui, rentInventory.rentItem, rentAction, formatter);

                gui.update();
            });

            this.setItem(gui, rentInventory.closeInventoryItem, (event) -> gui.close(player));

            this.scheduler.sync(() -> gui.open(player));
        });
    }

    private void updateFormatter(Formatter formatter, int days, House house) {
        formatter.register("{DAYS}", days);
        formatter.register("{PRICE}", house.getDailyRentalPrice() * days);
    }

}