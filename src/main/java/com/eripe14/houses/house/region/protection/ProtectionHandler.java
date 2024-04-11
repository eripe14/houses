package com.eripe14.houses.house.region.protection;

import com.eripe14.houses.house.member.HouseMemberPermission;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface ProtectionHandler {

    CompletableFuture<ProtectionInteractResult> canInteract(
            PlayerInteractEvent event,
            List<Material> interactableBlockMaterials,
            Player player,
            HouseMemberPermission permission
    );

}