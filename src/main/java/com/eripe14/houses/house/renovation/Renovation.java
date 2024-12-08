package com.eripe14.houses.house.renovation;

import com.eripe14.database.document.Document;

import java.time.Instant;
import java.util.UUID;

public class Renovation implements Document {

    private final UUID sender;
    private final String houseId;
    private final RenovationType renovationType;
    private final Instant startMoment;
    private final Instant endMoment;

    public Renovation(UUID sender, String houseId, RenovationType renovationType, Instant endMoment) {
        this.sender = sender;
        this.houseId = houseId;
        this.renovationType = renovationType;
        this.startMoment = Instant.now();
        this.endMoment = endMoment;
    }

    public Renovation(UUID sender, String houseId, RenovationType renovationType, Instant startMoment, Instant endMoment) {
        this.sender = sender;
        this.houseId = houseId;
        this.renovationType = renovationType;
        this.startMoment = startMoment;
        this.endMoment = endMoment;
    }

    public UUID getSender() {
        return this.sender;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public RenovationType getRenovationType() {
        return this.renovationType;
    }

    public Instant getStartMoment() {
        return this.startMoment;
    }

    public Instant getEndMoment() {
        return this.endMoment;
    }

    @Override
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}