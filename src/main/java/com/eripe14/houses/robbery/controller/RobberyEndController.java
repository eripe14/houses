package com.eripe14.houses.robbery.controller;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.contextual.NpcData;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.purchase.PurchaseService;
import com.eripe14.houses.robbery.Robbery;
import com.eripe14.houses.robbery.RobberyService;
import dev.lone.itemsadder.api.CustomFurniture;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import panda.std.Option;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class RobberyEndController implements Listener {

    private final RobberyService robberyService;
    private final PurchaseService purchaseService;
    private final HouseService houseService;
    private final AlertHandler alertHandler;
    private final NotificationAnnouncer notificationAnnouncer;
    private final RobberyConfiguration robberyConfiguration;
    private final MessageConfiguration messageConfiguration;

    public RobberyEndController(
            RobberyService robberyService,
            PurchaseService purchaseService,
            HouseService houseService,
            AlertHandler alertHandler,
            NotificationAnnouncer notificationAnnouncer,
            RobberyConfiguration robberyConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.robberyService = robberyService;
        this.purchaseService = purchaseService;
        this.houseService = houseService;
        this.alertHandler = alertHandler;
        this.notificationAnnouncer = notificationAnnouncer;
        this.robberyConfiguration = robberyConfiguration;
        this.messageConfiguration = messageConfiguration;
    }

    @EventHandler
    public void onNpcClick(NPCRightClickEvent event) {
        Player clicker = event.getClicker();
        NPC npc = event.getNPC();

        NpcData thiefNpcData = this.robberyConfiguration.thiefNpcData;

        if (!npc.getName().equalsIgnoreCase(thiefNpcData.getName())) {
            return;
        }

        if (!this.robberyService.isPlayerRobbing(clicker.getUniqueId())) {
            this.notificationAnnouncer.sendMessage(clicker, this.messageConfiguration.robbery.noCurrentRobbery);
            return;
        }

        Optional<Robbery> robberyOptional = this.robberyService.getRobbery(clicker.getUniqueId());

        if (robberyOptional.isEmpty()) {
            return;
        }

        Robbery robbery = robberyOptional.get();

        AlertFormatter formatter = new AlertFormatter();
        formatter.register("{HOUSE}", robbery.getHouseId());

        int randomMessageIndex = (int) (Math.random() * this.messageConfiguration.robbery.endedRobberyRandomMessages.size());
        String randomMessage = this.messageConfiguration.robbery.endedRobberyRandomMessages.get(randomMessageIndex);
        this.notificationAnnouncer.sendMessage(clicker, randomMessage, formatter);

        int minMaxPrice = this.robberyConfiguration.minMaxPrice;
        int maxMaxPrice = this.robberyConfiguration.maxMaxPrice;

        for (ItemStack stolenItem : robbery.getStolenItems()) {
            int randomPrice = new Random().nextInt((maxMaxPrice - minMaxPrice) + 1) + minMaxPrice;

            if (stolenItem.getItemMeta() != null) {
                String displayName = stolenItem.getItemMeta().getDisplayName().replace("§", "&");
                formatter.register("{ITEM}", displayName);
            }

            formatter.register("{PRICE}", String.valueOf(randomPrice));

            clicker.getInventory().removeItem(stolenItem);
            clicker.playSound(clicker.getLocation(), this.robberyConfiguration.itemSellSound, 1.0f, 1.0f);
            this.purchaseService.depositMoney(clicker, randomPrice);
            this.notificationAnnouncer.sendMessage(clicker, this.messageConfiguration.robbery.sellStolenItem, formatter);
        }

        robbery.getStolenFurniture().forEach(((location, customFurniture) -> {
            CustomFurniture.spawnPreciseNonSolid(customFurniture.getNamespacedID(), location);
        }));

        robbery.getBrokenGlass().forEach((Block::setType));

        for (BlockState blockState : robbery.getOpenedDoors()) {
            Openable openable = (Openable) blockState.getBlockData();
            openable.setOpen(false);
            blockState.setBlockData(openable);
            blockState.update();
        }

        robbery.getStolenFurniture().clear();
        robbery.getBrokenGlass().clear();
        robbery.getOpenedDoors().clear();

        Option<House> houseOption = this.houseService.getHouse(robbery.getHouseId());

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();

        if (house.getOwner().isEmpty() || this.houseService.getStuffInHouse(house).isEmpty()) {
            this.robberyService.removeRobbery(robbery.getHouseId());
            return;
        }

        for (UUID stuffUuid : this.houseService.getStuffInHouse(house)) {
            Alert alert = new Alert(
                    stuffUuid,
                    this.messageConfiguration.robbery.houseHasBeenRobbedSubject,
                    this.messageConfiguration.robbery.houseHasBeenRobbedMessage,
                    formatter
            );

            this.alertHandler.sendAlertIfPlayerNotOnline(stuffUuid, alert);
        }

        this.robberyService.removeRobbery(robbery.getHouseId());
    }

}