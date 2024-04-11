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

public class OpenDoorController implements Listener {

    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final HouseMemberService houseMemberService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public OpenDoorController(
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
        this.messageConfiguration = messageConfiguration;
        this.notificationAnnouncer = notificationAnnouncer;
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Block block = event.getClickedBlock();
        List<Material> doors = this.pluginConfiguration.doors;

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (block == null) {
            return;
        }

        Location clickedBlockLocation = block.getLocation();
        Material blockMaterial = block.getType();

        if (!doors.contains(blockMaterial)) {
            return;
        }

        Optional<ProtectedRegion> houseRegionOption = this.protectionService.findFirstRegion(clickedBlockLocation);

        for (ProtectedRegion locationRegion : this.protectionService.getLocationRegions(clickedBlockLocation)) {
            Option<House> houseOption = this.houseService.getHouse(locationRegion);

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
                this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToOpenDoors);
                event.setUseInteractedBlock(Event.Result.DENY);

                return;
            }

            HouseMember houseMember = houseMemberOption.get();

            if (this.houseMemberService.hasPermission(houseMember, HouseMemberPermission.OPEN_DOORS)) {
                return;
            }

            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.house.permissionToOpenDoors);
            event.setUseInteractedBlock(Event.Result.DENY);
        }
    }

}