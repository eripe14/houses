package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
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
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;

    public ListOfHouseMembersInventory(
            Scheduler scheduler,
            Server server,
            ConfirmInventory confirmInventory,
            InventoryConfiguration inventoryConfiguration,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration
    ) {
        this.scheduler = scheduler;
        this.server = server;
        this.confirmInventory = confirmInventory;
        this.inventoryConfiguration = inventoryConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
    }

    public void openInventory(Player player, House house, String title, Consumer<UUID> clickAction) {
        this.openInventory(player, house, title, clickAction, false);
    }

    public void openInventory(Player player, House house, String title, Consumer<UUID> clickAction, boolean canHandleYourself) {
        this.scheduler.async(() -> {
            InventoryConfiguration.ListOfHouseMembers listOfHouseMembers = this.inventoryConfiguration.listOfHouseMembers;

            PaginatedGui gui = Gui.paginated()
                    .title(Legacy.title(title))
                    .rows(listOfHouseMembers.rows)
                    .disableAllInteractions()
                    .create();

            Formatter formatter = new Formatter();
            Consumer<Player> close = gui::close;

            house.getMembers().forEach((uuid, houseMember) -> {
                formatter.register("{PLAYER}", houseMember.getMemberName());

                OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(uuid);

                this.addSkullItem(gui, listOfHouseMembers.headItem, offlinePlayer, event -> {
                    if (offlinePlayer.getUniqueId().equals(player.getUniqueId()) && !canHandleYourself) {
                        this.notificationAnnouncer.sendActionBar(player, this.messageConfiguration.house.canNotModifyYourself);
                        return;
                    }

                    if (offlinePlayer.getUniqueId().equals(house.getOwner().get().getUuid())) {
                        return;
                    }

                    this.confirmInventory.openSkullInventory(
                            player,
                            listOfHouseMembers.confirmTitle,
                            Option.of(offlinePlayer.getUniqueId()),
                            offlinePlayer,
                            this.inventoryConfiguration.confirm.skullAdditionalItem,
                            clickAction,
                            close
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