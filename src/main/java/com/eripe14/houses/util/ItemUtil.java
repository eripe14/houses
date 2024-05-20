package com.eripe14.houses.util;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class ItemUtil {

    private ItemUtil() { }

    public static String getNbtValue(Plugin plugin, ItemStack itemStack, String key) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return "-";
        }

        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        String value = persistentDataContainer.get(namespacedKey, PersistentDataType.STRING);

        if (value == null) {
            return "-";
        }

        return value;
    }

    public static boolean hasItem(Player player, ItemStack itemStack) {
        return player.getInventory().containsAtLeast(itemStack, 1);
    }

}