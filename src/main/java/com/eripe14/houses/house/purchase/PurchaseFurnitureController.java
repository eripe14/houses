package com.eripe14.houses.house.purchase;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.furniture.HouseCustomFurniture;
import com.eripe14.houses.house.inventory.impl.RentInventory;
import com.eripe14.houses.house.inventory.impl.SelectPurchaseInventory;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.region.HouseType;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.position.Position;
import com.eripe14.houses.position.PositionAdapter;
import com.eripe14.houses.robbery.RobberyService;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import dev.lone.itemsadder.api.Events.FurniturePlaceSuccessEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import panda.std.Option;

import java.util.Optional;

public class PurchaseFurnitureController implements Listener {

    private final RobberyService robberyService;
    private final SelectPurchaseInventory selectPurchaseInventory;
    private final RentInventory rentInventory;
    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration messageConfiguration;

    public PurchaseFurnitureController(
            RobberyService robberyService,
            SelectPurchaseInventory selectPurchaseInventory,
            RentInventory rentInventory,
            HouseService houseService,
            ProtectionService protectionService,
            NotificationAnnouncer notificationAnnouncer,
            PluginConfiguration pluginConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.robberyService = robberyService;
        this.selectPurchaseInventory = selectPurchaseInventory;
        this.rentInventory = rentInventory;
        this.houseService = houseService;
        this.protectionService = protectionService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.pluginConfiguration = pluginConfiguration;
        this.messageConfiguration = messageConfiguration;
    }

    @EventHandler
    public void onFurniturePlace(FurniturePlaceSuccessEvent event) {
        Player player = event.getPlayer();
        CustomFurniture furniture = event.getFurniture();
        Entity bukkitEntity = event.getBukkitEntity();
        Location location = bukkitEntity.getLocation();

        if (furniture == null) {
            return;
        }

        if (!furniture.getNamespacedID().equalsIgnoreCase(this.pluginConfiguration.itemsAdderPurchaseNamespacedId)) {
            return;
        }

        Optional<ProtectedRegion> firstRegion = this.protectionService.findFirstRegion(location);

        if (firstRegion.isEmpty()) {
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(firstRegion.get());

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();
        HouseRegion region = house.getRegion();

        if (!house.getRegion().getPurchaseFurniture().getNamespacedId().equalsIgnoreCase("-")) {
            return;
        }

        if (house.getRegion().getType() == HouseType.APARTMENT) {
            return;
        }

        HouseCustomFurniture houseCustomFurniture = new HouseCustomFurniture(
                furniture.getNamespacedID(),
                PositionAdapter.convert(location)
        );

        region.setHouseCustomFurniture(houseCustomFurniture);
        this.houseService.addHouse(house);

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.setUpHouseFurnitureForSale);
    }

    @EventHandler
    public void onPurchaseFurnitureBreak(FurnitureBreakEvent event) {
        Player player = event.getPlayer();
        CustomFurniture furniture = event.getFurniture();
        Entity bukkitEntity = event.getBukkitEntity();
        Location location = bukkitEntity.getLocation();

        if (furniture == null) {
            return;
        }

        if (!furniture.getNamespacedID().equalsIgnoreCase(this.pluginConfiguration.itemsAdderPurchaseNamespacedId)) {
            return;
        }

        Optional<ProtectedRegion> firstRegion = this.protectionService.findFirstRegion(location);

        if (firstRegion.isEmpty()) {
            player.sendMessage("Region not found");
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(firstRegion.get());

        if (houseOption.isEmpty()) {
            player.sendMessage("House not found");
            return;
        }

        House house = houseOption.get();
        HouseRegion region = house.getRegion();

        region.setHouseCustomFurniture(
                new HouseCustomFurniture("-", new Position(0, 0, 0, 0, 0, "-"))
        );
        this.houseService.addHouse(house);

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.removedHouseFurnitureForSale);
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CustomFurniture furniture = event.getFurniture();
        Entity bukkitEntity = event.getBukkitEntity();
        Location location = bukkitEntity.getLocation();

        if (furniture == null) {
            return;
        }

        if (!furniture.getNamespacedID().equalsIgnoreCase(this.pluginConfiguration.itemsAdderPurchaseNamespacedId)) {
            return;
        }

        Optional<ProtectedRegion> firstRegion = this.protectionService.findFirstRegion(location);

        if (firstRegion.isEmpty()) {
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(firstRegion.get());

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();

        if (house.getOwner().isPresent()) {
            event.setCancelled(true);
            return;
        }

        if (this.robberyService.getRobbery(house.getHouseId()).isPresent()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.canNotBoughtHouseNow);
            return;
        }

        if (house.getBuyPrice() == 0) {
            this.rentInventory.openInventory(player, house);
            return;
        }

        this.selectPurchaseInventory.openInventory(player, house);
    }

    @EventHandler
    public void onFurnitureBreak(FurnitureBreakEvent event) {
        CustomFurniture furniture = event.getFurniture();
        Entity bukkitEntity = event.getBukkitEntity();
        Location location = bukkitEntity.getLocation();

        if (furniture == null) {
            return;
        }

        if (!furniture.getNamespacedID().equalsIgnoreCase(this.pluginConfiguration.itemsAdderPurchaseNamespacedId)) {
            return;
        }

        if (!event.getPlayer().hasPermission("houses.admin")) {
            return;
        }

        Location furnitureLocation = new Location(
                event.getPlayer().getWorld(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch()
        );

        Optional<ProtectedRegion> firstRegion = this.protectionService.findFirstRegion(furnitureLocation);

        if (firstRegion.isEmpty()) {
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(firstRegion.get());

        if (houseOption.isEmpty()) {
            return;
        }

        event.setCancelled(true);
    }

}