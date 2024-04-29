package com.eripe14.houses.house.purchase;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.hook.implementation.ItemsAdderHook;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.purchase.PurchaseService;
import com.eripe14.houses.schematic.SchematicService;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class HouseSellService {

    private final HouseService houseService;
    private final ItemsAdderHook itemsAdderHook;
    private final SchematicService schematicService;
    private final PurchaseService purchaseService;
    private final PluginConfiguration pluginConfiguration;

    public HouseSellService(
            HouseService houseService,
            ItemsAdderHook itemsAdderHook,
            SchematicService schematicService,
            PurchaseService purchaseService,
            PluginConfiguration pluginConfiguration
    ) {
        this.houseService = houseService;
        this.itemsAdderHook = itemsAdderHook;
        this.schematicService = schematicService;
        this.purchaseService = purchaseService;
        this.pluginConfiguration = pluginConfiguration;
    }

    public int sellHouse(Player owner, House house) {
        int percentOfPurchasePriceReturned = this.pluginConfiguration.percentOfPurchasePriceReturned;
        int sellPrice = house.getBuyPrice() * percentOfPurchasePriceReturned / 100;

        HouseRegion region = house.getRegion();
        World regionWorld = region.getWorld();
        Location purchaseFurnitureLocation = region.getPurchaseFurnitureLocation();
        CustomFurniture purchaseFurniture = region.getPurchaseFurniture();
        String regionDefaultSchematicName = region.getDefaultSchematicName();

        ProtectedPolygonalRegion houseRegion = region.getHouse();
        BlockVector3 houseRegionMinimumPoint = houseRegion.getMinimumPoint();

        this.houseService.resetHouse(house);
        this.itemsAdderHook.spawnCustomFurniture(purchaseFurnitureLocation, purchaseFurniture.getNamespacedID());
        this.schematicService.pasteSchematic(regionWorld, houseRegionMinimumPoint, regionDefaultSchematicName);
        this.purchaseService.depositMoney(owner, sellPrice);

        return sellPrice;
    }

}