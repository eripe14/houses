package com.eripe14.houses.house.purchase;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.hook.implementation.ItemsAdderHook;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.furniture.HouseCustomFurniture;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.region.RegionService;
import com.eripe14.houses.house.renovation.Renovation;
import com.eripe14.houses.house.renovation.RenovationService;
import com.eripe14.houses.house.renovation.request.RenovationRequest;
import com.eripe14.houses.house.renovation.request.RenovationRequestService;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceRequest;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceService;
import com.eripe14.houses.position.PositionAdapter;
import com.eripe14.houses.purchase.PurchaseService;
import com.eripe14.houses.schematic.SchematicService;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import panda.std.Option;

public class HouseSellService {

    private final HouseService houseService;
    private final RenovationService renovationService;
    private final RenovationRequestService renovationRequestService;
    private final RenovationAcceptanceService renovationAcceptanceService;
    private final ItemsAdderHook itemsAdderHook;
    private final SchematicService schematicService;
    private final PurchaseService purchaseService;
    private final RegionService regionService;
    private final PluginConfiguration pluginConfiguration;

    public HouseSellService(
            HouseService houseService,
            RenovationService renovationService,
            RenovationRequestService renovationRequestService,
            RenovationAcceptanceService renovationAcceptanceService,
            ItemsAdderHook itemsAdderHook,
            SchematicService schematicService,
            PurchaseService purchaseService,
            RegionService regionService,
            PluginConfiguration pluginConfiguration
    ) {
        this.houseService = houseService;
        this.renovationService = renovationService;
        this.renovationRequestService = renovationRequestService;
        this.renovationAcceptanceService = renovationAcceptanceService;
        this.itemsAdderHook = itemsAdderHook;
        this.schematicService = schematicService;
        this.purchaseService = purchaseService;
        this.regionService = regionService;
        this.pluginConfiguration = pluginConfiguration;
    }

    public int getRefundMoney(Player owner, House house) {
        int percentOfPurchasePriceReturned = this.pluginConfiguration.percentOfPurchasePriceReturned;

        return house.getBuyPrice() * percentOfPurchasePriceReturned / 100;
    }

    public void sellHouse(Player owner, House house) {
        HouseRegion region = house.getRegion();
        World regionWorld = region.getWorld();
        Location purchaseFurnitureLocation = PositionAdapter.convert(region.getPurchaseFurniture().getPosition());
        HouseCustomFurniture purchaseFurniture = region.getPurchaseFurniture();
        String regionDefaultSchematicName = region.getDefaultSchematicName();

        ProtectedPolygonalRegion houseRegion = region.getHouse();
        BlockVector3 houseRegionMinimumPoint = houseRegion.getMinimumPoint();

        Option<RenovationRequest> renovationRequest = this.renovationRequestService.getRequest(house.getHouseId());
        Option<Renovation> renovation = this.renovationService.getRenovation(house.getHouseId());
        Option<RenovationAcceptanceRequest> renovationAcceptanceRequest = this.renovationAcceptanceService.getRenovationAcceptanceRequest(house);

        if (renovationRequest.isPresent()) {
            this.renovationRequestService.removeRequest(renovationRequest.get().getHouseId());
        }

        if (renovation.isPresent()) {
            this.renovationService.removeRenovation(renovation.get());
        }

        if (renovationAcceptanceRequest.isPresent()) {
            this.renovationAcceptanceService.removeRenovationAcceptanceRequest(renovationAcceptanceRequest.get().getHouseId());
        }

        this.houseService.resetHouse(house);
        this.regionService.resetRegion(region);
        this.itemsAdderHook.spawnCustomFurniture(purchaseFurnitureLocation, purchaseFurniture.getNamespacedId());
        this.schematicService.pasteSchematic(regionWorld, houseRegionMinimumPoint, regionDefaultSchematicName);
        this.purchaseService.depositMoney(owner, this.getRefundMoney(owner, house));
    }

    public void endRent(House house) {
        HouseRegion region = house.getRegion();
        World regionWorld = region.getWorld();
        Location purchaseFurnitureLocation = PositionAdapter.convert(region.getPurchaseFurniture().getPosition());
        HouseCustomFurniture purchaseFurniture = region.getPurchaseFurniture();
        String regionDefaultSchematicName = region.getDefaultSchematicName();

        ProtectedPolygonalRegion houseRegion = region.getHouse();
        BlockVector3 houseRegionMinimumPoint = houseRegion.getMinimumPoint();

        Option<RenovationRequest> renovationRequest = this.renovationRequestService.getRequest(house.getHouseId());
        Option<Renovation> renovation = this.renovationService.getRenovation(house.getHouseId());
        Option<RenovationAcceptanceRequest> renovationAcceptanceRequest = this.renovationAcceptanceService.getRenovationAcceptanceRequest(house);

        if (renovationRequest.isPresent()) {
            this.renovationRequestService.removeRequest(renovationRequest.get().getHouseId());
        }

        if (renovation.isPresent()) {
            this.renovationService.removeRenovation(renovation.get());
        }

        if (renovationAcceptanceRequest.isPresent()) {
            this.renovationAcceptanceService.removeRenovationAcceptanceRequest(renovationAcceptanceRequest.get().getHouseId());
        }

        this.houseService.resetHouse(house);
        this.regionService.resetRegion(region);
        this.itemsAdderHook.spawnCustomFurniture(purchaseFurnitureLocation, purchaseFurniture.getNamespacedId());
        this.schematicService.pasteSchematic(regionWorld, houseRegionMinimumPoint, regionDefaultSchematicName);
    }

}