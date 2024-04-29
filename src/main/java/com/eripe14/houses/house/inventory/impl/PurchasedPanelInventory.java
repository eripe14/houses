package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.inventory.action.impl.AddCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.AddPlayerAction;
import com.eripe14.houses.house.inventory.action.impl.ChangeOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.ChangePermissionsAction;
import com.eripe14.houses.house.inventory.action.impl.RemoveCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.RemovePlayerAction;
import com.eripe14.houses.house.inventory.action.impl.SellHouseAction;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PurchasedPanelInventory extends Inventory {

    private final Scheduler scheduler;
    private final AddPlayerAction addPlayerAction;
    private final AddCoOwnerAction addCoOwnerAction;
    private final RemovePlayerAction removePlayerAction;
    private final RemoveCoOwnerAction removeCoOwnerAction;
    private final ChangePermissionsAction changePermissionsAction;
    private final ChangeOwnerAction changeOwnerAction;
    private final SellHouseAction sellHouseAction;
    private final NotificationAnnouncer notificationAnnouncer;
    private final InventoryConfiguration inventoryConfiguration;

    private final MessageConfiguration messageConfiguration;

    public PurchasedPanelInventory(
            Scheduler scheduler,
            AddPlayerAction addPlayerAction,
            AddCoOwnerAction addCoOwnerAction,
            RemovePlayerAction removePlayerAction,
            RemoveCoOwnerAction removeCoOwnerAction,
            ChangePermissionsAction changePermissionsAction,
            ChangeOwnerAction changeOwnerAction,
            SellHouseAction sellHouseAction,
            NotificationAnnouncer notificationAnnouncer,
            InventoryConfiguration inventoryConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.scheduler = scheduler;
        this.addPlayerAction = addPlayerAction;
        this.addCoOwnerAction = addCoOwnerAction;
        this.removePlayerAction = removePlayerAction;
        this.removeCoOwnerAction = removeCoOwnerAction;
        this.changePermissionsAction = changePermissionsAction;
        this.changeOwnerAction = changeOwnerAction;
        this.sellHouseAction = sellHouseAction;
        this.notificationAnnouncer = notificationAnnouncer;
        this.inventoryConfiguration = inventoryConfiguration;
        this.messageConfiguration = messageConfiguration;
    }

    public void openInventory(Player player, House house) {
        UUID playerUuid = player.getUniqueId();
        Owner owner = house.getOwner().get();
        InventoryConfiguration.PurchasedPanel purchasedPanel = this.inventoryConfiguration.purchasedPanel;

        Gui gui = Gui.gui()
                .title(Legacy.title(purchasedPanel.title))
                .rows(purchasedPanel.rows)
                .disableAllInteractions()
                .create();

        if (purchasedPanel.fillEmptySlots) {
            gui.getFiller().fill(purchasedPanel.filler.asGuiItem());
        }

        this.setItem(gui, purchasedPanel.addPlayerToHouse, (event) -> {
            this.addPlayerAction.clickAction(player, house, gui).accept(playerUuid);
        });

        this.setItem(gui, purchasedPanel.removePlayerFromHouse, (event) -> {
            this.removePlayerAction.clickAction(player, house, gui).accept(playerUuid);
        });

        this.setItem(gui, purchasedPanel.addCoOwner, (event) -> {
            this.addCoOwnerAction.clickAction(player, house, gui).accept(playerUuid);
        });

        this.setItem(gui, purchasedPanel.removeCoOwner, (event) -> {
            this.removeCoOwnerAction.clickAction(player, house, gui).accept(playerUuid);
        });

        this.setItem(gui, purchasedPanel.changePlayerPermissions, (event) -> {
            this.changePermissionsAction.clickAction(player, house, gui).accept(playerUuid);
        });

        this.setItem(gui, purchasedPanel.sell, (event) -> {
            if (!owner.getUuid().equals(playerUuid)) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.youNeedToBeOwner);
                return;
            }

            this.sellHouseAction.clickAction(player, house, gui).accept(playerUuid);
        });

        this.setItem(gui, purchasedPanel.changeOwner, (event) -> {
            if (!owner.getUuid().equals(playerUuid)) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.youNeedToBeOwner);
                return;
            }

            this.changeOwnerAction.clickAction(player, house, gui).accept(playerUuid);
        });

        this.setItem(gui, purchasedPanel.closeInventoryItem, (event) -> {
            player.closeInventory();
        });

        gui.open(player);
    }

}