package com.eripe14.houses.house.rent;

import com.eripe14.database.document.Document;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Rent implements Document {

    private UUID renter;
    private final String houseId;
    private final int pricePerDay;
    private final Duration rentDuration;
    private final Instant endOfRent;

    public Rent(String houseId, UUID renter, int pricePerDay, Duration rentDuration) {
        this.houseId = houseId;
        this.renter = renter;
        this.pricePerDay = pricePerDay;
        this.rentDuration = rentDuration;
        this.endOfRent = Instant.now().plus(rentDuration);
    }

    public Rent(String houseId, UUID renter, int pricePerDay, Duration rentDuration, Instant endOfRent) {
        this.houseId = houseId;
        this.renter = renter;
        this.pricePerDay = pricePerDay;
        this.rentDuration = rentDuration;
        this.endOfRent = endOfRent;
    }

    public void setRenter(UUID renter) {
        this.renter = renter;
    }

    public UUID getRenter() {
        return this.renter;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public int getPricePerDay() {
        return this.pricePerDay;
    }

    public Duration getRentDuration() {
        return this.rentDuration;
    }

    public Instant getEndOfRent() {
        return this.endOfRent;
    }

    @Override
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}