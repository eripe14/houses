package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.renovation.Renovation;
import com.eripe14.houses.house.renovation.RenovationService;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.schematic.SchematicService;
import com.eripe14.houses.util.DurationUtil;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;

public class MenageRenovationInventory extends Inventory {

    private final Server server;
    private final Scheduler scheduler;
    private final RenovationService renovationService;
    private final RenovationAcceptanceService renovationAcceptanceService;
    private final HouseService houseService;
    private final AlertHandler alertHandler;
    private final SchematicService schematicService;
    private final MessageConfiguration messageConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;
    private final InventoryConfiguration inventoryConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public MenageRenovationInventory(
            Server server,
            Scheduler scheduler,
            RenovationService renovationService,
            RenovationAcceptanceService renovationAcceptanceService,
            HouseService houseService,
            AlertHandler alertHandler,
            SchematicService schematicService,
            MessageConfiguration messageConfiguration,
            NotificationAnnouncer notificationAnnouncer,
            InventoryConfiguration inventoryConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.server = server;
        this.scheduler = scheduler;
        this.renovationService = renovationService;
        this.renovationAcceptanceService = renovationAcceptanceService;
        this.houseService = houseService;
        this.alertHandler = alertHandler;
        this.schematicService = schematicService;
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
        this.inventoryConfiguration = inventoryConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            AlertFormatter formatter = new AlertFormatter();
            formatter.register("{HOUSE}", house.getHouseId().replace('_', ' '));

            Gui gui = Gui.gui()
                    .title(Legacy.title(formatter.format(this.inventoryConfiguration.menageRenovation.title)))
                    .rows(this.inventoryConfiguration.menageRenovation.rows)
                    .disableAllInteractions()
                    .create();

            Option<Renovation> renovationOption = house.getCurrentRenovation();

            if (renovationOption.isEmpty()) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.noRenovationInProgress);
                return;
            }

            Renovation renovation = renovationOption.get();

            formatter.register("{DATE}", DurationUtil.format(renovation.getStartMoment()));

            if (this.inventoryConfiguration.menageRenovation.fillEmptySlots) {
                gui.getFiller().fill(this.inventoryConfiguration.menageRenovation.filler.asGuiItem());
            }

            this.setItem(gui, this.inventoryConfiguration.menageRenovation.cancelRenovationBeforeTimeEnds, event -> {
                for (Player onlinePlayer : this.server.getOnlinePlayers()) {
                    if (!onlinePlayer.hasPermission(this.pluginConfiguration.renovationApplicationsPermission)) {
                        continue;
                    }

                    Alert alert = new Alert(
                        onlinePlayer.getUniqueId(),
                        this.messageConfiguration.house.renovationTerminateSubject,
                        this.messageConfiguration.house.renovationTerminateMessage,
                        formatter
                    );

                    this.alertHandler.sendAlertIfPlayerNotOnline(onlinePlayer.getUniqueId(), alert);
                }

                this.renovationAcceptanceService.createRenovationAcceptanceRequest(house, renovation);
                this.renovationService.removeRenovation(renovation);
                this.houseService.removeRenovation(renovation);
                this.schematicService.saveSchematic(house.getRegion(), "_renovation_backup");

                Alert alert = new Alert(
                    player.getUniqueId(),
                    this.messageConfiguration.house.renovationTerminateOwnerSubject,
                    this.messageConfiguration.house.renovationTerminateOwnerMessage,
                    formatter
                );

                this.alertHandler.sendAlertIfPlayerNotOnline(player.getUniqueId(), alert);
                gui.close(player);
            }, formatter);

            this.setItem(gui, this.inventoryConfiguration.menageRenovation.closeInventoryItem, event -> {
                gui.close(player);
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }
}