package com.eripe14.houses.house.region.protection.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.member.HouseMemberPermission;
import com.eripe14.houses.house.member.HouseMemberService;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import panda.std.Option;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OpenChestController implements Listener {

    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final HouseMemberService houseMemberService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public OpenChestController(
            HouseService houseService,
            ProtectionService protectionService,
            HouseMemberService houseMemberService,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.houseService = houseService;
        this.protectionService = protectionService;
        this.houseMemberService = houseMemberService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Block block = event.getClickedBlock();
        List<Material> chests = this.pluginConfiguration.chests;

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (block == null) {
            event.setUseInteractedBlock(Event.Result.DENY);
            return;
        }

        Location clickedBlockLocation = block.getLocation();
        Material blockMaterial = block.getType();

        if (!chests.contains(blockMaterial)) {
            return;
        }

        Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(clickedBlockLocation);

        if (houseRegionOption.isEmpty()) {
            return;
        }

        ProtectedRegion houseRegion = houseRegionOption.get();
        Option<House> houseOption = this.houseService.getHouse(houseRegion);

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
            event.setCancelled(true);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToOpenChests);
            return;
        }

        HouseMember houseMember = houseMemberOption.get();

        if (this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.OPEN_CHESTS)) {
            return;
        }

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToOpenChests);
        event.setCancelled(true);


    }

}