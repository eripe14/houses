package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.inventory.action.impl.AddCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.AddPlayerAction;
import com.eripe14.houses.house.inventory.action.impl.BuyHouseAction;
import com.eripe14.houses.house.inventory.action.impl.ChangeOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.ChangePermissionsAction;
import com.eripe14.houses.house.inventory.action.impl.ExtendRentAction;
import com.eripe14.houses.house.inventory.action.impl.RemoveCoOwnerAction;
import com.eripe14.houses.house.inventory.action.impl.RemovePlayerAction;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.notification.NotificationAnnouncer;
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
    private final BuyHouseAction buyHouseAction;
    private final HouseMemberService houseMemberService;
    private final MenageRenovationInventory menageRenovationInventory;
    private final InventoryConfiguration inventoryConfiguration;
    private final NotificationAnnouncer notificationAnnouncer;
    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration messageConfiguration;

    public RentedPanelInventory(
            Scheduler scheduler,
            AddPlayerAction addPlayerAction,
            AddCoOwnerAction addCoOwnerAction,
            RemovePlayerAction removePlayerAction,
            RemoveCoOwnerAction removeCoOwnerAction,
            ChangePermissionsAction changePermissionsAction,
            ExtendRentAction extendRentAction,
            ChangeOwnerAction changeOwnerAction,
            BuyHouseAction buyHouseAction,
            HouseMemberService houseMemberService,
            MenageRenovationInventory menageRenovationInventory,
            InventoryConfiguration inventoryConfiguration,
            NotificationAnnouncer notificationAnnouncer,
            PluginConfiguration pluginConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.scheduler = scheduler;
        this.addPlayerAction = addPlayerAction;
        this.addCoOwnerAction = addCoOwnerAction;
        this.removePlayerAction = removePlayerAction;
        this.removeCoOwnerAction = removeCoOwnerAction;
        this.changePermissionsAction = changePermissionsAction;
        this.extendRentAction = extendRentAction;
        this.changeOwnerAction = changeOwnerAction;
        this.buyHouseAction = buyHouseAction;
        this.houseMemberService = houseMemberService;
        this.menageRenovationInventory = menageRenovationInventory;
        this.inventoryConfiguration = inventoryConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
        this.pluginConfiguration = pluginConfiguration;
        this.messageConfiguration = messageConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            UUID playerUuid = player.getUniqueId();
            Owner owner = house.getOwner().get();
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
            }, formatter);

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
                if (!owner.getUuid().equals(playerUuid)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.youNeedToBeOwner);
                    return;
                }

                this.extendRentAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.changeOwner, (event) -> {
                if (!owner.getUuid().equals(playerUuid)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.youNeedToBeOwner);
                    return;
                }

                this.changeOwnerAction.clickAction(player, house, gui).accept(playerUuid);
            });

            this.setItem(gui, rentedPanel.endRenovation, (event) -> {
                if (!owner.getUuid().equals(playerUuid)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.youNeedToBeOwnerOrCoOwner);
                    return;
                }

                if (!this.houseMemberService.isCoOwner(house, playerUuid) && !owner.getUuid().equals(playerUuid)) {
                    this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.youNeedToBeOwnerOrCoOwner);
                    return;
                }

                this.menageRenovationInventory.openInventory(player, house);
            });

            if (house.getBuyPrice() != 0) {
                this.setItem(gui, rentedPanel.buyHouse, (event) -> {
                    if (!owner.getUuid().equals(playerUuid)) {
                        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.youNeedToBeOwner);
                        return;
                    }

                    this.buyHouseAction.clickAction(player, house, gui).accept(playerUuid);
                });
            }

            this.setItem(gui, rentedPanel.closeInventoryItem, (event) -> {
                player.closeInventory();
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }

}