package com.eripe14.houses.house.rent;

import com.eripe14.database.Database;
import com.eripe14.database.document.Document;
import com.eripe14.database.document.DocumentCollection;
import com.eripe14.houses.house.House;
import panda.std.Option;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class RentService {

    private final Map<String, Rent> rents = new HashMap<>();
    private final DocumentCollection documentCollection;

    public RentService(Database database) {
        this.documentCollection = database.getOrCreateCollection("houses_rents");
        for (Document document : this.documentCollection.getAllDocuments()) {
            Rent rent = (Rent) document;
            this.rents.put(rent.getHouseId(), rent);
        }
    }

    public Rent createRent(UUID renter, House house, int days) {
        Duration rentDuration = Duration.ofDays(days);

        return new Rent(house.getHouseId(), renter, house.getDailyRentalPrice(), rentDuration);
    }

    public void addRent(Rent rent) {
        this.rents.put(rent.getHouseId(), rent);
        this.documentCollection.addDocument(rent.getHouseId(), rent);
    }

    public void removeRent(String houseId) {
        this.rents.remove(houseId);
        this.documentCollection.removeDocument(houseId);
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