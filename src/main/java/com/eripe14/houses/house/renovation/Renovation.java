package com.eripe14.houses.house.renovation;

import pl.craftcityrp.developerapi.data.DataBit;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Renovation extends DataBit {

    private final UUID sender;
    private final String houseId;
    private final RenovationType renovationType;
    private final Instant startMoment;
    private final Instant endMoment;

    public Renovation(UUID sender, String houseId, RenovationType renovationType, Instant endMoment) {
        super(null);
        this.sender = sender;
        this.houseId = houseId;
        this.renovationType = renovationType;
        this.startMoment = Instant.now();
        this.endMoment = endMoment;
    }

    public Renovation(UUID sender, String houseId, RenovationType renovationType, Instant startMoment, Instant endMoment) {
        super(null);
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
    public Object asJson() {
        return Map.of(
                "sender", this.sender,
                "houseId", this.houseId,
                "renovationType", this.renovationType,
                "startMoment", this.startMoment.toString(),
                "endMoment", this.endMoment.toString()
        );
    }
}