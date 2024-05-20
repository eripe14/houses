package com.eripe14.houses.history;

import pl.craftcityrp.developerapi.data.DataBit;

import java.time.Instant;
import java.util.Map;

public class HistorySell extends DataBit {

    private final String houseId;
    private final double gainedMoney;
    private final Instant soldTime;

    public HistorySell(String houseId, double gainedMoney) {
        super(null);
        this.houseId = houseId;
        this.gainedMoney = gainedMoney;
        this.soldTime = Instant.now();
    }

    public HistorySell(String houseId, double gainedMoney, Instant soldTime) {
        super(null);
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
    public Object asJson() {
        return Map.of(
                "houseId",     this.houseId,
                "gainedMoney", this.gainedMoney,
                "soldTime",    this.soldTime.toString()
        );
    }
}