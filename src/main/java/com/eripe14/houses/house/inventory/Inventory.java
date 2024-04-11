package com.eripe14.houses.house.inventory;

import com.eripe14.houses.configuration.implementation.ItemConfiguration;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import panda.utilities.text.Formatter;

public abstract class Inventory {

    public void setItem(BaseGui gui, ItemConfiguration item, GuiAction<InventoryClickEvent> action) {
        gui.setItem(item.slot, item.asGuiItem(action));
    }

    public void setItem(BaseGui gui, ItemConfiguration item, GuiAction<InventoryClickEvent> action, Formatter... formatters) {
        gui.setItem(item.slot, item.asGuiItem(action, formatters));
    }

    public void setSkullItem(BaseGui gui, ItemConfiguration item, OfflinePlayer player, GuiAction<InventoryClickEvent> action, Formatter... formatters) {
        gui.setItem(item.slot, item.asGuiItemSkull(action, player, formatters));
    }

    public void updateGui(BaseGui gui, ItemConfiguration item, Formatter... formatters) {
        gui.updateItem(item.slot, item.asGuiItem(formatters));
    }

}