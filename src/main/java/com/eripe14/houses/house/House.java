package com.eripe14.houses.house;

import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.rent.Rent;
import panda.std.Option;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class House {

    private final Instant creationDate;
    private final String houseId;
    private final Map<UUID, HouseMember> members;
    private final HouseRegion region;
    private final int buyPrice;
    private final int dailyRentalPrice;
    private Option<Owner> owner;
    private Option<Rent> rent;

    public House(String houseId, Option<Owner> owner, HouseRegion region, int dailyRentalPrice) {
        this.creationDate = Instant.now();
        this.houseId = houseId;
        this.owner = owner;
        this.members = new HashMap<>();
        this.region = region;
        this.buyPrice = 0;
        this.dailyRentalPrice = dailyRentalPrice;
        this.rent = Option.none();
    }

    public House(String houseId, Option<Owner> owner, HouseRegion region, int buyPrice, int dailyRentalPrice) {
        this.creationDate = Instant.now();
        this.houseId = houseId;
        this.owner = owner;
        this.members = new HashMap<>();
        this.region = region;
        this.buyPrice = buyPrice;
        this.dailyRentalPrice = dailyRentalPrice;
        this.rent = Option.none();
    }

    public Instant getCreationDate() {
        return this.creationDate;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public Option<Owner> getOwner() {
        return this.owner;
    }

    public Map<UUID, HouseMember>  getMembers() {
        return this.members;
    }

    public HouseRegion getRegion() {
        return this.region;
    }

    public Option<Rent> getRent() {
        return this.rent;
    }

    public int getBuyPrice() {
        return this.buyPrice;
    }

    public int getDailyRentalPrice() {
        return this.dailyRentalPrice;
    }

    public void setRent(Rent rent) {
        this.rent = Option.of(rent);
    }

    public void setOwner(Owner owner) {
        this.owner = Option.of(owner);
    }

    @Override
    public String toString() {
        return "House{" +
                "creationDate=" + this.creationDate +
                ", houseId='" + this.houseId + '\'' +
                ", owner=" + this.owner +
                ", members=" + this.members.values() +
                ", region=" + this.region +
                ", rent=" + this.rent +
                ", buyPrice=" + this.buyPrice +
                ", dailyRentalPrice=" + this.dailyRentalPrice +
                '}';
    }

}