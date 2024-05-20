package com.eripe14.houses.house.rent;

import com.eripe14.houses.house.House;
import panda.std.Option;
import pl.craftcityrp.developerapi.data.DataBit;
import pl.craftcityrp.developerapi.data.DataChunk;
import pl.craftcityrp.developerapi.data.DataManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RentService {

    private final Map<String, Rent> rents = new HashMap<>();
    private final DataManager dataManager;
    private final DataChunk dataChunk;

    public RentService(DataManager dataManager) {
        this.dataManager = dataManager;

        if (!this.dataManager.getData().containsKey("rents")) {
            this.dataManager.getData().put("rents", new DataBit(new DataChunk()));
        }

        this.dataChunk = this.dataManager.getData().get("rents").asChunk();

        for (String key : this.dataChunk.getData().keySet()) {
            DataChunk rentChunk = this.dataChunk.getBit(key).asChunk();
            String houseId = rentChunk.getBit("houseId").asString();

            Rent rent = new Rent(
                    houseId,
                    UUID.fromString(rentChunk.getBit("renter").asString()),
                    rentChunk.getBit("pricePerDay").asInt(),
                    Duration.parse(rentChunk.getBit("rentDuration").asString()),
                    Instant.parse(rentChunk.getBit("endOfRent").asString())
            );

            this.rents.put(houseId, rent);
        }
    }

    public Rent createRent(UUID renter, House house, int days) {
        Duration rentDuration = Duration.ofDays(days);

        return new Rent(house.getHouseId(), renter, house.getDailyRentalPrice(), rentDuration);
    }

    public void addRent(Rent rent) {
        this.rents.put(rent.getHouseId(), rent);
        this.dataChunk.updateBit(rent.getHouseId(), rent);
    }

    public void removeRent(String houseId) {
        this.rents.remove(houseId);
        this.dataChunk.removeBit(houseId);
    }

    public Optional<Rent> getPlayersRent(UUID renter) {
        return this.rents.values().stream()
                .filter(rent -> rent.getRenter().equals(renter))
                .findFirst();
    }

    public boolean isTimeToRemind(Rent rent, Duration timeReminderBeforeRentEnd) {
        Instant rentEnd = rent.getEndOfRent();

        return Instant.now().isAfter(rentEnd.minus(timeReminderBeforeRentEnd));
    }

    public Option<Rent> getRent(String houseId) {
        return Option.of(this.rents.get(houseId));
    }

    public Collection<Rent> getAllRents() {
        return Collections.unmodifiableCollection(this.rents.values());
    }

}