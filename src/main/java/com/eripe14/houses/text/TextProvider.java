package com.eripe14.houses.text;

import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface TextProvider {

    void handleText(Player player, String text, BiConsumer<Player, String> callBack);

}