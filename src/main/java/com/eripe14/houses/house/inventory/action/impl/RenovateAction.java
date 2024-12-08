package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ApartamentRenovationInventory;
import com.eripe14.houses.house.inventory.impl.MenageRenovationInventory;
import com.eripe14.houses.house.inventory.impl.RenovationInventory;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceRequest;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.UUID;
import java.util.function.Consumer;

public class RenovateAction implements InventoryClickAction {

    private final RenovationInventory renovationInventory;
    private final RenovationAcceptanceService renovationAcceptanceService;
    private final ApartamentRenovationInventory apartamentRenovationInventory;
    private final MenageRenovationInventory menageRenovationInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;

    public RenovateAction(
            RenovationInventory renovationInventory,
            RenovationAcceptanceService renovationAcceptanceService,
            ApartamentRenovationInventory apartamentRenovationInventory,
            MenageRenovationInventory menageRenovationInventory,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration
    ) {
        this.renovationInventory = renovationInventory;
        this.renovationAcceptanceService = renovationAcceptanceService;
        this.apartamentRenovationInventory = apartamentRenovationInventory;
        this.menageRenovationInventory = menageRenovationInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        return (uuid -> {
            Option<RenovationAcceptanceRequest> renovationAcceptanceRequest =
                    this.renovationAcceptanceService.getRenovationAcceptanceRequest(house);

            if (renovationAcceptanceRequest.isPresent()) {
                this.notificationAnnouncer.sendActionBar(player, this.messageConfiguration.house.previousApplicationNotAcceptedYet);
                return;
            }

            if (house.getCurrentRenovation().isPresent()) {
                this.menageRenovationInventory.openInventory(player, house);
                return;
            }

            if (house.getRent().isPresent()) {
                this.apartamentRenovationInventory.openInventory(player, house);
                return;
            }

            if (house.getRegion().getHouseType() == HouseType.APARTMENT) {
                this.apartamentRenovationInventory.openInventory(player, house);
                return;
            }

            this.renovationInventory.openInventory(player, house);
        });
    }

}