package com.eripe14.houses.house.inventory.action.impl;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.inventory.action.InventoryClickAction;
import com.eripe14.houses.house.inventory.impl.ExtendRentInventory;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class ExtendRentAction implements InventoryClickAction {

    private final ExtendRentInventory extendRentInventory;

    public ExtendRentAction(ExtendRentInventory extendRentInventory) {
        this.extendRentInventory = extendRentInventory;
    }

    @Override
    public Consumer<UUID> clickAction(Player player, House house, Gui gui) {
        return (uuid) -> this.extendRentInventory.openInventory(player, house);
    }
}