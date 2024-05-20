package com.eripe14.houses.robbery;

import com.eripe14.houses.alert.Alert;
import com.eripe14.houses.alert.AlertFormatter;
import com.eripe14.houses.alert.AlertHandler;
import com.eripe14.houses.configuration.implementation.MessageConfiguration;
import com.eripe14.houses.configuration.implementation.RobberyConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.region.HouseDistrict;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RobberyStartHandler {

    private final Server server;
    private final HouseService houseService;
    private final RobberyService robberyService;
    private final AlertHandler alertHandler;
    private final MessageConfiguration messageConfiguration;
    private final RobberyConfiguration robberyConfiguration;

    public RobberyStartHandler(
            Server server,
            HouseService houseService,
            RobberyService robberyService,
            AlertHandler alertHandler,
            MessageConfiguration messageConfiguration,
            RobberyConfiguration robberyConfiguration
    ) {
        this.server = server;
        this.houseService = houseService;
        this.robberyService = robberyService;
        this.alertHandler = alertHandler;
        this.messageConfiguration = messageConfiguration;
        this.robberyConfiguration = robberyConfiguration;
    }

    public void notifyPlayers(Robbery robbery) {
        Option<House> houseOption = this.houseService.getHouse(robbery.getHouseId());

        if (houseOption.isEmpty()) {
            return;
        }

        House house = houseOption.get();

        AlertFormatter formatter = new AlertFormatter();
        formatter.register("{HOUSE}", house.getHouseId());

        for (Player onlinePlayer : this.server.getOnlinePlayers()) {
            if (!onlinePlayer.hasPermission(this.robberyConfiguration.policePermission)) {
                continue;
            }

            Alert policeAlert = new Alert(
                    onlinePlayer.getUniqueId(),
                    this.messageConfiguration.robbery.policeRobberySubject,
                    this.messageConfiguration.robbery.policeRobberyMessage,
                    formatter
            );

            if (!house.hasAlarm() && house.getOwner().isPresent()) {
                this.alertHandler.sendAlertAfterTime(
                        onlinePlayer,
                        policeAlert,
                        this.robberyConfiguration.policeNotificationDelay
                );
                continue;
            }

            this.alertHandler.sendAlert(
                    onlinePlayer,
                    policeAlert
            );
        }

        if (house.getOwner().isEmpty()) {
            return;
        }

        if (!house.hasAlarm()) {
            return;
        }

        List<Player> onlineStuffInHouse = this.houseService.getOnlineStuffInHouse(this.server, house);

        for (Player onlineStuff : onlineStuffInHouse) {
            Alert stuffAlert = new Alert(
                    onlineStuff.getUniqueId(),
                    this.messageConfiguration.robbery.houseStuffRobberySubject,
                    this.messageConfiguration.robbery.houseStuffRobberyMessage,
                    formatter
            );

            this.alertHandler.sendAlert(
                    onlineStuff,
                    stuffAlert
            );
        }
    }

    public String getRobberyRequest(Player player) {
        Option<House> randomHouseForRobbery = this.getRandomHouseForRobbery(player);

        if (randomHouseForRobbery.isEmpty()) {
            return "";
        }

        int minMaxWeight = this.robberyConfiguration.minMaxWeight;
        int maxMaxWeight = this.robberyConfiguration.maxMaxWeight;
        int randomMaxWeight = new Random().nextInt((maxMaxWeight - minMaxWeight) + 1) + minMaxWeight;

        House house = randomHouseForRobbery.get();
        Robbery robbery = new Robbery(
                player.getUniqueId(),
                house.getHouseId(),
                randomMaxWeight
        );

        this.robberyService.addRobbery(robbery);
        return house.getHouseId();
    }

    public Option<House> getRandomHouseForRobbery(Player thief) {
        Collection<House> houses;

        if (!this.houseService.getBoughtHouses().isEmpty()) {
            if ((Math.random() * 100) < this.robberyConfiguration.npcHouseProbability) {
                houses = this.houseService.getNpcHouses();
            } else {
                houses = this.houseService.getAllHouses();
            }
        } else {
            houses = this.houseService.getNpcHouses();
        }

        Map<Double, HouseDistrict> districtProbability = this.robberyConfiguration.districtProbability;

        double totalProbability = districtProbability.keySet().stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = Math.random() * totalProbability;
        double cumulativeProbability = 0.0;

        for (Map.Entry<Double, HouseDistrict> entry : districtProbability.entrySet()) {
            cumulativeProbability += entry.getKey();
            if (cumulativeProbability >= randomValue) {
                HouseDistrict randomDistrict = entry.getValue();
                Collection<House> houseInDistrict = this.houseService.getHouseInDistrict(houses, randomDistrict);

                if (houseInDistrict.isEmpty()) {
                    continue;
                }

                int randomIndex = (int) (Math.random() * houseInDistrict.size());
                House house = houseInDistrict.stream().toList().get(randomIndex);

                if (this.robberyService.isHouseRobbed(house.getHouseId())) {
                    continue;
                }

                if (house.getOwner().isEmpty()) {
                    return Option.of(house);
                }

                if (house.getOwner().get().getUuid().equals(thief.getUniqueId())) {
                    continue;
                }

                if (house.getMembers().containsKey(thief.getUniqueId())) {
                    continue;
                }

                List<Player> onlineStuffInHouse = this.houseService.getOnlineStuffInHouse(this.server, house);

                if (onlineStuffInHouse.isEmpty()) {
                    continue;
                }

                return Option.of(house);
            }
        }

        return Option.none();
    }

}