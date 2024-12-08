package com.eripe14.houses.house.renovation.request.acceptance;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.inventory.impl.RenovationAcceptanceInventory;
import com.eripe14.houses.util.ItemUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class RenovationAcceptanceController implements Listener {

    private final Plugin plugin;
    private final AlertHandler alertHandler;
    private final RenovationAcceptanceService renovationAcceptanceService;
    private final RenovationAcceptanceInventory renovationAcceptanceInventory;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public RenovationAcceptanceController(
            Plugin plugin,
            AlertHandler alertHandler,
            RenovationAcceptanceService renovationAcceptanceService,
            RenovationAcceptanceInventory renovationAcceptanceInventory,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.plugin = plugin;
        this.alertHandler = alertHandler;
        this.renovationAcceptanceService = renovationAcceptanceService;
        this.renovationAcceptanceInventory = renovationAcceptanceInventory;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onFurnitureInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInUse = player.getInventory().getItemInMainHand();

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        String nbtKey = this.pluginConfiguration.renovationAcceptanceItem.nbtKey;

        if (!ItemUtil.getNbtValue(this.plugin, itemInUse, "rp_houses").equalsIgnoreCase(nbtKey)) {
            return;
        }

        if (!player.hasPermission(this.pluginConfiguration.renovationApplicationsPermission)) {
            return;
        }

        this.renovationAcceptanceInventory.openInventory(player);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission(this.pluginConfiguration.renovationApplicationsPermission)) {
            return;
        }

        if (this.renovationAcceptanceService.getRenovationAcceptanceRequests().isEmpty()) {
            return;
        }

        Alert alert = new Alert(
                player.getUniqueId(),
                this.messageConfiguration.house.renovationAcceptanceSubject,
                this.messageConfiguration.house.renovationAcceptanceMessage,
                new AlertFormatter()
        );

        this.alertHandler.sendAlertAfterTime(player, alert, this.pluginConfiguration.alertDelay);
    }

}