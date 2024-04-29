package com.eripe14.houses.text;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.function.BiConsumer;

public class ChatTextProvider implements TextProvider, Listener {

    // TODO CHANGE TO USE A MAP!!!

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        this.handleText(player, message, (p, m) -> event.setCancelled(true));
    }

    @Override
    public void handleText(Player player, String text, BiConsumer<Player, String> callBack) {
        callBack.accept(player, text);
    }

}