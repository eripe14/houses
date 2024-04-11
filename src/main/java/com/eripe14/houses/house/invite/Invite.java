package com.eripe14.houses.house.invite;

import com.eripe14.houses.house.House;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public interface Invite {

    House getHouse();

    BiConsumer<Player, Player> resultAction();

}