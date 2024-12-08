package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.configuration.implementation.InventoryConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.scheduler.Scheduler;
import com.eripe14.houses.util.adventure.Legacy;
import com.eripe14.houses.util.math.Sorter;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.PaginatedGui;
import org.bukkit.entity.Player;
import panda.utilities.text.Formatter;

import java.util.List;

public class ApartmentListInventory extends Inventory {

    private final Scheduler scheduler;
    private final HouseService houseService;
    private final SelectPurchaseInventory selectPurchaseInventory;
    private final RentInventory rentInventory;
    private final InventoryConfiguration inventoryConfiguration;

    public ApartmentListInventory(
            Scheduler scheduler,
            HouseService houseService,
            SelectPurchaseInventory selectPurchaseInventory,
            RentInventory rentInventory,
            InventoryConfiguration inventoryConfiguration
    ) {
        this.scheduler = scheduler;
        this.houseService = houseService;
        this.selectPurchaseInventory = selectPurchaseInventory;
        this.rentInventory = rentInventory;
        this.inventoryConfiguration = inventoryConfiguration;
    }

    public void openInventory(Player player, House house) {
        this.scheduler.async(() -> {
            InventoryConfiguration.ApartmentBuyList apartmentBuyList = this.inventoryConfiguration.apartmentBuyList;

            Formatter formatter = new Formatter();
            formatter.register("{BLOCK_OF_FLATS}", house.getBlockOfFlatsId().replace('_', ' '));

            PaginatedGui gui = Gui.paginated()
                    .title(Legacy.title(formatter.format(apartmentBuyList.title)))
                    .rows(apartmentBuyList.rows)
                    .disableAllInteractions()
                    .create();

            List<House> houses = Sorter.sortAlphanumeric(this.houseService.getApartmentsInBlockOfFlats(house));

            for (House apartmentsInBlockOfFlat : houses) {
                formatter.register("{HOUSE_ID}", apartmentsInBlockOfFlat.getHouseId().replace("_", " "));

                this.addItem(gui, apartmentBuyList.apartmentItem, event -> {
                    if (house.getBuyPrice() == 0) {
                        this.rentInventory.openInventory(player, apartmentsInBlockOfFlat);
                        return;
                    }

                    this.selectPurchaseInventory.openInventory(player, apartmentsInBlockOfFlat);
                }, formatter);
            }

            this.setItem(gui, apartmentBuyList.nextPageItem, event -> {
                gui.next();
            });

            this.setItem(gui, apartmentBuyList.previousPageItem, event -> {
                gui.previous();
            });

            this.setItem(gui, apartmentBuyList.closeInventoryItem, event -> {
                gui.close(player);
            });

            this.scheduler.sync(() -> gui.open(player));
        });
    }
}