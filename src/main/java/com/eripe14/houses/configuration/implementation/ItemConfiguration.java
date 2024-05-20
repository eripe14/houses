package com.eripe14.houses.configuration.implementation;

import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.util.adventure.Legacy;
import com.eripe14.houses.util.adventure.LegacyColorProcessor;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import net.dzikoysk.cdn.entity.Contextual;
import net.dzikoysk.cdn.entity.Exclude;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import panda.utilities.text.Formatter;

import java.util.List;
import java.util.stream.Collectors;

@Contextual
public class ItemConfiguration {

    @Exclude
    private final MiniMessage miniMessage = MiniMessage.builder()
            .postProcessor(new LegacyColorProcessor())
            .build();

    public String nbtKey;

    public int model;

    public int slot;

    public String itemName;

    public List<String> itemLore;

    public List<ItemFlag> itemFlags;

    public Material itemMaterial;

    public boolean itemGlow;

    public ItemConfiguration() {
    }

    public ItemConfiguration(int slot, String itemName, List<String> itemLore, List<ItemFlag> itemFlags, Material itemMaterial, boolean itemGlow) {
        this.model = 0;
        this.nbtKey = "-";
        this.slot = slot;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.itemFlags = itemFlags;
        this.itemMaterial = itemMaterial;
        this.itemGlow = itemGlow;
    }

    public ItemConfiguration(int slot, int model, String itemName, List<String> itemLore, List<ItemFlag> itemFlags, Material itemMaterial, boolean itemGlow) {
        this.model = model;
        this.nbtKey = "-";
        this.slot = slot;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.itemFlags = itemFlags;
        this.itemMaterial = itemMaterial;
        this.itemGlow = itemGlow;
    }

    public ItemConfiguration(String nbtKey, String itemName, List<String> itemLore, List<ItemFlag> itemFlags, Material itemMaterial, boolean itemGlow) {
        this.model = 0;
        this.nbtKey = nbtKey;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.itemMaterial = itemMaterial;
        this.itemFlags = itemFlags;
        this.itemGlow = itemGlow;
    }

    public ItemConfiguration(int model, String nbtKey, String itemName, List<String> itemLore, List<ItemFlag> itemFlags, Material itemMaterial, boolean itemGlow) {
        this.model = model;
        this.nbtKey = nbtKey;
        this.itemName = itemName;
        this.itemLore = itemLore;
        this.itemMaterial = itemMaterial;
        this.itemFlags = itemFlags;
        this.itemGlow = itemGlow;
    }

    public GuiItem asGuiItem(Formatter... formatters) {
        return this.asGuiItem(event -> {
        }, formatters);
    }

    public GuiItem asGuiItem(GuiAction<InventoryClickEvent> action, Formatter... formatters) {
        String tempName = this.itemName;
        List<String> tempLore = this.itemLore;

        for (Formatter formatter : formatters) {
            tempName = formatter.format(tempName);
            tempLore = tempLore.stream().map(formatter::format).toList();
        }

        Component name = Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(tempName.replace('_', ' ')));
        List<Component> formattedLore = tempLore.stream()
                .map(input -> Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(input)))
                .collect(Collectors.toList());
        ItemFlag[] itemFlags = this.itemFlags.toArray(new ItemFlag[0]);

        return ItemBuilder.from(this.itemMaterial)
                .name(name)
                .amount(1)
                .model(this.model)
                .lore(formattedLore)
                .flags(itemFlags)
                .glow(this.itemGlow)
                .setNbt("rp_houses", this.nbtKey == null ? "none" : this.nbtKey)
                .asGuiItem(action);
    }

    public GuiItem asGuiItem(GuiAction<InventoryClickEvent> action, AlertFormatter... formatters) {
        String tempName = this.itemName;
        List<String> tempLore = this.itemLore;

        for (AlertFormatter formatter : formatters) {
            tempName = formatter.format(tempName);
            tempLore = tempLore.stream().map(formatter::format).toList();
        }

        Component name = Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(tempName));
        List<Component> formattedLore = tempLore.stream()
                .map(input -> Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(input)))
                .collect(Collectors.toList());
        ItemFlag[] itemFlags = this.itemFlags.toArray(new ItemFlag[0]);

        return ItemBuilder.from(this.itemMaterial)
                .name(name)
                .model(this.model)
                .lore(formattedLore)
                .flags(itemFlags)
                .glow(this.itemGlow)
                .setNbt("rp_houses", this.nbtKey == null ? "none" : this.nbtKey)
                .asGuiItem(action);
    }

    public GuiItem asGuiItemSkull(GuiAction<InventoryClickEvent> action, OfflinePlayer player, Formatter... formatters) {
        String tempName = this.itemName;
        List<String> tempLore = this.itemLore;

        for (Formatter formatter : formatters) {
            tempName = formatter.format(tempName);
            tempLore = tempLore.stream().map(formatter::format).collect(Collectors.toList());
        }

        Component name = Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(tempName));
        List<Component> formattedLore = tempLore.stream()
                .map(input -> Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(input)))
                .collect(Collectors.toList());
        ItemFlag[] itemFlags = this.itemFlags.toArray(new ItemFlag[0]);

        if (this.itemMaterial != Material.PLAYER_HEAD) {
            throw new IllegalArgumentException("Material must be PLAYER_HEAD");
        }

        return ItemBuilder.skull()
                .owner(player)
                .model(this.model)
                .name(name)
                .lore(formattedLore)
                .flags(itemFlags)
                .glow(this.itemGlow)
                .setNbt("rp_houses", this.nbtKey)
                .asGuiItem(action);
    }

    public GuiItem asGuiItemSkull(GuiAction<InventoryClickEvent> action, OfflinePlayer player, AlertFormatter... formatters) {
        String tempName = this.itemName;
        List<String> tempLore = this.itemLore;

        for (AlertFormatter formatter : formatters) {
            tempName = formatter.format(tempName);
            tempLore = tempLore.stream().map(formatter::format).collect(Collectors.toList());
        }

        Component name = Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(tempName));
        List<Component> formattedLore = tempLore.stream()
                .map(input -> Legacy.RESET_ITALIC.append(this.miniMessage.deserialize(input)))
                .collect(Collectors.toList());
        ItemFlag[] itemFlags = this.itemFlags.toArray(new ItemFlag[0]);

        if (this.itemMaterial != Material.PLAYER_HEAD) {
            throw new IllegalArgumentException("Material must be PLAYER_HEAD");
        }

        return ItemBuilder.skull()
                .owner(player)
                .name(name)
                .model(this.model)
                .lore(formattedLore)
                .flags(itemFlags)
                .glow(this.itemGlow)
                .setNbt("rp_houses", this.nbtKey)
                .asGuiItem(action);
    }

}