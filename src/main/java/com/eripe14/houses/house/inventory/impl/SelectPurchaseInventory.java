package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.purchase.HousePurchaseService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class SelectPurchaseInventory extends Inventory {

    private final Scheduler scheduler;
    private final HousePurchaseService housePurchaseService;
    private final ConfirmInventory confirmInventory;
    private final RentInventory rentInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final InventoryConfiguration inventoryConfiguration;
    private final MessageConfiguration messageConfiguration;

    public SelectPurchaseInventory(
            Scheduler scheduler,
            HousePurchaseService housePurchaseService,
            ConfirmInventory confirmInventory, RentInventory rentInventory,
            NotificationAnnouncer notificationAnnouncer,
            InventoryConfiguration inventoryConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.scheduler = scheduler;
        this.housePurchaseService = housePurchaseService;
        this.confirmInventory = confirmInventory;
        this.rentInventory = rentInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.inventoryConfiguration = inventoryConfiguration;
        this.messageConfiguration = messageConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            InventoryConfiguration.SelectPurchaseOption selectPurchaseOption = this.inventoryConfiguration.selectPurchaseOption;

            Gui gui = Gui.gui()
                    .title(Legacy.title(selectPurchaseOption.title))
                    .rows(selectPurchaseOption.rows)
                    .disableAllInteractions()
                    .create();

            if (selectPurchaseOption.fillEmptySlots) {
                gui.getFiller().fill(selectPurchaseOption.filler.asGuiItem());
            }

            Formatter formatter = new Formatter();
            formatter.register("{HOUSE_ID}", house.getHouseId());
            formatter.register("{BUY_PRICE}", house.getBuyPrice());

            this.setItem(gui, selectPurchaseOption.buyItem, event -> {
                Consumer<UUID> confirm = (confirmPlayer) -> {
                    if (this.housePurchaseService.purchaseHouse(player, house)) {
                        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.boughtHouse, formatter);
                        gui.close(player);

                        return;
                    }

                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.notEnoughMoneyToBuy);
                    gui.close(player);
                };

                this.confirmInventory.openInventory(player, Option.none(), confirm, gui::close);
            }, formatter);

            this.setItem(gui, selectPurchaseOption.rentItem, event -> this.rentInventory.openInventory(player, house), formatter);

            this.setItem(gui, selectPurchaseOption.closeInventoryItem, event -> gui.close(player));

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}