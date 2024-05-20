package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

public class ListOfHousesInventory extends Inventory {

    private final Scheduler scheduler;
    private final HouseService houseService;
    private final ApartamentRenovationInventory apartamentRenovationInventory;
    private final RenovationInventory renovationInventory;
    private final MenageRenovationInventory menageRenovationInventory;
    private final InventoryConfiguration inventoryConfiguration;

    public ListOfHousesInventory(
            Scheduler scheduler,
            HouseService houseService,
            ApartamentRenovationInventory apartamentRenovationInventory,
            RenovationInventory renovationInventory,
            MenageRenovationInventory menageRenovationInventory,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.scheduler = scheduler;
        this.houseService = houseService;
        this.apartamentRenovationInventory = apartamentRenovationInventory;
        this.renovationInventory = renovationInventory;
        this.menageRenovationInventory = menageRenovationInventory;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(Player player) {
        this.scheduler.async(() -> {
            InventoryConfiguration.ListOfHouses listOfHouses = this.inventoryConfiguration.listOfHouses;

            PaginatedGui gui = Gui.paginated()
                    .title(Legacy.title(listOfHouses.title))
                    .rows(listOfHouses.rows)
                    .disableAllInteractions()
                    .create();

            Formatter formatter = new Formatter();

            for (House house : this.houseService.getHousesThatUserCanRenovate(player)) {
                formatter.register("{HOUSE_ID}", house.getHouseId());

                this.addItem(gui, listOfHouses.houseItem, event -> {
                    if (house.getCurrentRenovation().isPresent()) {
                        this.menageRenovationInventory.openInventory(player, house);
                        return;
                    }

                    if (house.getRegion().getType() == HouseType.APARTMENT) {
                        this.apartamentRenovationInventory.openInventory(player, house);
                        return;
                    }

                    this.renovationInventory.openInventory(player, house);
                }, formatter);
            }

            this.setItem(gui, listOfHouses.nextPageItem, event -> {
                gui.next();
            });

            this.setItem(gui, listOfHouses.previousPageItem, event -> {
                gui.previous();
            });

            this.setItem(gui, listOfHouses.closeInventoryItem, event -> {
                player.closeInventory();
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }
}