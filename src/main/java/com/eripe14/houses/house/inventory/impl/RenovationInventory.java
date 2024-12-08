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
import com.eripe14.houses.house.renovation.RenovationType;
import com.eripe14.houses.house.renovation.request.RenovationRequest;
import com.eripe14.houses.house.renovation.request.RenovationRequestService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.purchase.PurchaseService;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.text.TextProvider;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class RenovationInventory extends Inventory {

    private final Scheduler scheduler;
    private final Server server;
    private final RenovationRequestService renovationRequestService;
    private final HouseService houseService;
    private final TextProvider textProvider;
    private final AlertHandler alertHandler;
    private final PurchaseService purchaseService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final InventoryConfiguration inventoryConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public RenovationInventory(
            Scheduler scheduler,
            Server server,
            RenovationRequestService renovationRequestService,
            HouseService houseService,
            TextProvider textProvider,
            AlertHandler alertHandler,
            PurchaseService purchaseService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            InventoryConfiguration inventoryConfiguration,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.scheduler = scheduler;
        this.server = server;
        this.renovationRequestService = renovationRequestService;
        this.houseService = houseService;
        this.textProvider = textProvider;
        this.alertHandler = alertHandler;
        this.purchaseService = purchaseService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.inventoryConfiguration = inventoryConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            InventoryConfiguration.Renovate renovate = inventoryConfiguration.renovate;

            Gui gui = Gui.gui()
                    .title(Legacy.title(renovate.title))
                    .rows(renovate.rows)
                    .disableAllInteractions()
                    .create();

            if (renovate.fillEmptySlots) {
                gui.getFiller().fill(renovate.filler.asGuiItem());
            }

            Formatter formatter = new Formatter();
            formatter.register("{COMPLETE_PRICE}", this.pluginConfiguration.completeRenovationRequestPrice);
            formatter.register("{MAJOR_PRICE}", this.pluginConfiguration.majorRenovationRequestPrice);
            formatter.register("{NOT_INTERFERING_PRICE}", this.pluginConfiguration.notInterferingRenovationRequestPrice);

            this.setItem(gui, renovate.completeRenovationItem, (event) -> {
                if (!this.purchaseService.hasEnoughMoney(player, this.pluginConfiguration.completeRenovationRequestPrice)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.notEnoughMoneyToRequestRenovate);
                    return;
                }

                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.provideRenovationRequest);
                gui.close(player);

                this.handle(player, house, RenovationType.COMPLETE, gui);
            }, formatter);

            this.setItem(gui, renovate.majorRenovationItem, (event) -> {
                if (!this.purchaseService.hasEnoughMoney(player, this.pluginConfiguration.majorRenovationRequestPrice)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.notEnoughMoneyToRequestRenovate);
                    return;
                }

                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.provideRenovationRequest);
                gui.close(player);

                this.handle(player, house, RenovationType.MAJOR, gui);
            }, formatter);

            this.setItem(gui, renovate.nonInterferingRenovationItem, (event) -> {
                if (!this.purchaseService.hasEnoughMoney(player, this.pluginConfiguration.notInterferingRenovationRequestPrice)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.notEnoughMoneyToRequestRenovate);
                    return;
                }

                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.provideRenovationRequest);
                gui.close(player);

                this.handle(player, house, RenovationType.NON_INTERFERING, gui);
            }, formatter);

            this.setItem(gui, renovate.closeItem, (event) -> {
                player.closeInventory();
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

    private void handle(Player player, House house, RenovationType type, Gui gui) {
        AlertFormatter formatter = new AlertFormatter();
        formatter.register("{TIME}", String.valueOf(this.pluginConfiguration.maxRenovationDays));
        formatter.register("{HOUSE}", house.getHouseId().replace("_", " "));

        this.textProvider.getPlayerInput(player).whenComplete((message, throwable) -> {
            if (message == null || message.isEmpty()) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationRequestCanNotBeEmpty);
                return;
            }

            for (String renovationRequestCancelWord : this.pluginConfiguration.renovationRequestCancelWords) {
                if (message.equalsIgnoreCase(renovationRequestCancelWord)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationRequestCancelled);
                    return;
                }
            }

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.provideRenovationTime);
            this.handleDays(player, message, type, house, gui);
        });
    }

    private void handleDays(Player player, String message, RenovationType renovationType, House house, Gui gui) {
        Formatter formatter = new Formatter();
        formatter.register("{TIME}", this.pluginConfiguration.maxRenovationDays);

        this.textProvider.getPlayerInput(player).thenAccept((days) -> {
            if (days == null || days.isEmpty()) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationTimeCanNotBeEmpty);
                return;
            }

            try {
                int numberOfDays = Integer.parseInt(days);

                if (numberOfDays <= 0 || numberOfDays > this.pluginConfiguration.maxRenovationDays) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationTimeCanNotBe, formatter);
                    this.handleDays(player, message, renovationType, house, gui);
                    return;
                }

                formatter.register("{RENOVATION_TYPE}", renovationType.getName());
                formatter.register("{REQUEST}", message);
                formatter.register("{HOUSE}", house.getHouseId().replace("_", " "));
                formatter.register("{DAYS}", numberOfDays);

                Consumer<UUID> confirmAction = (uuid) -> {
                    RenovationRequest renovationRequest = this.renovationRequestService.addRequest(
                            player,
                            house,
                            renovationType,
                            numberOfDays,
                            message
                    );
                    this.houseService.requestRenovation(house, renovationRequest);

                    AlertFormatter alertFormatter = new AlertFormatter();
                    alertFormatter.register("{HOUSE}", house.getHouseId().replace("_", " "));

                    for (Player onlinePlayer : this.server.getOnlinePlayers()) {
                        if (!onlinePlayer.hasPermission(this.pluginConfiguration.renovationApplicationsPermission)) {
                            continue;
                        }

                        Alert alert = new Alert(
                                onlinePlayer.getUniqueId(),
                                this.messageConfiguration.house.renovationRequestSubject,
                                this.messageConfiguration.house.renovationRequestMessage,
                                alertFormatter
                        );

                        this.alertHandler.sendAlertIfPlayerNotOnline(onlinePlayer.getUniqueId(), alert);
                    }

                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationRequestCompleted);
                    gui.close(player);
                };

                Consumer<Player> cancelAction = (secondPlayerVariable) -> {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationRequestCancelled);
                    gui.close(secondPlayerVariable);
                };

                this.confirmInventory.openInventory(
                        player,
                        this.inventoryConfiguration.renovate.confirmTitle,
                        Option.none(),
                        this.inventoryConfiguration.confirm.playerRenovationRequestAdditionalItem,
                        confirmAction,
                        cancelAction,
                        formatter
                );
            } catch (NumberFormatException exception) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.renovationTimeMustBeNumber);
            }
        });
    }

}