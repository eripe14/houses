package com.eripe14.houses.text;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChatTextProvider implements TextProvider, Listener {

    private final Map<Player, CompletableFuture<String>> playersText = new HashMap<>();
    private final PluginConfiguration pluginConfiguration;

    public ChatTextProvider(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        if (!this.playersText.containsKey(player)) {
            return;
        }

        CompletableFuture<String> future = this.playersText.remove(player);
        future.complete(message);

        event.setCancelled(true);
    }

    @Override
    public CompletableFuture<String> getPlayerInput(Player player) {
        CompletableFuture<String> future = new CompletableFuture<>();
        this.playersText.put(player, future);

        Duration timeToProvideRenovationRequest = this.pluginConfiguration.timeToProvideRenovationRequest;

        future.orTimeout(timeToProvideRenovationRequest.getSeconds(), TimeUnit.SECONDS).exceptionally(exception -> {
            if (exception instanceof TimeoutException) {
                this.playersText.remove(player);
            }

            return "";
        });


        return future;
    }
}