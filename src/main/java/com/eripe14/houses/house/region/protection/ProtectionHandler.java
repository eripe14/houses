package com.eripe14.houses.house.region.protection;

import com.eripe14.houses.house.member.HouseMemberPermission;
import dev.lone.itemsadder.api.Events.FurnitureBreakEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import panda.std.reactive.Completable;

import java.util.List;

public interface ProtectionHandler {

    Completable<ProtectionInteractResult> canInteractWithBlocks(
            PlayerInteractEvent event,
            List<Material> interactableBlockMaterials,
            Player player,
            HouseMemberPermission permission
    );

    Completable<ProtectionInteractResult> canPlaceBlock(
            BlockPlaceEvent event,
            Player player
    );

    Completable<ProtectionInteractResult> canBreakBlock(
            BlockBreakEvent event,
            Player player
    );

    Completable<ProtectionInteractResult> canBreakFurniture(
            FurnitureBreakEvent event,
            Player player,
            HouseMemberPermission permission
    );

}