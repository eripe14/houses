package com.eripe14.houses.house.region.selection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.concurrent.CompletableFuture;

public class HouseSelectionController implements Listener {

    private final HouseSelectionService houseSelectionService;

    public HouseSelectionController(HouseSelectionService houseSelectionService) {
        this.houseSelectionService = houseSelectionService;
    }

    @EventHandler
    void onPlayerAcceptSelection(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        if (!this.houseSelectionService.hasSelection(player)) {
            return;
        }

        CompletableFuture<Player> playerSelectionCompletableFuture = this.houseSelectionService.removeSelection(player);
        playerSelectionCompletableFuture.complete(player);

        event.setCancelled(true);
    }

}