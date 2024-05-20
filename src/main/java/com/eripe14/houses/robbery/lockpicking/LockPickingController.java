package com.eripe14.houses.robbery.lockpicking;

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
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import panda.std.Option;
import panda.utilities.text.Formatter;

import java.util.Optional;

public class LockPickingController implements Listener {

    private final Plugin plugin;
    private final LockPickingService lockPickingService;
    private final RobberyService robberyService;
    private final RobberyStartHandler robberyStartHandler;
    private final HouseService houseService;
    private final ProtectionService protectionService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final RobberyConfiguration robberyConfiguration;
    private final PluginConfiguration pluginConfiguration;
    private final MessageConfiguration messageConfiguration;

    public LockPickingController(
            Plugin plugin,
            LockPickingService lockPickingService,
            RobberyService robberyService,
            RobberyStartHandler robberyStartHandler,
            HouseService houseService,
            ProtectionService protectionService,
            NotificationAnnouncer notificationAnnouncer,
            RobberyConfiguration robberyConfiguration,
            PluginConfiguration pluginConfiguration,
            MessageConfiguration messageConfiguration
    ) {
        this.plugin = plugin;
        this.lockPickingService = lockPickingService;
        this.robberyService = robberyService;
        this.robberyStartHandler = robberyStartHandler;
        this.houseService = houseService;
        this.protectionService = protectionService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.robberyConfiguration = robberyConfiguration;
        this.pluginConfiguration = pluginConfiguration;
        this.messageConfiguration = messageConfiguration;
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

        String nbtKey = this.robberyConfiguration.lockpickItem.nbtKey;

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

        if (this.lockPickingService.containsLockPickingTask(player)) {
            event.setCancelled(true);
            return;
        }

        BlockState blockState = clickedBlock.getState();
        Openable openable = (Openable) blockState.getBlockData();

        if (openable.isOpen()) {
            event.setCancelled(true);
            return;
        }

        itemInUse.setAmount(itemInUse.getAmount() - 1);
        event.setCancelled(true);

        Formatter formatter = new Formatter();
        formatter.register("{TIME}", this.robberyConfiguration.redBarTime);


        new LockPickingTask(
                this.lockPickingService,
                this.notificationAnnouncer,
                this.robberyService,
                this.robberyStartHandler,
                this.robberyConfiguration,
                this.messageConfiguration,
                blockState,
                player
        ).runTaskTimer(this.plugin, 0L, 20L);

        this.notificationAnnouncer.sendMessage(player, this.messageConfiguration.robbery.startedLockPicking, formatter);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location eventTo = event.getTo();
        Location eventFrom = event.getFrom();

        Option<LockPickingTask> lockPickingTaskOption = this.lockPickingService.getLockPickingTask(player);

        if (lockPickingTaskOption.isEmpty()) {
            return;
        }

        event.setCancelled(false);

        if (eventFrom.getX() != eventTo.getX() || eventFrom.getY() != eventTo.getY() || eventFrom.getZ() != eventTo.getZ()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();

        Option<LockPickingTask> taskOption = this.lockPickingService.getLockPickingTask(player);

        if (taskOption.isEmpty()) {
            return;
        }

        LockPickingTask lockPickingTask = taskOption.get();
        event.setCancelled(true);

        if (!lockPickingTask.hasToInteract()) {
            event.setCancelled(true);

            lockPickingTask.cancel();
            lockPickingTask.setHasToInteract(false);
            lockPickingTask.setIsRedBar(false);
            lockPickingTask.setRedBarTimer(0);

            this.lockPickingService.removeLockPickingTask(player);
            this.lockPickingService.removeLockPickingPlayerInteracting(player);

            player.playSound(
                    player.getLocation(),
                    this.robberyConfiguration.lockpickBreakSound,
                    1.0f,
                    1.0f
            );
            this.notificationAnnouncer.sendActionBar(player, this.messageConfiguration.robbery.brokeLockPick);

            return;
        }

        event.setCancelled(true);
        lockPickingTask.setRedBarTimer(0);
        lockPickingTask.setHasToInteract(false);
        lockPickingTask.setIsRedBar(false);
        player.playSound(
                player.getLocation(),
                this.robberyConfiguration.lockpickSuccessSound,
                1.0f,
                1.0f
        );
    }

}