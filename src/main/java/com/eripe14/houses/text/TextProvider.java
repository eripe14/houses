package com.eripe14.houses.text;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface TextProvider {

    CompletableFuture<String> getPlayerInput(Player player);

}