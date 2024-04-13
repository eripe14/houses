package com.eripe14.houses.house.inventory.impl;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.Inventory;
import com.eripe14.houses.util.adventure.Legacy;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

public class PurchasedPanelInventory extends Inventory {

    public void openInventory(Player player, House house) {
        Gui gui = Gui.gui()
                .title(Legacy.title("Purchased Panel"))
                .rows(3)
                .create();

        gui.open(player);
    }

}