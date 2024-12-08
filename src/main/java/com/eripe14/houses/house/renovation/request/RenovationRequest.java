package com.eripe14.houses.house.renovation.request;

import com.eripe14.database.document.Document;
import com.eripe14.houses.house.renovation.RenovationType;

import java.time.Instant;
import java.util.UUID;

public class RenovationRequest implements Document {

    private final UUID sender;
    private final String senderName;
    private final String houseId;
    private final RenovationType renovationType;
    private final int renovationDays;
    private final String request;
    private final Instant creationTime;

    public RenovationRequest(UUID sender, String senderName, String houseId, RenovationType renovationType, int renovationDays, String request) {
        this.sender = sender;
        this.senderName = senderName;
        this.houseId = houseId;
        this.renovationType = renovationType;
        this.renovationDays = renovationDays;
        this.request = request;
        this.creationTime = Instant.now();
    }

    public RenovationRequest(UUID sender, String senderName, String houseId, RenovationType renovationType, int renovationDays, String request, Instant creationTime) {
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
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}