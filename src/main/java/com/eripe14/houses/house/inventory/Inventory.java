package com.eripe14.houses.house.inventory;

import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.configuration.implementation.ItemConfiguration;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.BaseGui;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import panda.utilities.text.Formatter;

public abstract class Inventory {

    public void setItem(BaseGui gui, ItemConfiguration item, GuiAction<InventoryClickEvent> action) {
        gui.setItem(item.slot, item.asGuiItem(action, new Formatter()));
    }

    public void setItem(BaseGui gui, ItemConfiguration item, GuiAction<InventoryClickEvent> action, Formatter... formatters) {
        gui.setItem(item.slot, item.asGuiItem(action, formatters));
    }

    public void setSkullItem(BaseGui gui, ItemConfiguration item, OfflinePlayer player, GuiAction<InventoryClickEvent> action, Formatter... formatters) {
        gui.setItem(item.slot, item.asGuiItemSkull(action, player, formatters));
    }

    public void setItem(BaseGui gui, ItemConfiguration item, GuiAction<InventoryClickEvent> action, AlertFormatter... formatters) {
        gui.setItem(item.slot, item.asGuiItem(action, formatters));
    }

    public void setSkullItem(BaseGui gui, ItemConfiguration item, OfflinePlayer player, GuiAction<InventoryClickEvent> action, AlertFormatter... formatters) {
        gui.setItem(item.slot, item.asGuiItemSkull(action, player, formatters));
    }

    public void addItem(BaseGui gui, ItemConfiguration item, GuiAction<InventoryClickEvent> action, Formatter... formatters) {
        gui.addItem(item.asGuiItem(action, formatters));
    }

    public void addSkullItem(BaseGui gui, ItemConfiguration item, OfflinePlayer player, GuiAction<InventoryClickEvent> action, Formatter... formatters) {
        gui.addItem(item.asGuiItemSkull(action, player, formatters));
    }

    public void addItem(BaseGui gui, ItemConfiguration item, GuiAction<InventoryClickEvent> action, AlertFormatter... formatters) {
        gui.addItem(item.asGuiItem(action, formatters));
    }

    public void addSkullItem(BaseGui gui, ItemConfiguration item, OfflinePlayer player, GuiAction<InventoryClickEvent> action, AlertFormatter... formatters) {
        gui.addItem(item.asGuiItemSkull(action, player, formatters));
    }

}