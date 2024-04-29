package com.eripe14.houses.house.renovation;

import net.citizensnpcs.api.event.NPCClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RenovationStartHandler implements Listener {

    @EventHandler
    public void onClickNpc(NPCClickEvent event) {
        Player clicker = event.getClicker();
        int id = event.getNPC().getId();

        // TODO check id if is equal to id from configuration
    }

}