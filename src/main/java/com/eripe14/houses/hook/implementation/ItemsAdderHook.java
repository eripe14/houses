package com.eripe14.houses.hook.implementation;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

public class ItemsAdderHook {

    public void addItemsAdderFurniture(Player player, String furnitureNamespacedKey) {
        ItemStack itemStack = CustomStack.getInstance(furnitureNamespacedKey).getItemStack();

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItem(player.getLocation(), itemStack);
            return;
        }

        player.getInventory().addItem(itemStack);
    }

    public CustomFurniture spawnCustomFurniture(Location location, String furnitureNamespacedKey) {
        return CustomFurniture.spawn(furnitureNamespacedKey, location.getBlock());
    }

    public Option<CustomStack> getCustomStack(ItemStack itemStack) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);

        return Option.of(customStack).orElse(Option::none);
    }

    public boolean isItemsAdderFurniture(String furnitureNamespacedKey) {
        return CustomFurniture.isInRegistry(furnitureNamespacedKey);
    }

    public boolean isItemsAdderCustomRecipe(ItemStack itemStack) {
        CustomStack customStack = CustomFurniture.byItemStack(itemStack);

        if (customStack == null) {
            return false;
        }

        return CustomFurniture.isInRegistry(customStack.getNamespacedID());
    }

}