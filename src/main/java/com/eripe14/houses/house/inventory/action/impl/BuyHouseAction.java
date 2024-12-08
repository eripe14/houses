package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ConfirmInventory;
import com.eripe14.houses.house.purchase.HousePurchaseService;
import com.eripe14.houses.house.rent.RentService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.UUID;
import java.util.function.Consumer;

public class BuyHouseAction implements InventoryClickAction {

    private final HousePurchaseService housePurchaseService;
    private final HouseService houseService;
    private final RentService rentService;
    private final ConfirmInventory confirmInventory;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final InventoryConfiguration inventoryConfiguration;

    public BuyHouseAction(
            HousePurchaseService housePurchaseService,
            HouseService houseService,
            RentService rentService,
            ConfirmInventory confirmInventory,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.housePurchaseService = housePurchaseService;
        this.houseService = houseService;
        this.rentService = rentService;
        this.confirmInventory = confirmInventory;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        Formatter formatter = new Formatter();
        formatter.register("{HOUSE_ID}", house.getHouseId().replace("_", " "));
        formatter.register("{BUY_PRICE}", house.getBuyPrice());

        Consumer<UUID> acceptAction = (uuid) -> {
            if (this.housePurchaseService.purchaseRentedHouse(player, house)) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.boughtHouse, formatter);
                this.houseService.removeRent(house);
                this.rentService.removeRent(house.getHouseId());

                gui.close(player);
                return;
            }

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.notEnoughMoneyToBuy);
            gui.close(player);
        };

        Consumer<Player> declineAction = (secondPlayerVariable) -> gui.close(player);

        return (uuid) -> {
            this.confirmInventory.openInventory(
                    player,
                    this.inventoryConfiguration.confirm.confirmBuyTitle,
                    Option.none(),
                    this.inventoryConfiguration.confirm.buyHouseAdditionalItem,
                    acceptAction,
                    declineAction,
                    formatter
            );
        };
    }

}