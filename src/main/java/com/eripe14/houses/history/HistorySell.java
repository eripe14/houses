package com.eripe14.houses.history;

import com.eripe14.database.document.Document;

import java.time.Instant;

public class HistorySell implements Document {

    private final String houseId;
    private final double gainedMoney;
    private final Instant soldTime;

    public HistorySell(String houseId, double gainedMoney) {
        this.houseId = houseId;
        this.gainedMoney = gainedMoney;
        this.soldTime = Instant.now();
    }

    public HistorySell(String houseId, double gainedMoney, Instant soldTime) {
        this.houseId = houseId;
        this.gainedMoney = gainedMoney;
        this.soldTime = soldTime;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public double getGainedMoney() {
        return this.gainedMoney;
    }

    public Instant getSoldTime() {
        return this.soldTime;
    }

    @Override
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}