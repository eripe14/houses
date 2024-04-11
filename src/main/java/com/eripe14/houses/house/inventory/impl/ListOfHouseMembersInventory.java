package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class ListOfHouseMembersInventory extends Inventory {

    private final Scheduler scheduler;
    private final Server server;
    private final ConfirmInventory confirmInventory;
    private final InventoryConfiguration inventoryConfiguration;

    public ListOfHouseMembersInventory(Scheduler scheduler, Server server, ConfirmInventory confirmInventory, InventoryConfiguration inventoryConfiguration) {
        this.scheduler = scheduler;
        this.server = server;
        this.confirmInventory = confirmInventory;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(Player player, House house, String title, Consumer<UUID> clickAction) {
        this.scheduler.async(() -> {
            InventoryConfiguration.ListOfHouseMembers listOfHouseMembers = this.inventoryConfiguration.listOfHouseMembers;

            PaginatedGui gui = Gui.paginated()
                    .title(Legacy.title(title))
                    .rows(listOfHouseMembers.rows)
                    .disableAllInteractions()
                    .create();

            Formatter formatter = new Formatter();

            house.getMembers().forEach((uuid, houseMember) -> {
                formatter.register("{PLAYER}", houseMember.getMemberName());

                OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(uuid);

                this.setSkullItem(gui, listOfHouseMembers.headItem, offlinePlayer, event -> {
                    this.confirmInventory.openInventory(
                            player,
                            Option.of(offlinePlayer.getUniqueId()),
                            clickAction, gui::close
                    );
                }, formatter);
            });

            this.setItem(gui, listOfHouseMembers.nextPageItem, event -> {
                gui.next();
            });

            this.setItem(gui, listOfHouseMembers.previousPageItem, event -> {
                gui.previous();
            });

            this.setItem(gui, listOfHouseMembers.closeInventoryItem, event -> {
                gui.close(player);
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}