package com.eripe14.houses.robbery.lockpicking;

import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.notification.NotificationAnnouncer;
import com.eripe14.houses.robbery.Robbery;
import com.eripe14.houses.robbery.RobberyService;
import com.eripe14.houses.robbery.RobberyStartHandler;
import org.bukkit.ChatColor;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;
import java.util.Random;

public class LockPickingTask extends BukkitRunnable {

    private final LockPickingService lockPickingService;
    private final NotificationAnnouncer notificationAnnouncer;
    private final RobberyService robberyService;
    private final RobberyStartHandler robberyStartHandler;
    private final RobberyConfiguration robberyConfiguration;
    private final MessageConfiguration messageConfiguration;
    private final BlockState blockState;
    private final Player player;
    private final Random random;
    private int secondsPassed = 0;
    private int redBarTimer = 0;
    private int redBars = 0;
    private boolean hasToInteract = false;
    private boolean isRedBar = false;
    private int lastRedBarSegment = 0;

    public LockPickingTask(
            LockPickingService lockPickingService,
            NotificationAnnouncer notificationAnnouncer,
            RobberyService robberyService,
            RobberyStartHandler robberyStartHandler,
            RobberyConfiguration robberyConfiguration,
            MessageConfiguration messageConfiguration,
            BlockState blockState,
            Player player
    ) {
        this.lockPickingService = lockPickingService;
        this.notificationAnnouncer = notificationAnnouncer;
        this.robberyService = robberyService;
        this.robberyStartHandler = robberyStartHandler;
        this.robberyConfiguration = robberyConfiguration;
        this.messageConfiguration = messageConfiguration;
        this.blockState = blockState;
        this.player = player;
        this.random = new Random();
        this.lockPickingService.addLockPickingTask(player, this);
    }

    @Override
    public void run() {
        if (this.secondsPassed > this.robberyConfiguration.lockPickingDuration) {
            this.lockPickingService.removeLockPickingTask(this.player);
            this.notificationAnnouncer.sendMessage(this.player, this.messageConfiguration.robbery.wonLockPicking);

            Optional<Robbery> robberyOptional = this.robberyService.getRobbery(player.getUniqueId());

            if (robberyOptional.isEmpty()) {
                return;
            }

            Openable openable = (Openable) this.blockState.getBlockData();
            openable.setOpen(!openable.isOpen());
            this.blockState.setBlockData(openable);
            this.blockState.update();

            Robbery robbery = robberyOptional.get();

            if (robbery.isPoliceNotified()) {
                this.cancel();
                return;
            }

            robbery.setPoliceNotified(true);
            this.robberyStartHandler.notifyPlayers(robbery);

            this.cancel();
            return;
        }

        if (this.redBarTimer > this.robberyConfiguration.redBarTime) {
            this.lockPickingService.removeLockPickingTask(this.player);
            this.lockPickingService.removeLockPickingPlayerInteracting(this.player);
            this.notificationAnnouncer.sendMessage(this.player, this.messageConfiguration.robbery.lostLockPicking);

            player.playSound(
                    player.getLocation(),
                    this.robberyConfiguration.lockpickBreakSound,
                    1.0f,
                    1.0f
            );

            this.cancel();
            return;
        }

        if (this.hasToInteract) {
            this.notificationAnnouncer.sendTitle(
                    this.player,
                    ChatColor.translateAlternateColorCodes('&', this.robberyConfiguration.lockPickingTitle),
                    ChatColor.translateAlternateColorCodes('&', this.getSubTitle(
                            this.secondsPassed,
                            this.robberyConfiguration.lockPickingDuration,
                            this.robberyConfiguration.totalBars,
                            true
                    )),
                    0,
                    20,
                    0
            );

            this.redBarTimer++;
            return;
        }

        int segment = this.robberyConfiguration.lockPickingDuration / 4;
        int currentSegment = this.secondsPassed / segment + 1;

        if (this.redBars < currentSegment && this.lastRedBarSegment != currentSegment && this.redBars < 4 && (random.nextInt(5) == 0 || this.secondsPassed >= (currentSegment - 1) * segment)) {
            this.isRedBar = true;
            this.redBars++;
            this.redBarTimer = 0;
            this.lastRedBarSegment = currentSegment;
        }

        if (this.secondsPassed % segment == 0) {
            this.lastRedBarSegment = 0;
        }

        if (this.redBars > 4) {
            this.isRedBar = false;
        }

        if (this.secondsPassed == 0 || this.secondsPassed == 1) {
            this.isRedBar = false;
            this.redBars = 0;
            this.lastRedBarSegment = 0;
        }

        if (this.secondsPassed == Math.round(this.robberyConfiguration.lockPickingDuration * 0.9f)) {
            if (this.redBars == 2) {
                this.isRedBar = true;
                this.redBars++;
            }
        }

        this.notificationAnnouncer.sendTitle(
                this.player,
                ChatColor.translateAlternateColorCodes('&', this.robberyConfiguration.lockPickingTitle),
                ChatColor.translateAlternateColorCodes('&', this.getSubTitle(
                        this.secondsPassed,
                        this.robberyConfiguration.lockPickingDuration,
                        this.robberyConfiguration.totalBars,
                        this.isRedBar
                )),
                0,
                20,
                0
        );

        if (!this.isRedBar) {
            this.secondsPassed++;
            this.player.playSound(
                    player.getLocation(),
                    this.robberyConfiguration.lockpickProgressSound,
                    1.0f,
                    1.0f
            );
        }

        if (this.isRedBar) {
            this.redBarTimer++;
            this.hasToInteract = true;
            this.lockPickingService.addLockPickingPlayerInteracting(this.player);
        }
    }

    private String getSubTitle(int current, int max, int totalBars, boolean isRedBar) {
        if (current > max) {
            current = max;
        }

        float percent = (float) current / max;
        int progressBars = Math.round(totalBars * percent);
        int remainingBars = Math.max(0, totalBars - progressBars);

        String colorCode = "&a";
        String progressBar = String.valueOf('|').repeat(progressBars);
        String remainingBar = "&7" + String.valueOf('|').repeat(remainingBars);

        if (isRedBar && progressBars > 0) {
            progressBar = progressBar.substring(0, progressBar.length() - 1) + "&c|";
        }

        return colorCode + progressBar + remainingBar;
    }

    public void setRedBarTimer(int redBarTimer) {
        this.redBarTimer = redBarTimer;
    }

    public void setHasToInteract(boolean hasToInteract) {
        this.hasToInteract = hasToInteract;
    }

    public void setIsRedBar(boolean isRedBar) {
        this.isRedBar = isRedBar;
    }

    public boolean hasToInteract() {
        return this.hasToInteract;
    }

}