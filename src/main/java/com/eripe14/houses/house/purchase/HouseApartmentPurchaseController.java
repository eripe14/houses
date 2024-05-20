package com.eripe14.houses.house.purchase;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.impl.ApartmentListInventory;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class HouseApartmentPurchaseController implements Listener {

    private final ApartmentListInventory apartmentListInventory;
    private final ProtectionService protectionService;
    private final HouseService houseService;
    private final PluginConfiguration pluginConfiguration;

    public HouseApartmentPurchaseController(
            ApartmentListInventory apartmentListInventory,
            ProtectionService protectionService,
            HouseService houseService,
            PluginConfiguration pluginConfiguration
    ) {
        this.apartmentListInventory = apartmentListInventory;
        this.protectionService = protectionService;
        this.houseService = houseService;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        String namespacedID = event.getNamespacedID();

        if (!namespacedID.equalsIgnoreCase(this.pluginConfiguration.apartmentsBuyerNamespacedId)) {
            return;
        }

        for (ProtectedRegion region : this.protectionService.getLocationRegions(event.getBukkitEntity().getLocation()).getRegions()) {
            String regionId = region.getId();

            Optional<House> houseByBlockOfFlatsId = this.houseService.getHouseByBlockOfFlatsId(regionId);

            if (houseByBlockOfFlatsId.isEmpty()) {
                continue;
            }

            House house = houseByBlockOfFlatsId.get();
            this.apartmentListInventory.openInventory(player, house);
        }

    }

}