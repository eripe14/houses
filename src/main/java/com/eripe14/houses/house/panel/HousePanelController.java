package com.eripe14.houses.house.panel;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.inventory.impl.PurchasedPanelInventory;
import com.eripe14.houses.house.inventory.impl.RentedPanelInventory;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import panda.std.Option;

import java.util.Optional;
import java.util.UUID;

public class HousePanelController implements Listener {

    private final HouseService houseService;
    private final HouseMemberService houseMemberService;
    private final ProtectionService protectionService;
    private final RentedPanelInventory rentedPanelInventory;
    private final PurchasedPanelInventory purchasePanelInventory;
    private final PluginConfiguration pluginConfiguration;

    public HousePanelController(HouseService houseService, HouseMemberService houseMemberService, ProtectionService protectionService, RentedPanelInventory rentedPanelInventory, PurchasedPanelInventory purchasePanelInventory, PluginConfiguration pluginConfiguration) {
        this.houseService = houseService;
        this.houseMemberService = houseMemberService;
        this.protectionService = protectionService;
        this.rentedPanelInventory = rentedPanelInventory;
        this.purchasePanelInventory = purchasePanelInventory;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (event.getFurniture() == null) {
            return;
        }

        Option<House> houseOption = this.getHouseFromFurnitureLocation(event.getNamespacedID(), event.getFurniture());

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        Owner owner = house.getOwner().get();

        if (this.houseMemberService.isCoOwner(house, uuid) || owner.getUuid().equals(uuid)) {
            event.setCancelled(true);

            if (house.getRent().isEmpty()) {
                this.purchasePanelInventory.openInventory(player, house);
                return;
            }

            this.rentedPanelInventory.openInventory(player, house);
        }
    }

    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (event.getFurniture() == null) {
            return;
        }

        Option<House> houseOption = this.getHouseFromFurnitureLocation(event.getNamespacedID(), event.getFurniture());

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        Owner owner = house.getOwner().get();

        event.setCancelled(true);

        if (this.houseMemberService.isCoOwner(house, uuid) || owner.getUuid().equals(uuid)) {
            event.setCancelled(false);
        }
    }

    private Option<House> getHouseFromFurnitureLocation(String namespacedID, CustomFurniture furniture) {
        Entity armorstand = furniture.getArmorstand();

        if (namespacedID == null) {
            return Option.none();
        }

        if (armorstand == null) {
            return Option.none();
        }

        Location location = armorstand.getLocation();

        if (!namespacedID.equalsIgnoreCase(this.pluginConfiguration.itemsAdderHousePanelNamespacedId)) {
            return Option.none();
        }

        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(location);

        if (regionOption.isEmpty()) {
            return Option.none();
        }

        ProtectedRegion region = regionOption.get();
        Option<House> houseOption = this.houseService.getHouse(region);

        if (houseOption.isEmpty()) {
            return Option.none();
        }

        return houseOption;
    }

}