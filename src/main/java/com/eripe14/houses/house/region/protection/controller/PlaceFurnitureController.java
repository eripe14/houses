package com.eripe14.houses.house.region.protection.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import dev.lone.itemsadder.api.Events.FurniturePlaceEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

import java.util.UUID;

import static dev.lone.itemsadder.api.CustomStack.byItemStack;

public class PlaceFurnitureController implements Listener {

    private final ProtectionService protectionService;
    private final HouseMemberService houseMemberService;
    private final HouseService houseService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;

    public PlaceFurnitureController(ProtectionService protectionService, HouseMemberService houseMemberService, HouseService houseService, NotificationAnnouncer notificationAnnouncer, MessageConfiguration messageConfiguration) {
        this.protectionService = protectionService;
        this.houseMemberService = houseMemberService;
        this.houseService = houseService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
    }

    @EventHandler
    public void onFurniturePlace(FurniturePlaceEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        ItemStack itemInUse = player.getInventory().getItemInMainHand();
        CustomStack customStack = byItemStack(itemInUse);

        player.sendMessage("place furniture event");

        if (CustomFurniture.getInstance(event.getNamespacedID()) instanceof CustomFurniture) {
            player.sendMessage("custom furniture");
        }

        CustomFurniture customFurniture = null;

        if (customFurniture == null) {
            return;
        }

        if (customStack == null) {
            return;
        }

        for (ProtectedRegion playerRegion : this.protectionService.getLocationRegions(customFurniture.getArmorstand().getLocation())) {
            Option<House> houseOption = this.houseService.getHouse(playerRegion);

            if (houseOption.isEmpty()) {
                return;
            }

            House house = houseOption.get();
            Owner owner = house.getOwner().get();

            if (owner.getUuid().equals(uuid)) {
                return;
            }

            Option<HouseMember> houseMemberOption = this.houseMemberService.getHouseMember(house, uuid);

            if (houseMemberOption.isEmpty()) {
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToPlaceFurniture);
                event.setCancelled(true);

                return;
            }

            HouseMember houseMember = houseMemberOption.get();

            if (this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.PLACE_FURNITURE)) {
                return;
            }

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToPlaceFurniture);
            event.setCancelled(true);
        }
    }

}