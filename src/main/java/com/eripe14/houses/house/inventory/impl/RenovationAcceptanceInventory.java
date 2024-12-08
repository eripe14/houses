package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceRequest;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.schematic.SchematicService;
import com.eripe14.houses.util.DurationUtil;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class RenovationAcceptanceInventory extends Inventory {

    private final Scheduler scheduler;
    private final AlertHandler alertHandler;
    private final RenovationAcceptanceService renovationAcceptanceService;
    private final HouseService houseService;
    private final SchematicService schematicService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;

    public RenovationAcceptanceInventory(
            Scheduler scheduler,
            AlertHandler alertHandler,
            RenovationAcceptanceService renovationAcceptanceService,
            HouseService houseService,
            SchematicService schematicService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.scheduler = scheduler;
        this.alertHandler = alertHandler;
        this.renovationAcceptanceService = renovationAcceptanceService;
        this.houseService = houseService;
        this.schematicService = schematicService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(Player player) {
        this.scheduler.async(() -> {
            PaginatedGui gui = Gui.paginated()
                    .title(Legacy.title(this.inventoryConfiguration.renovationAcceptance.title))
                    .rows(this.inventoryConfiguration.renovationAcceptance.rows)
                    .disableAllInteractions()
                    .create();

            this.setItem(gui, this.inventoryConfiguration.renovationAcceptance.nextPageItem, event -> gui.next());
            this.setItem(gui, this.inventoryConfiguration.renovationAcceptance.previousPageItem, event -> gui.previous());
            this.setItem(gui, this.inventoryConfiguration.renovationAcceptance.closeInventoryItem, event -> gui.close(player));

            Formatter formatter = new Formatter();

            for (RenovationAcceptanceRequest renovationAcceptanceRequest : this.renovationAcceptanceService.getRenovationAcceptanceRequests()) {
                Option<House> houseOption = this.houseService.getHouse(renovationAcceptanceRequest.getHouseId());

                if (houseOption.isEmpty()) {
                    continue;
                }

                House house = houseOption.get();
                HouseRegion region = house.getRegion();
                Owner owner = house.getOwner().get();

                formatter.register("{HOUSE}", house.getHouseId().replace("_", " "));
                formatter.register("{OWNER}", house.getOwner().get().getName());
                formatter.register("{RENOVATION_TYPE}", renovationAcceptanceRequest.getRenovationType().getName());
                formatter.register("{START_DATE}", DurationUtil.format(renovationAcceptanceRequest.getStartMoment()));
                formatter.register("{END_DATE}", DurationUtil.format(renovationAcceptanceRequest.getEndMoment()));

                this.addItem(gui, this.inventoryConfiguration.renovationAcceptance.acceptRenovationItem, event -> {
                    Consumer<UUID> accept = (uuid) -> {
                        Alert alert = new Alert(
                                owner.getUuid(),
                                this.messageConfiguration.house.renovationChangesAcceptedSubject,
                                this.messageConfiguration.house.renovationChangesAcceptedMessage,
                                new AlertFormatter()
                        );

                        this.renovationAcceptanceService.removeRenovationAcceptanceRequest(house.getHouseId());

                        this.alertHandler.sendAlertIfPlayerNotOnline(owner.getUuid(), alert);
                        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationChangesAccepted, formatter);
                        gui.close(player);
                    };

                    Consumer<Player> cancel = (secondPlayerVariable) -> {
                        Alert alert = new Alert(
                                owner.getUuid(),
                                this.messageConfiguration.house.renovationChangesDeniedSubject,
                                this.messageConfiguration.house.renovationChangesDeniedMessage,
                                new AlertFormatter()
                        );

                        this.renovationAcceptanceService.removeRenovationAcceptanceRequest(house.getHouseId());
                        this.schematicService.pasteSchematicNormalHeight(
                                region.getWorld(),
                                region.getPlot().getMinimumPoint(),
                                house.getRegion().getLatestSchematicName()
                        );

                        this.alertHandler.sendAlertIfPlayerNotOnline(owner.getUuid(), alert);
                        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationChangesDenied, formatter);

                        gui.close(player);
                    };

                    this.confirmInventory.openInventory(
                            player,
                            this.inventoryConfiguration.renovationAcceptance.confirmTitle,
                            Option.of(owner.getUuid()),
                            accept,
                            cancel
                    );
                }, formatter);
            }


            this.scheduler.sync(() -> gui.open(player));
        });
    }

}