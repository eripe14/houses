package com.eripe14.houses.house.renovation;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.renovation.request.acceptance.RenovationAcceptanceService;
import com.eripe14.houses.schematic.SchematicService;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import panda.std.Option;

import java.time.Instant;

public class RenovationExpireTask extends BukkitRunnable {

    private final Server server;
    private final RenovationService renovationService;
    private final RenovationAcceptanceService renovationAcceptanceService;
    private final SchematicService schematicService;
    private final HouseService houseService;
    private final AlertHandler alertHandler;
    private final MessageConfiguration messageConfiguration;
    private final PluginConfiguration pluginConfiguration;

    public RenovationExpireTask(
            Server server,
            RenovationService renovationService,
            RenovationAcceptanceService renovationAcceptanceService,
            SchematicService schematicService,
            HouseService houseService,
            AlertHandler alertHandler,
            MessageConfiguration messageConfiguration,
            PluginConfiguration pluginConfiguration
    ) {
        this.server = server;
        this.renovationService = renovationService;
        this.renovationAcceptanceService = renovationAcceptanceService;
        this.schematicService = schematicService;
        this.houseService = houseService;
        this.alertHandler = alertHandler;
        this.messageConfiguration = messageConfiguration;
        this.pluginConfiguration = pluginConfiguration;
    }

    @Override
    public void run() {
        for (Renovation renovation : this.renovationService.getRenovations()) {
            Instant instant = renovation.getEndMoment();

            if (!Instant.now().isAfter(instant)) {
                continue;
            }

            String houseId = renovation.getHouseId();
            Option<House> houseOption = this.houseService.getHouse(houseId);

            if (houseOption.isEmpty()) {
                return;
            }

            House house = houseOption.get();
            Owner owner = house.getOwner().get();

            AlertFormatter formatter = new AlertFormatter();
            formatter.register("{HOUSE}", house.getHouseId());

            for (Player onlinePlayer : this.server.getOnlinePlayers()) {
                if (!onlinePlayer.hasPermission(this.pluginConfiguration.renovationApplicationsPermission)) {
                    return;
                }

                Alert alert = new Alert(
                        onlinePlayer.getUniqueId(),
                        this.messageConfiguration.house.renovationTerminateSubject,
                        this.messageConfiguration.house.renovationTerminateMessage,
                        formatter
                );

                this.alertHandler.sendAlertIfPlayerNotOnline(onlinePlayer.getUniqueId(), alert);
            }

            Alert alert = new Alert(
                    owner.getUuid(),
                    this.messageConfiguration.house.renovationTerminateOwnerSubject,
                    this.messageConfiguration.house.renovationTerminateOwnerMessage,
                    formatter
            );

            this.alertHandler.sendAlertIfPlayerNotOnline(owner.getUuid(), alert);

            this.schematicService.saveSchematic(house.getRegion(), "_renovation_backup");
            this.renovationAcceptanceService.createRenovationAcceptanceRequest(house, renovation);
            this.renovationService.removeRenovation(renovation);
            this.houseService.removeRenovation(renovation);
        }
    }


}