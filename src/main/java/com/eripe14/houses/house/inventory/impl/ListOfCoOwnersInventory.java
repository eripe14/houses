package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.notification.NotificationAnnouncer;
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

public class ListOfCoOwnersInventory extends Inventory {

    private final Scheduler scheduler;
    private final Server server;
    private final ConfirmInventory confirmInventory;
    private final InventoryConfiguration inventoryConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;

    public ListOfCoOwnersInventory(
            Scheduler scheduler,
            Server server, ConfirmInventory confirmInventory,
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
        this.scheduler.async(() -> {
            InventoryConfiguration.ListOfCoOwners listOfCoOwners = this.inventoryConfiguration.listOfCoOwners;

            PaginatedGui gui = Gui.paginated()
                    .title(Legacy.title(title))
                    .rows(listOfCoOwners.rows)
                    .disableAllInteractions()
                    .create();

            Formatter formatter = new Formatter();
            Consumer<Player> close = gui::close;

            house.getMembers().values().stream().filter(HouseMember::isCoOwner).forEach(houseMember -> {
                formatter.register("{PLAYER}", houseMember.getMemberName());

                OfflinePlayer offlinePlayer = this.server.getOfflinePlayer(houseMember.getMemberUuid());

                this.addSkullItem(gui, listOfCoOwners.headItem, offlinePlayer, event -> {
                    if (offlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                        this.notificationAnnouncer.sendActionBar(player, this.messageConfiguration.house.canNotModifyYourself);
                        return;
                    }

                    this.confirmInventory.openSkullInventory(
                            player,
                            listOfCoOwners.confirmTitle,
                            Option.of(offlinePlayer.getUniqueId()),
                            offlinePlayer,
                            this.inventoryConfiguration.confirm.skullAdditionalItem,
                            clickAction,
                            close
                    );
                }, formatter);
            });

            this.setItem(gui, listOfCoOwners.nextPageItem, event -> {
                gui.next();
            });

            this.setItem(gui, listOfCoOwners.previousPageItem, event -> {
                gui.previous();
            });

            this.setItem(gui, listOfCoOwners.closeInventoryItem, event -> {
                gui.close(player);
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}