package com.eripe14.houses.house.renovation.request;

import com.eripe14.houses.house.renovation.RenovationType;
import pl.craftcityrp.developerapi.data.DataBit;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class RenovationRequest extends DataBit {

    private final UUID sender;
    private final String senderName;
    private final String houseId;
    private final RenovationType renovationType;
    private final int renovationDays;
    private final String request;
    private final Instant creationTime;

    public RenovationRequest(UUID sender, String senderName, String houseId, RenovationType renovationType, int renovationDays, String request) {
        super(null);
        this.sender = sender;
        this.senderName = senderName;
        this.houseId = houseId;
        this.renovationType = renovationType;
        this.renovationDays = renovationDays;
        this.request = request;
        this.creationTime = Instant.now();
    }

    public RenovationRequest(UUID sender, String senderName, String houseId, RenovationType renovationType, int renovationDays, String request, Instant creationTime) {
        super(null);
        this.sender = sender;
        this.senderName = senderName;
        this.houseId = houseId;
        this.renovationType = renovationType;
        this.renovationDays = renovationDays;
        this.request = request;
        this.creationTime = creationTime;
    }

    public UUID getSender() {
        return this.sender;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public RenovationType getRenovationType() {
        return this.renovationType;
    }

    public int getRenovationDays() {
        return this.renovationDays;
    }

    public String getRequest() {
        return this.request;
    }

    public Instant getCreationTime() {
        return this.creationTime;
    }

    @Override
    public Object asJson() {
        return Map.of(
                "sender", this.sender,
                "senderName", this.senderName,
                "houseId", this.houseId,
                "renovationType", this.renovationType,
                "renovationDays", this.renovationDays,
                "request", this.request,
                "creationTime", this.creationTime.toString()
        );
    }
}