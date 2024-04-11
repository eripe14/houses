package com.eripe14.houses.house.rent;

import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.HouseRegion;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RentedHouse {

    private final String houseId;
    private final UUID renter;
    private final Set<HouseMember> coOwners;
    private final Set<HouseMember> members;
    private final HouseRegion region;
    private final int dailyRentalPrice;

    public RentedHouse(String houseId, UUID renter, HouseRegion region, int dailyRentalPrice) {
        this.houseId = houseId;
        this.renter = renter;
        this.coOwners = new HashSet<>();
        this.members = new HashSet<>();
        this.region = region;
        this.dailyRentalPrice = dailyRentalPrice;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public UUID getRenter() {
        return this.renter;
    }

    public Set<HouseMember> getCoOwners() {
        return this.coOwners;
    }

    public Set<HouseMember> getMembers() {
        return this.members;
    }

    public HouseRegion getRegion() {
        return this.region;
    }

    public int getDailyRentalPrice() {
        return this.dailyRentalPrice;
    }

}