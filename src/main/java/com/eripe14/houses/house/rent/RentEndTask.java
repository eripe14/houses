package com.eripe14.houses.house.rent;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.purchase.HouseSellService;
import org.bukkit.scheduler.BukkitRunnable;
import panda.std.Option;

import java.time.Instant;

public class RentEndTask extends BukkitRunnable {

    private final RentService rentService;
    private final HouseService houseService;
    private final HouseSellService houseSellService;
    private final AlertHandler alertHandler;
    private final MessageConfiguration messageConfiguration;

    public RentEndTask(
            RentService rentService,
            HouseService houseService,
            HouseSellService houseSellService,
            AlertHandler alertHandler,
            MessageConfiguration messageConfiguration
    ) {
        this.rentService = rentService;
        this.houseService = houseService;
        this.houseSellService = houseSellService;
        this.alertHandler = alertHandler;
        this.messageConfiguration = messageConfiguration;
    }

    @Override
    public void run() {
        for (Rent rent : this.rentService.getAllRents()) {
            Instant instant = rent.getEndOfRent();

            if (!Instant.now().isAfter(instant)) {
                continue;
            }

            Option<House> houseOption = this.houseService.getHouse(rent.getHouseId());

            if (houseOption.isEmpty()) {
                continue;
            }

            House house = houseOption.get();
            this.houseSellService.endRent(house);

            AlertFormatter formatter = new AlertFormatter();
            formatter.register("{HOUSE}", house.getHouseId());

            Alert alert = new Alert(
                    house.getOwner().get().getUuid(),
                    this.messageConfiguration.rent.rentEndedSubject,
                    this.messageConfiguration.rent.rentEndedMessage,
                    formatter
            );

            this.alertHandler.sendAlertIfPlayerNotOnline(alert.getTarget(), alert);
        }
    }

}