package com.eripe14.houses.history;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class HistoryUserController implements Listener {

    private final HistoryUserService historyUserService;

    public HistoryUserController(HistoryUserService historyUserService) {
        this.historyUserService = historyUserService;
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (this.historyUserService.exists(player.getUniqueId())) {
            return;
        }

        HistoryUser historyUser = this.historyUserService.create(player.getUniqueId(), player.getName());
        this.historyUserService.addUser(historyUser);
    }
}