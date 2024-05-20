package com.eripe14.houses.robbery.controller;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Openable;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import panda.std.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class KickingDoorController implements Listener {

    private final Plugin plugin;
    private final Server server;
    private final NotificationAnnouncer notificationAnnouncer;
    private final RobberyConfiguration robberyConfiguration;
    private final PluginConfiguration pluginConfiguration;
    private final RobberyService robberyService;
    private final RobberyStartHandler robberyStartHandler;
    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final MessageConfiguration messageConfiguration;
    private final Map<Player, BossBar> playersBossBars = new HashMap<>();
    private final Map<Player, Integer> doorKickingLevel = new HashMap<>();
    private final Map<Player, Double> doorKickingProgress = new HashMap<>();
    private final Map<Player, BukkitTask> doorKickingTasks = new HashMap<>();
    private final Map<Player, Boolean> playerSneaking = new HashMap<>();
    private final Map<Player, BlockState> blockStates = new HashMap<>();

    public KickingDoorController(
            Plugin plugin,
            Server server,
            NotificationAnnouncer notificationAnnouncer,
            RobberyConfiguration robberyConfiguration,
            PluginConfiguration pluginConfiguration,
            RobberyService robberyService,
            RobberyStartHandler robberyStartHandler,
            HouseService houseService,
            ProtectionService protectionService,
            MessageConfiguration messageConfiguration
    ) {
        this.plugin = plugin;
        this.server = server;
        this.notificationAnnouncer = notificationAnnouncer;
        this.robberyConfiguration = robberyConfiguration;
        this.pluginConfiguration = pluginConfiguration;
        this.robberyService = robberyService;
        this.robberyStartHandler = robberyStartHandler;
        this.houseService = houseService;
        this.protectionService = protectionService;
        this.messageConfiguration = messageConfiguration;
        this.startDecreasingTask();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location eventTo = event.getTo();
        Location eventFrom = event.getFrom();

        if (!this.playersBossBars.containsKey(player)) {
            return;
        }

        event.setCancelled(false);

        if (eventFrom.getX() != eventTo.getX() || eventFrom.getY() != eventTo.getY() || eventFrom.getZ() != eventTo.getZ()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Block clickedBlock = event.getClickedBlock();
        ItemStack itemInUse = player.getInventory().getItemInMainHand();

        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        if (clickedBlock == null) {
            return;
        }

        String nbtKey = this.robberyConfiguration.kickDoorItem.nbtKey;

        if (!ItemUtil.getNbtValue(this.plugin, itemInUse, "rp_houses").equalsIgnoreCase(nbtKey)) {
            return;
        }

        Optional<Robbery> robberyOptional = this.robberyService.getRobbery(player.getUniqueId());

        if (robberyOptional.isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyWhileRobbing);
            event.setCancelled(true);
            return;
        }

        Optional<ProtectedRegion> regionOption = this.protectionService.findFirstRegion(clickedBlock.getLocation());

        if (regionOption.isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyInRobbingHouse);
            event.setCancelled(true);
            return;
        }

        Option<House> houseOption = this.houseService.getHouse(regionOption.get());

        if (houseOption.isEmpty()) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyInRobbingHouse);
            event.setCancelled(true);
            return;
        }

        Option<House> robberyHouseOption = this.houseService.getHouse(robberyOptional.get().getHouseId());

        if (robberyHouseOption.isEmpty() || !robberyHouseOption.get().equals(houseOption.get())) {
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.itemCouldBeUsedOnlyInRobbingHouse);
            event.setCancelled(true);
            return;
        }

        if (!this.pluginConfiguration.doors.contains(clickedBlock.getType())) {
            return;
        }

        if (this.playersBossBars.containsKey(player)) {
            event.setCancelled(true);
            return;
        }

        this.blockStates.put(player, clickedBlock.getState());

        Material material = itemInUse.getType();
        short maxDurability = material.getMaxDurability();
        short removalDurability = (short) ((maxDurability / 5) + 1);

        itemInUse.setDurability((short) (itemInUse.getDurability() + removalDurability));

        if (itemInUse.getDurability() >= maxDurability) {
            itemInUse.setAmount(itemInUse.getAmount() - 1);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK,1f, 1f);
        }

        event.setCancelled(true);
        Robbery robbery = robberyOptional.get();

        if (!robbery.isPoliceNotified()) {
            robbery.setPoliceNotified(true);
            this.robberyStartHandler.notifyPlayers(robbery);
        }

        BossBar bossBar = this.server.createBossBar(
                ChatColor.translateAlternateColorCodes('&', this.robberyConfiguration.kickDoorBossBarTitle + "1"),
                this.robberyConfiguration.kickDoorBossBarColor,
                BarStyle.SOLID
        );
        bossBar.addPlayer(player);
        bossBar.setProgress(0);

        this.playersBossBars.put(player, bossBar);
        this.doorKickingLevel.put(player, 1);
        this.doorKickingProgress.put(player, 0.1);
        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.startedKickingDoor);
    }

    @EventHandler
    public void onPlayerSneak(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();

        if (!this.doorKickingProgress.containsKey(player)) {
            return;
        }

        if (!player.isSneaking()) {
            this.playerSneaking.put(player, false);

            return;
        }

        BukkitTask task = this.doorKickingTasks.get(player);
        if (task != null) {
            task.cancel();
            this.doorKickingTasks.remove(player);
        }

        double progress = this.doorKickingProgress.get(player);
        this.doorKickingProgress.put(player, progress + 0.75);

        double bossBarProgress = progress / (10 * this.doorKickingLevel.get(player));

        if (bossBarProgress < 0) {
            bossBarProgress = 0;
        }

        if (bossBarProgress > 1) {
            bossBarProgress = 1;
        }

        BossBar bossBar = this.playersBossBars.get(player);
        bossBar.setProgress(bossBarProgress);

        if (progress >= 10 * this.doorKickingLevel.get(player)) {
            this.doorKickingLevel.put(player, this.doorKickingLevel.get(player) + 1);
            this.doorKickingProgress.put(player, 0.1);

            bossBar.setTitle(ChatColor.translateAlternateColorCodes('&',
                    this.robberyConfiguration.kickDoorBossBarTitle + this.doorKickingLevel.get(player)));
        }

        if (this.doorKickingLevel.get(player) > 3) {
            bossBar.removePlayer(player);

            this.playersBossBars.remove(player);
            this.doorKickingLevel.remove(player);
            this.doorKickingProgress.remove(player);
            this.playerSneaking.remove(player);
            this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.endedKickingDoor);

            BlockState blockState = this.blockStates.get(player);

            Openable openable = (Openable) blockState.getBlockData();
            openable.setOpen(!openable.isOpen());
            blockState.setBlockData(openable);
            blockState.update();

            this.blockStates.remove(player);

            Optional<Robbery> robbery = this.robberyService.getRobbery(player.getUniqueId());

            if (robbery.isEmpty()) {
                return;
            }

            robbery.get().addOpenedDoor(blockState);
        }
    }

    public void startDecreasingTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : playerSneaking.keySet()) {
                    if (!playerSneaking.get(player) && doorKickingProgress.containsKey(player)) {
                        double playerLevel = doorKickingLevel.get(player);

                        if (playerLevel == 2) {
                            playerLevel = 1.5;
                        }

                        if (playerLevel == 3) {
                            playerLevel = 2;
                        }

                        BukkitTask task = new BukkitRunnable() {
                            @Override
                            public void run() {
                                double progress = doorKickingProgress.get(player);

                                if (progress == 0.1) {
                                    this.cancel();
                                    return;
                                }

                                if (progress <= 0) {
                                    BossBar bossBar = playersBossBars.get(player);
                                    bossBar.removePlayer(player);

                                    playersBossBars.remove(player);
                                    doorKickingLevel.remove(player);
                                    doorKickingProgress.remove(player);
                                    playerSneaking.remove(player);
                                    blockStates.remove(player);

                                    notificationAnnouncer.sendMessage(player, messageConfiguration.robbery.failedToKickDoor);
                                    this.cancel();
                                    return;
                                }

                                if (progress > 0) {
                                    progress -= 1;
                                    doorKickingProgress.put(player, progress);

                                    BossBar bossBar = playersBossBars.get(player);
                                    double bossBarProgress = progress / (10 * doorKickingLevel.get(player));

                                    if (bossBarProgress < 0) {
                                        bossBarProgress = 0;
                                    }

                                    bossBar.setProgress(bossBarProgress);
                                }
                            }
                        }.runTaskLater(plugin, (long) (robberyConfiguration.regressionStartTime / (playerLevel) / 1.5));

                        doorKickingTasks.put(player, task);
                    }
                }
            }
        }.runTaskTimerAsynchronously(this.plugin, 1L, 15L);
    }

}