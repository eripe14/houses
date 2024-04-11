package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

public class SelectPurchaseInventory extends Inventory {

    private final Scheduler scheduler;
    private final InventoryConfiguration inventoryConfiguration;

    public SelectPurchaseInventory(Scheduler scheduler, InventoryConfiguration inventoryConfiguration) {
        this.scheduler = scheduler;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(Player player) {
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

            selectPurchaseOption.buyItem.asGuiItem((event) -> {
                event.setCancelled(true);
            });

            selectPurchaseOption.rentItem.asGuiItem((event) -> {
                event.setCancelled(true);
            });

            selectPurchaseOption.closeInventoryItem.asGuiItem((event) -> {
                gui.close(player);
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}