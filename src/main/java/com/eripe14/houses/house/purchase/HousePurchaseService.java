package com.eripe14.houses.house.purchase;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.purchase.PurchaseService;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.entity.Player;

public class HousePurchaseService {

    private final HouseService houseService;
    private final PurchaseService purchaseService;
    private final PluginConfiguration pluginConfiguration;

    public HousePurchaseService(HouseService houseService, PurchaseService purchaseService, PluginConfiguration pluginConfiguration) {
        this.houseService = houseService;
        this.purchaseService = purchaseService;
        this.pluginConfiguration = pluginConfiguration;
    }

    public synchronized boolean purchaseHouse(Player player, House house) {
        Owner owner = new Owner(player.getUniqueId(), player.getName());
        house.setOwner(owner);

        if (!this.purchaseService.hasEnoughMoney(player, house.getBuyPrice())) {
            return false;
        }

        this.houseService.addHouse(house);
        this.purchaseService.withdrawMoney(player, house.getBuyPrice());

        this.addPanelItem(player);
        this.killPurchaseFurniture(house);

        return true;
    }

    public void addPanelItem(Player player) {
        CustomStack panelStack = CustomFurniture.getInstance(this.pluginConfiguration.itemsAdderHousePanelNamespacedId);

        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), panelStack.getItemStack());
            return;
        }

        player.getInventory().addItem(panelStack.getItemStack());
    }

    public void killPurchaseFurniture(House house) {
        CustomFurniture purchaseFurniture = house.getRegion().getPurchaseFurniture();

        purchaseFurniture.remove(false);
    }

}