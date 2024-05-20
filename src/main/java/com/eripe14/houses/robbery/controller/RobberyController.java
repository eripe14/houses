package com.eripe14.houses.robbery.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.region.protection.ProtectionService;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.robbery.Robbery;
import com.eripe14.houses.robbery.RobberyService;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.Events.FurnitureInteractEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import panda.std.Option;
import panda.std.Pair;
import panda.utilities.text.Formatter;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RobberyController implements Listener {

    private final Plugin plugin;
    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final RobberyService robberyService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final MessageConfiguration messageConfiguration;
    private final RobberyConfiguration robberyConfiguration;
    private final Map<Player, Pair<CustomFurniture, BukkitTask>> robberyTasks = new HashMap<>();

    public RobberyController(
            Plugin plugin,
            HouseService houseService,
            ProtectionService protectionService,
            RobberyService robberyService,
            NotificationAnnouncer notificationAnnouncer,
            MessageConfiguration messageConfiguration,
            RobberyConfiguration robberyConfiguration
    ) {
        this.plugin = plugin;
        this.houseService = houseService;
        this.protectionService = protectionService;
        this.robberyService = robberyService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.messageConfiguration = messageConfiguration;
        this.robberyConfiguration = robberyConfiguration;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Player player = event.getPlayer();
        CustomFurniture furniture = event.getFurniture();
        Entity bukkitEntity = event.getBukkitEntity();
        Location location = bukkitEntity.getLocation();

        if (furniture == null) {
            return;
        }

        Optional<Robbery> robberyOptional = this.robberyService.getRobbery(player.getUniqueId());

        if (robberyOptional.isEmpty()) {
            return;
        }

        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(location);

        if (regionOption.isEmpty()) {
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(regionOption.get());

        if (houseOption.isEmpty()) {
            return;
        }

        Option<House> robberyHouseOption = this.houseService.getHouse(robberyOptional.get().getHouseId());

        if (robberyHouseOption.isEmpty() || !robberyHouseOption.get().equals(houseOption.get())) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyInRobbingHouse);
            return;
        }

        if (this.robberyConfiguration.blockedNamespaceIds.contains(furniture.getNamespacedID())) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.thisItemIsBlocked);
            return;
        }

        if (this.robberyTasks.containsKey(player)) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.alreadyStealingItem);
            return;
        }

        Robbery robbery = robberyOptional.get();

        event.setCancelled(true);

        if (robbery.getCurrentWeight() >= robbery.getMaxWeight()) {
            this.notificationAnnouncer.sendActionBar(player, this.messageConfiguration.robbery.maxWeightReached);
            return;
        }

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.startStealingItem);

        Formatter formatter = new Formatter();
        formatter.register("{MAX_WEIGHT}", robbery.getMaxWeight());

        player.playSound(player.getLocation(), this.robberyConfiguration.packingStartSound, 1f, 1f);

        Instant now = Instant.now();
        Duration duration = Duration.ofSeconds(this.robberyConfiguration.packingTime);

        BukkitTask bukkitRunnable = new BukkitRunnable() {
            int timeElapsed = 0;

            @Override
            public void run() {
                if (!Instant.now().isAfter(now.plus(duration))) {
                    notificationAnnouncer.sendActionBar(
                            player,
                            getActionBar(timeElapsed, robberyConfiguration.packingTime * 4, 100)
                    );
                    timeElapsed++;
                    return;
                }

                player.getInventory().addItem(furniture.getItemStack());
                robbery.setCurrentWeight(robbery.getCurrentWeight() + 1);
                robbery.addStolenItem(furniture.getItemStack());
                robbery.addStolenFurniture(location, furniture);

                formatter.register("{WEIGHT}", robbery.getCurrentWeight());

                notificationAnnouncer.sendActionBar(
                        player,
                        messageConfiguration.robbery.stoleItem,
                        formatter
                );

                furniture.remove(false);
                robberyTasks.remove(player);
                this.cancel();
            }
        }.runTaskTimer(this.plugin, 0, 4L);

        this.robberyTasks.put(player, Pair.of(furniture, bukkitRunnable));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location eventFrom = event.getFrom();
        Location eventTo = event.getTo();

        if (!this.robberyTasks.containsKey(player)) {
            return;
        }

        event.setCancelled(false);

        if (eventFrom.getX() != eventTo.getX() || eventFrom.getY() != eventTo.getY() || eventFrom.getZ() != eventTo.getZ()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!this.robberyTasks.containsKey(player)) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        this.notificationAnnouncer.sendActionBar(player, this.messageConfiguration.robbery.cancelledStealingItem);

        Pair<CustomFurniture, BukkitTask> customFurnitureBukkitTaskPair = this.robberyTasks.get(player);
        customFurnitureBukkitTaskPair.getSecond().cancel();

        this.robberyTasks.remove(player);
    }

    private String getActionBar(int current, int max, int totalBars) {
        if (current > max) {
            current = max;
        }

        float percent = (float) current / max;
        int progressBars = Math.round(totalBars * percent);
        int remainingBars = Math.max(0, totalBars - progressBars);

        return "&a" + String.valueOf('|').repeat(progressBars) + "&7" + String.valueOf('|').repeat(remainingBars);
    }

}