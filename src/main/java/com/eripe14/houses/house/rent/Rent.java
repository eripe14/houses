package com.eripe14.houses.house.rent;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public class Rent {

    private final String houseId;
    private final UUID renter;
    private final Duration rentDuration;
    private final Instant endOfRent;
    private final RentedHouse rentedHouse;

    public Rent(String houseId, UUID renter, Duration rentDuration, RentedHouse rentedHouse) {
        this.houseId = houseId;
        this.renter = renter;
        this.rentDuration = rentDuration;
        this.endOfRent = Instant.now().plus(rentDuration);
        this.rentedHouse = rentedHouse;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public UUID getRenter() {
        return this.renter;
    }

    public Duration getRentDuration() {
        return this.rentDuration;
    }

    public Instant getEndOfRent() {
        return this.endOfRent;
    }

    public RentedHouse getRentedHouse() {
        return this.rentedHouse;
    }

}