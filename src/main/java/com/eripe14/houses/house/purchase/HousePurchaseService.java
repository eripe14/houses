package com.eripe14.houses.house.purchase;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.furniture.HouseCustomFurniture;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.RegionService;
import com.eripe14.houses.position.PositionAdapter;
import com.eripe14.houses.purchase.PurchaseService;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class HousePurchaseService {

    private final HouseService houseService;
    private final RegionService regionService;
    private final PurchaseService purchaseService;
    private final PluginConfiguration pluginConfiguration;

    public HousePurchaseService(
            HouseService houseService,
            RegionService regionService,
            PurchaseService purchaseService,
            PluginConfiguration pluginConfiguration
    ) {
        this.houseService = houseService;
        this.regionService = regionService;
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
        this.regionService.resetRegion(house.getRegion());

        this.addPanelItem(player);

        return true;
    }

    public synchronized boolean purchaseRentedHouse(Player player, House house) {
        Owner owner = new Owner(player.getUniqueId(), player.getName());
        house.setOwner(owner);

        if (!this.purchaseService.hasEnoughMoney(player, house.getBuyPrice())) {
            return false;
        }

        this.houseService.addHouse(house);
        this.purchaseService.withdrawMoney(player, house.getBuyPrice());

        this.addPanelItem(player);

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
        HouseCustomFurniture purchaseFurniture = house.getRegion().getPurchaseFurniture();

        for (ArmorStand armorStand : house.getRegion().getWorld().getEntitiesByClass(ArmorStand.class)) {
            Location armorStandLocation = armorStand.getLocation().clone();

            if (
                    !PositionAdapter.compareLocations(armorStandLocation, PositionAdapter.convertFurniture(purchaseFurniture.getPosition())) ||
                    !PositionAdapter.compareLocations(armorStandLocation, PositionAdapter.convert(purchaseFurniture.getPosition()))
            ) {
                continue;
            }

            CustomFurniture customFurniture = CustomFurniture.byAlreadySpawned(armorStand);
            customFurniture.remove(false);
        }
    }

}