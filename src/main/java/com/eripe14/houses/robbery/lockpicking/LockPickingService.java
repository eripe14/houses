package com.eripe14.houses.robbery.lockpicking;

import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.HashMap;
import java.util.Map;

public class LockPickingService {

    private final Map<Player, LockPickingTask> lockPickingPlayers = new HashMap<>();
    private final Map<Player, Boolean> lockPickingPlayersInteracting = new HashMap<>();

    public void addLockPickingTask(Player player, LockPickingTask lockPickingTask) {
        this.lockPickingPlayers.put(player, lockPickingTask);
    }

    public void removeLockPickingTask(Player player) {
        this.lockPickingPlayers.remove(player);
    }

    public Option<LockPickingTask> getLockPickingTask(Player player) {
        return Option.of(this.lockPickingPlayers.get(player));
    }

    public boolean containsLockPickingTask(Player player) {
        return this.lockPickingPlayers.containsKey(player);
    }

    public void addLockPickingPlayerInteracting(Player player) {
        this.lockPickingPlayersInteracting.put(player, true);
    }

    public void removeLockPickingPlayerInteracting(Player player) {
        this.lockPickingPlayersInteracting.remove(player);
    }

    public Option<Boolean> isLockPickingPlayerInteracting(Player player) {
        return Option.of(this.lockPickingPlayersInteracting.get(player));
    }

}