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

public class ApartamentRenovationInventory extends Inventory {

    private final Server server;
    private final Scheduler scheduler;
    private final RenovationRequestService renovationRequestService;
    private final HouseService houseService;
    private final TextProvider textProvider;
    private final PurchaseService purchaseService;
    private final ConfirmInventory confirmInventory;
    private final AlertHandler alertHandler;
    private final NotificationAnnouncer notificationAnnouncer;
    private final InventoryConfiguration inventoryConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public ApartamentRenovationInventory(
            Server server,
            Scheduler scheduler,
            RenovationRequestService renovationRequestService,
            HouseService houseService,
            TextProvider textProvider,
            PurchaseService purchaseService,
            ConfirmInventory confirmInventory,
            AlertHandler alertHandler,
            NotificationAnnouncer notificationAnnouncer,
            InventoryConfiguration inventoryConfiguration,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.server = server;
        this.scheduler = scheduler;
        this.renovationRequestService = renovationRequestService;
        this.houseService = houseService;
        this.textProvider = textProvider;
        this.purchaseService = purchaseService;
        this.confirmInventory = confirmInventory;
        this.alertHandler = alertHandler;
        this.notificationAnnouncer = notificationAnnouncer;
        this.inventoryConfiguration = inventoryConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            InventoryConfiguration.ApartamentRenovation renovation = inventoryConfiguration.apartamentRenovation;

            Gui gui = Gui.gui()
                    .title(Legacy.title(renovation.title))
                    .rows(renovation.rows)
                    .disableAllInteractions()
                    .create();

            Formatter formatter = new Formatter();
            formatter.register("{PRICE}", this.pluginConfiguration.notInterferingRenovationRequestPrice);

            if (renovation.fillEmptySlots) {
                gui.getFiller().fill(renovation.filler.asGuiItem());
            }

            this.setItem(gui, renovation.nonInterfering, (event) -> {
                if (!this.purchaseService.hasEnoughMoney(player, this.pluginConfiguration.notInterferingRenovationRequestPrice)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.notEnoughMoneyToRequestRenovate);
                    return;
                }

                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.provideRenovationRequest);
                gui.close(player);
                this.handle(player, house, gui);
            }, formatter);

            this.setItem(gui, renovation.closeItem, (event) -> {
                player.closeInventory();
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

    private void handle(Player player, House house, Gui gui) {
        Formatter formatter = new Formatter();
        formatter.register("{TIME}", this.pluginConfiguration.maxRenovationDays);

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
            this.handleDays(player, message, house, gui);
        });
    }

    private void handleDays(Player player, String message, House house, Gui gui) {
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
                    this.handleDays(player, message, house, gui);
                    return;
                }

                formatter.register("{RENOVATION_TYPE}", RenovationType.NON_INTERFERING.getName());
                formatter.register("{REQUEST}", message);
                formatter.register("{DAYS}", numberOfDays);

                Consumer<UUID> confirmAction = (uuid) -> {
                    RenovationRequest renovationRequest = this.renovationRequestService.addRequest(
                            player,
                            house,
                            RenovationType.NON_INTERFERING,
                            numberOfDays,
                            message
                    );
                    this.houseService.requestRenovation(house, renovationRequest);

                    AlertFormatter alertFormatter = new AlertFormatter();
                    alertFormatter.register("{HOUSE}", house.getHouseId());

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
                        this.inventoryConfiguration.apartamentRenovation.confirmTitle,
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