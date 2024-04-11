package com.eripe14.houses.house.rent;

import com.eripe14.houses.house.House;
import panda.std.Option;

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

    public Rent createRent(UUID renter, House house, int pricePerDay, int days) {
        Duration rentDuration = Duration.ofDays(days);
        RentedHouse rentedHouse = new RentedHouse(house.getHouseId(), renter, house.getRegion(), pricePerDay);

        return new Rent(house.getHouseId(), renter, rentDuration, rentedHouse);
    }

    public void addRent(Rent rent) {
        this.rents.put(rent.getHouseId(), rent);
    }

    public void removeRent(String houseId) {
        this.rents.remove(houseId);
    }

    public Optional<Rent> getPlayersRent(UUID renter) {
        return this.rents.values().stream()
                .filter(rent -> rent.getRenter().equals(renter))
                .findFirst();
    }

    public boolean isTimeToRemind(Rent rent, Duration timeReminderBeforeRentEnd) {
        Instant rentEnd = rent.getEndOfRent();

        return rentEnd.isAfter(Instant.now().plus(timeReminderBeforeRentEnd));
    }

    public Option<Rent> getRent(String houseId) {
        return Option.of(this.rents.get(houseId));
    }

    public Collection<Rent> getAllRents() {
        return Collections.unmodifiableCollection(this.rents.values());
    }

}