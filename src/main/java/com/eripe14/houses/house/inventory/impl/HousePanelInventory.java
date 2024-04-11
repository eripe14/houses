package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.inventory.action.impl.AddCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.AddPlayerAction;
import com.eripe14.houses.house.inventory.action.impl.ChangePermissionsAction;
import com.eripe14.houses.house.inventory.action.impl.RemoveCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.RemovePlayerAction;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.DurationUtil;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.UUID;

public class HousePanelInventory extends Inventory {

    private final Scheduler scheduler;
    private final AddPlayerAction addPlayerAction;
    private final AddCoOwnerAction addCoOwnerAction;
    private final RemovePlayerAction removePlayerAction;
    private final RemoveCoOwnerAction removeCoOwnerAction;
    private final ChangePermissionsAction changePermissionsAction;
    private final ConfirmInventory confirmInventory;
    private final InventoryConfiguration inventoryConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public HousePanelInventory(
            Scheduler scheduler,
            AddPlayerAction addPlayerAction,
            AddCoOwnerAction addCoOwnerAction,
            RemovePlayerAction removePlayerAction,
            RemoveCoOwnerAction removeCoOwnerAction,
            ChangePermissionsAction changePermissionsAction,
            ConfirmInventory confirmInventory, MessageConfiguration messageConfiguration,
            InventoryConfiguration inventoryConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.scheduler = scheduler;
        this.addPlayerAction = addPlayerAction;
        this.addCoOwnerAction = addCoOwnerAction;
        this.removePlayerAction = removePlayerAction;
        this.removeCoOwnerAction = removeCoOwnerAction;
        this.changePermissionsAction = changePermissionsAction;
        this.confirmInventory = confirmInventory;
        this.inventoryConfiguration = inventoryConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            UUID playerUuid = player.getUniqueId();
            InventoryConfiguration.Panel panel = this.inventoryConfiguration.panel;

            Formatter formatter = new Formatter();
            formatter.register("{TIME}", DurationUtil.format(this.pluginConfiguration.timeToConfirmHouseInvite));
            formatter.register("{OWNER}", player.getName());

            Gui gui = Gui.gui()
                    .title(Legacy.title(panel.title))
                    .rows(panel.rows)
                    .disableAllInteractions()
                    .create();

            if (panel.fillEmptySlots) {
                gui.getFiller().fill(panel.filler.asGuiItem());
            }

            this.setItem(gui, panel.addPlayerToHouse, (event) -> {
                this.addPlayerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, panel.removePlayerFromHouse, (event) -> {
                this.removePlayerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, panel.addCoOwner, (event) -> {
                this.addCoOwnerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, panel.removeCoOwner, (event) -> {
                this.removeCoOwnerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, panel.changePlayerPermissions, (event) -> {
                this.changePermissionsAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, panel.extendRent, (event) -> {

            });

            this.setItem(gui, panel.changeOwner, (event) -> {

            });

            this.setItem(gui, panel.sellHouse, (event) -> {

            });

            this.setItem(gui, panel.closeInventoryItem, (event) -> {
                player.closeInventory();
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}