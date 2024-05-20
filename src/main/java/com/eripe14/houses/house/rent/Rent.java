package com.eripe14.houses.house.rent;

import pl.craftcityrp.developerapi.data.DataBit;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Rent extends DataBit {

    private UUID renter;
    private final String houseId;
    private final int pricePerDay;
    private final Duration rentDuration;
    private final Instant endOfRent;

    public Rent(String houseId, UUID renter, int pricePerDay, Duration rentDuration) {
        super(null);
        this.houseId = houseId;
        this.renter = renter;
        this.pricePerDay = pricePerDay;
        this.rentDuration = rentDuration;
        this.endOfRent = Instant.now().plus(rentDuration);
    }

    public Rent(String houseId, UUID renter, int pricePerDay, Duration rentDuration, Instant endOfRent) {
        super(null);
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
    public Object asJson() {
        return Map.of(
                "renter", this.renter,
                "houseId", this.houseId,
                "pricePerDay", this.pricePerDay,
                "rentDuration", this.rentDuration.toString(),
                "endOfRent", this.endOfRent.toString()
        );
    }
}