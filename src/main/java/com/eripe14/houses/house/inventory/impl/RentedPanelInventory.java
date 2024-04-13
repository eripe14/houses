package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.inventory.action.impl.AddCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.AddPlayerAction;
import com.eripe14.houses.house.inventory.action.impl.ChangeOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.ChangePermissionsAction;
import com.eripe14.houses.house.inventory.action.impl.ExtendRentAction;
import com.eripe14.houses.house.inventory.action.impl.RemoveCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.RemovePlayerAction;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.DurationUtil;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.UUID;

public class RentedPanelInventory extends Inventory {

    private final Scheduler scheduler;
    private final AddPlayerAction addPlayerAction;
    private final AddCoOwnerAction addCoOwnerAction;
    private final RemovePlayerAction removePlayerAction;
    private final RemoveCoOwnerAction removeCoOwnerAction;
    private final ChangePermissionsAction changePermissionsAction;
    private final ExtendRentAction extendRentAction;
    private final ChangeOwnerAction changeOwnerAction;
    private final InventoryConfiguration inventoryConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public RentedPanelInventory(
            Scheduler scheduler,
            AddPlayerAction addPlayerAction,
            AddCoOwnerAction addCoOwnerAction,
            RemovePlayerAction removePlayerAction,
            RemoveCoOwnerAction removeCoOwnerAction,
            ChangePermissionsAction changePermissionsAction,
            ExtendRentAction extendRentAction, ChangeOwnerAction changeOwnerAction,
            InventoryConfiguration inventoryConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.scheduler = scheduler;
        this.addPlayerAction = addPlayerAction;
        this.addCoOwnerAction = addCoOwnerAction;
        this.removePlayerAction = removePlayerAction;
        this.removeCoOwnerAction = removeCoOwnerAction;
        this.changePermissionsAction = changePermissionsAction;
        this.extendRentAction = extendRentAction;
        this.changeOwnerAction = changeOwnerAction;
        this.inventoryConfiguration = inventoryConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            UUID playerUuid = player.getUniqueId();
            InventoryConfiguration.RentedPanel rentedPanel = this.inventoryConfiguration.rentedPanel;

            Formatter formatter = new Formatter();
            formatter.register("{TIME}", DurationUtil.format(this.pluginConfiguration.timeToConfirmHouseInvite));
            formatter.register("{OWNER}", player.getName());

            Gui gui = Gui.gui()
                    .title(Legacy.title(rentedPanel.title))
                    .rows(rentedPanel.rows)
                    .disableAllInteractions()
                    .create();

            if (rentedPanel.fillEmptySlots) {
                gui.getFiller().fill(rentedPanel.filler.asGuiItem());
            }

            this.setItem(gui, rentedPanel.addPlayerToHouse, (event) -> {
                this.addPlayerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.removePlayerFromHouse, (event) -> {
                this.removePlayerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.addCoOwner, (event) -> {
                this.addCoOwnerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.removeCoOwner, (event) -> {
                this.removeCoOwnerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.changePlayerPermissions, (event) -> {
                this.changePermissionsAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.extendRent, (event) -> {
                this.extendRentAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.changeOwner, (event) -> {
                this.changeOwnerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.changePanelObjectLocation, (event) -> {

            });

            this.setItem(gui, rentedPanel.closeInventoryItem, (event) -> {
                player.closeInventory();
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}