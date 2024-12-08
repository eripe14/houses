package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.renovation.Renovation;
import com.eripe14.houses.house.renovation.RenovationService;
import com.eripe14.houses.house.renovation.request.RenovationRequest;
import com.eripe14.houses.house.renovation.request.RenovationRequestService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.schematic.SchematicService;
import com.eripe14.houses.util.DurationUtil;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;

public class RenovationApplicationsInventory extends Inventory {

    private final Scheduler scheduler;
    private final RenovationRequestService renovationRequestService;
    private final RenovationService renovationService;
    private final HouseService houseService;
    private final AlertHandler alertHandler;
    private final SchematicService schematicService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;

    public RenovationApplicationsInventory(
            Scheduler scheduler,
            RenovationRequestService renovationRequestService,
            RenovationService renovationService,
            HouseService houseService,
            AlertHandler alertHandler,
            SchematicService schematicService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.scheduler = scheduler;
        this.renovationRequestService = renovationRequestService;
        this.renovationService = renovationService;
        this.houseService = houseService;
        this.alertHandler = alertHandler;
        this.schematicService = schematicService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(Player player) {
        this.scheduler.async(() -> {
            InventoryConfiguration.RenovationApplications renovationApplications = this.inventoryConfiguration.renovationApplications;

            PaginatedGui gui = Gui.paginated()
                    .title(Legacy.title(renovationApplications.title))
                    .rows(renovationApplications.rows)
                    .disableAllInteractions()
                    .create();

            this.setItem(gui, renovationApplications.nextPageItem, event -> {
                gui.next();
            });

            this.setItem(gui, renovationApplications.previousPageItem, event -> {
                gui.previous();
            });

            this.setItem(gui, renovationApplications.closeInventoryItem, event -> {
                gui.close(player);
            });

            AlertFormatter formatter = new AlertFormatter();

            for (RenovationRequest renovationRequest : this.renovationRequestService.getRenovations()) {
                formatter.register("{OWNER}", renovationRequest.getSenderName());
                formatter.register("{RENOVATION_TYPE}", renovationRequest.getRenovationType().getName());
                formatter.register("{REQUEST}", renovationRequest.getRequest());
                formatter.register("{HOUSE}", renovationRequest.getHouseId().replace('_', ' '));
                formatter.register("{HOUSE_ID}", renovationRequest.getHouseId().replace('_', ' '));
                formatter.register("{DAYS}", String.valueOf(renovationRequest.getRenovationDays()));
                formatter.register("{CREATION_TIME}", DurationUtil.format(renovationRequest.getCreationTime()));

                this.addItem(gui, renovationApplications.renovationItem, event -> {
                    Consumer<UUID> accept = (uuid) -> {
                        Renovation renovation = this.renovationService.addRenovation(renovationRequest);
                        this.houseService.renovateHouse(renovation);

                        Alert alert = new Alert(
                                uuid,
                                this.messageConfiguration.house.renovationRequestAcceptedSubject,
                                this.messageConfiguration.house.renovationRequestAcceptedMessage,
                                formatter
                        );

                        Option<House> houseOption = this.houseService.getHouse(renovationRequest.getHouseId());

                        if (houseOption.isEmpty()) {
                            return;
                        }

                        House house = houseOption.get();

                        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationRequestAccepted);
                        this.alertHandler.sendAlertIfPlayerNotOnline(uuid, alert);
                        this.renovationRequestService.removeRequest(renovationRequest.getHouseId());
                        this.houseService.removeRenovationRequest(renovationRequest.getHouseId());

                        String name = house.getHouseId() + "_before_renovation_" + DurationUtil.formatSchematic(Instant.now());

                        this.schematicService.saveSchematicWithName(house.getRegion(), name);
                        this.houseService.setLatestSchematicName(
                                house,
                                name
                        );

                        gui.close(player);
                    };

                    Consumer<Player> cancel = (secondPlayerField) -> {
                        Alert alert = new Alert(
                                secondPlayerField.getUniqueId(),
                                this.messageConfiguration.house.renovationRequestDeniedSubject,
                                this.messageConfiguration.house.renovationRequestDeniedMessage,
                                formatter
                        );

                        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationRequestDenied);
                        this.alertHandler.sendAlertIfPlayerNotOnline(secondPlayerField.getUniqueId(), alert);
                        this.renovationRequestService.removeRequest(renovationRequest.getHouseId());
                        this.houseService.removeRenovationRequest(renovationRequest.getHouseId());

                        gui.close(player);
                    };

                    this.confirmInventory.openInventory(
                            player,
                            renovationApplications.confirmTitle,
                            Option.of(renovationRequest.getSender()),
                            this.inventoryConfiguration.confirm.renovationRequestAdditionalItem,
                            accept,
                            cancel,
                            formatter.toFormatter()
                    );
                }, formatter);
            }

            this.scheduler.sync(() -> gui.open(player));
        });
    }
}