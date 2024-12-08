package com.eripe14.houses.house;

import com.eripe14.database.document.Document;
import com.eripe14.houses.house.member.HouseMember;
import com.eripe14.houses.house.owner.Owner;
import com.eripe14.houses.house.region.HouseRegion;
import com.eripe14.houses.house.renovation.Renovation;
import com.eripe14.houses.house.renovation.RenovationData;
import com.eripe14.houses.house.renovation.request.RenovationRequest;
import com.eripe14.houses.house.rent.Rent;
import panda.std.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class House implements Document {

    private final String houseId;
    private final Map<UUID, HouseMember> members;
    private final HouseRegion region;
    private final int buyPrice;
    private final int dailyRentalPrice;
    private final String blockOfFlatsId;
    private final RenovationData renovationData;
    private Option<Owner> owner;
    private Option<Rent> rent;
    private Option<RenovationRequest> renovationRequest;
    private Option<Renovation> currentRenovation;
    private boolean hasAlarm;

    public House(String houseId, Option<Owner> owner, HouseRegion region, String blockOfFlatsId, int dailyRentalPrice) {
        this.houseId = houseId;
        this.owner = owner;
        this.members = new HashMap<>();
        this.region = region;
        this.buyPrice = 0;
        this.dailyRentalPrice = dailyRentalPrice;
        this.renovationData = new RenovationData(this.houseId);
        this.blockOfFlatsId = blockOfFlatsId;
        this.rent = Option.none();
        this.renovationRequest = Option.none();
        this.currentRenovation = Option.none();
        this.hasAlarm = false;
    }

    public House(String houseId, Option<Owner> owner, HouseRegion region, String blockOfFlatsId, int buyPrice, int dailyRentalPrice) {
        this.houseId = houseId;
        this.owner = owner;
        this.members = new HashMap<>();
        this.region = region;
        this.buyPrice = buyPrice;
        this.dailyRentalPrice = dailyRentalPrice;
        this.renovationData = new RenovationData(this.houseId);
        this.blockOfFlatsId = blockOfFlatsId;
        this.rent = Option.none();
        this.renovationRequest = Option.none();
        this.currentRenovation = Option.none();
        this.hasAlarm = false;
    }

    public House(
            String houseId,
            Map<UUID, HouseMember> members,
            HouseRegion region,
            int buyPrice,
            int dailyRentalPrice,
            RenovationData renovationData,
            String blockOfFlatsId,
            Owner owner,
            Rent rent,
            RenovationRequest renovationRequest,
            Renovation currentRenovation,
            boolean hasAlarm
    ) {
        this.houseId = houseId;
        this.members = members;
        this.region = region;
        this.buyPrice = buyPrice;
        this.dailyRentalPrice = dailyRentalPrice;
        this.renovationData = renovationData;
        this.blockOfFlatsId = blockOfFlatsId;
        this.owner = Option.of(owner);
        this.rent = Option.of(rent);
        this.renovationRequest = Option.of(renovationRequest);
        this.currentRenovation = Option.of(currentRenovation);
        this.hasAlarm = hasAlarm;
    }

    public House(
            String houseId,
            Map<UUID, HouseMember> members,
            HouseRegion region,
            int buyPrice,
            int dailyRentalPrice,
            RenovationData renovationData,
            String blockOfFlatsId,
            Option<Owner> owner,
            Option<Rent> rent,
            Option<RenovationRequest> renovationRequest,
            Option<Renovation> currentRenovation,
            boolean hasAlarm
    ) {
        this.houseId = houseId;
        this.members = members;
        this.region = region;
        this.buyPrice = buyPrice;
        this.dailyRentalPrice = dailyRentalPrice;
        this.renovationData = renovationData;
        this.blockOfFlatsId = blockOfFlatsId;
        this.owner = owner;
        this.rent = rent;
        this.renovationRequest = renovationRequest;
        this.currentRenovation = currentRenovation;
        this.hasAlarm = hasAlarm;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public Map<UUID, HouseMember> getMembers() {
        return this.members;
    }

    public HouseRegion getRegion() {
        return this.region;
    }

    public int getBuyPrice() {
        return this.buyPrice;
    }

    public int getDailyRentalPrice() {
        return this.dailyRentalPrice;
    }

    public RenovationData getRenovationData() {
        return this.renovationData;
    }

    public Option<Rent> getRent() {
        return this.rent;
    }

    public Option<Owner> getOwner() {
        return this.owner;
    }

    public Option<RenovationRequest> getRenovationRequest() {
        return this.renovationRequest;
    }

    public Option<Renovation> getCurrentRenovation() {
        return this.currentRenovation;
    }

    public String getBlockOfFlatsId() {
        return this.blockOfFlatsId;
    }

    public boolean hasAlarm() {
        return this.hasAlarm;
    }

    public void setRent(Rent rent) {
        this.rent = Option.of(rent);
    }

    public void setOwner(Owner owner) {
        this.owner = Option.of(owner);
    }

    public void setRenovationRequest(RenovationRequest renovationRequest) {
        this.renovationRequest = Option.of(renovationRequest);
    }

    public void setCurrentRenovation(Renovation renovation) {
        this.currentRenovation = Option.of(renovation);
    }

    public void setHasAlarm(boolean hasAlarm) {
        this.hasAlarm = hasAlarm;
    }

    @Override
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}