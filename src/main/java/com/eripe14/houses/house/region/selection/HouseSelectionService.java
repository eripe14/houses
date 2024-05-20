package com.eripe14.houses.house.region.selection;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class HouseSelectionService {

    private final Map<Player, CompletableFuture<Player>> playerSelections = new HashMap<>();

    public void putSelection(Player player, CompletableFuture<Player> playerCompletableFuture) {
        this.playerSelections.put(player, playerCompletableFuture);
    }

    public CompletableFuture<Player> removeSelection(Player player) {
        return this.playerSelections.remove(player);
    }

    public boolean hasSelection(Player player) {
        return this.playerSelections.containsKey(player);
    }

}