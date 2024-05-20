package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.ItemConfiguration;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class ConfirmInventory extends Inventory {

    private final Scheduler scheduler;
    private final InventoryConfiguration inventoryConfiguration;

    public ConfirmInventory(Scheduler scheduler, InventoryConfiguration inventoryConfiguration) {
        this.scheduler = scheduler;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(
            Player player,
            String title,
            Option<UUID> targetOption,
            ItemConfiguration additionalItem,
            Consumer<UUID> confirmAction,
            Consumer<Player> cancelAction,
            Formatter formatter
    ) {
        this.scheduler.async(() -> {
            InventoryConfiguration.Confirm confirm = this.inventoryConfiguration.confirm;

            Gui gui = Gui.gui()
                    .title(Legacy.title(title))
                    .rows(confirm.rows)
                    .disableAllInteractions()
                    .create();

            if (confirm.fillEmptySlots) {
                gui.getFiller().fill(confirm.filler.asGuiItem());
            }

            this.setItem(gui, confirm.confirmItem, (event) -> {
                if (targetOption.isEmpty()) {
                    confirmAction.accept(player.getUniqueId());
                    return;
                }

                confirmAction.accept(targetOption.get());
            });

            this.setItem(gui, additionalItem, (event) -> {
            }, formatter);

            this.setItem(gui, confirm.cancelItem, (event) -> {
                cancelAction.accept(player);
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

    public void openSkullInventory(
            Player player,
            String title,
            Option<UUID> targetOption,
            OfflinePlayer offlinePlayer,
            ItemConfiguration additionalSkullItem,
            Consumer<UUID> confirmAction,
            Consumer<Player> cancelAction
    ) {
        this.scheduler.async(() -> {
            InventoryConfiguration.Confirm confirm = this.inventoryConfiguration.confirm;

            Gui gui = Gui.gui()
                    .title(Legacy.title(title))
                    .rows(confirm.rows)
                    .disableAllInteractions()
                    .create();

            if (confirm.fillEmptySlots) {
                gui.getFiller().fill(confirm.filler.asGuiItem());
            }

            this.setItem(gui, confirm.confirmItem, (event) -> {
                if (targetOption.isEmpty()) {
                    confirmAction.accept(player.getUniqueId());
                    return;
                }

                confirmAction.accept(targetOption.get());
            });

            Formatter formatter = new Formatter();
            formatter.register("{PLAYER}", offlinePlayer.getName());

            this.setSkullItem(gui, additionalSkullItem, offlinePlayer, (event) -> { }, formatter);

            this.setItem(gui, confirm.cancelItem, (event) -> {
                cancelAction.accept(player);
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

    public void openInventory(
            Player player,
            String title,
            Option<UUID> targetOption,
            Consumer<UUID> confirmAction,
            Consumer<Player> cancelAction
    ) {
        this.scheduler.async(() -> {
            InventoryConfiguration.Confirm confirm = this.inventoryConfiguration.confirm;

            Gui gui = Gui.gui()
                    .title(Legacy.title(title))
                    .rows(confirm.rows)
                    .disableAllInteractions()
                    .create();

            if (confirm.fillEmptySlots) {
                gui.getFiller().fill(confirm.filler.asGuiItem());
            }

            this.setItem(gui, confirm.confirmItem, (event) -> {
                if (targetOption.isEmpty()) {
                    confirmAction.accept(player.getUniqueId());
                    return;
                }

                confirmAction.accept(targetOption.get());
            });

            this.setItem(gui, confirm.cancelItem, (event) -> {
                cancelAction.accept(player);
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}