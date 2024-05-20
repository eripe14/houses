package com.eripe14.houses.robbery.controller;

import com.eripe14.houses.configuration.contextual.NpcData;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.robbery.RobberyService;
import com.eripe14.houses.robbery.RobberyStartHandler;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import panda.utilities.text.Formatter;

public class RobberyStartController implements Listener {

    private final RobberyStartHandler robberyStartHandler;
    private final RobberyService robberyService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final RobberyConfiguration robberyConfiguration;

    public RobberyStartController(
            RobberyStartHandler robberyStartHandler,
            RobberyService robberyService,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            RobberyConfiguration robberyConfiguration
    ) {
        this.robberyStartHandler = robberyStartHandler;
        this.robberyService = robberyService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.robberyConfiguration = robberyConfiguration;
    }

    @EventHandler
    public void onNpcInteract(NPCRightClickEvent event) {
        Player clicker = event.getClicker();
        NPC npc = event.getNPC();

        NpcData principalNpcData = this.robberyConfiguration.principalNpcData;

        if (!npc.getName().equalsIgnoreCase(principalNpcData.getName())) {
            return;
        }

        if (this.robberyService.isPlayerRobbing(clicker.getUniqueId())) {
            this.notificationAnnouncer.sendMessage(clicker, this.messageConfiguration.robbery.alreadyHasRobbery);
            return;
        }

        String robberyHouseId = this.robberyStartHandler.getRobberyRequest(clicker);

        Formatter formatter = new Formatter();
        formatter.register("{HOUSE}", robberyHouseId);

        if (robberyHouseId.isEmpty()) {
            this.notificationAnnouncer.sendMessage(clicker, this.messageConfiguration.robbery.failedToFindHouseToRob);
            return;
        }

        int randomMessageIndex = (int) (Math.random() * this.messageConfiguration.robbery.startedRobberyRandomMessages.size());
        String randomMessage = this.messageConfiguration.robbery.startedRobberyRandomMessages.get(randomMessageIndex);

        this.notificationAnnouncer.sendMessage(clicker, randomMessage, formatter);
    }

}