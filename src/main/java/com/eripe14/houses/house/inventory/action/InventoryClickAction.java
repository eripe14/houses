package com.eripe14.houses.house.inventory.action;

import com.eripe14.houses.house.House;
import dev.triumphteam.gui.guis.Gui;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public interface InventoryClickAction {

    Consumer<UUID> clickAction(Player player, House house, Gui gui);

}