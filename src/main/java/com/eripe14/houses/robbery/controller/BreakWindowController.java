package com.eripe14.houses.robbery.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.robbery.Robbery;
import com.eripe14.houses.robbery.RobberyService;
import com.eripe14.houses.robbery.RobberyStartHandler;
import com.eripe14.houses.util.ItemUtil;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.projectiles.ProjectileSource;
import panda.std.Option;

import java.util.Optional;

public class BreakWindowController implements Listener {

    private final Plugin plugin;
    private final RobberyService robberyService;
    private final RobberyStartHandler robberyStartHandler;
    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final RobberyConfiguration robberyConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final String nbtValue;

    public BreakWindowController(
            Plugin plugin,
            RobberyService robberyService,
            RobberyStartHandler robberyStartHandler,
            HouseService houseService,
            ProtectionService protectionService,
            NotificationAnnouncer notificationAnnouncer,
            RobberyConfiguration robberyConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.plugin = plugin;
        this.robberyService = robberyService;
        this.robberyStartHandler = robberyStartHandler;
        this.houseService = houseService;
        this.protectionService = protectionService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.robberyConfiguration = robberyConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.nbtValue = this.robberyConfiguration.robberySnowball.nbtKey;
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        ProjectileSource shooter = entity.getShooter();

        if (!(shooter instanceof Player player)) {
            return;
        }

        ItemStack itemInUse = player.getInventory().getItemInMainHand();

        if (!ItemUtil.getNbtValue(this.plugin, itemInUse, "rp_houses").equalsIgnoreCase(this.nbtValue)) {
            return;
        }

        Optional<Robbery> robberyOptional = this.robberyService.getRobbery(player.getUniqueId());

        if (robberyOptional.isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyWhileRobbing);
            event.setCancelled(true);
            return;
        }

        NBT.modifyPersistentData(entity, nbt -> {
            nbt.setString("rp_houses", this.nbtValue);
        });
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Block hitBlock = event.getHitBlock();
        Projectile entity = event.getEntity();
        ProjectileSource shooter = entity.getShooter();

        if (hitBlock == null || shooter == null) {
            return;
        }

        if (!(shooter instanceof Player player)) {
            return;
        }

        String rpHouses = NBT.getPersistentData(entity, nbt -> nbt.getString("rp_houses"));

        if (!rpHouses.equalsIgnoreCase(this.nbtValue)) {
            return;
        }

        if (!hitBlock.getType().name().contains("GLASS")) {
            return;
        }

        if (hitBlock.getType().name().contains("PANE")) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.bulletProofGlassPane);
            return;
        }

        Optional<Robbery> robberyOptional = this.robberyService.getRobbery(player.getUniqueId());

        if (robberyOptional.isEmpty()) {
            return;
        }

        Robbery robbery = robberyOptional.get();
        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(hitBlock.getLocation());

        if (regionOption.isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyInRobbingHouse);
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(regionOption.get());

        if (houseOption.isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyInRobbingHouse);
            return;
        }

        Option<House> robberyHouseOption = this.houseService.getHouse(robbery.getHouseId());

        if (robberyHouseOption.isEmpty() || !robberyHouseOption.get().equals(houseOption.get())) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyInRobbingHouse);
            return;
        }

        robbery.addBrokenGlass(hitBlock, hitBlock.getType());
        hitBlock.setType(Material.AIR);
        player.playSound(hitBlock.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0F, 1.0F);

        if (robbery.isPoliceNotified()) {
            return;
        }

        robbery.setPoliceNotified(true);
        this.robberyStartHandler.notifyPlayers(robbery);
    }

}