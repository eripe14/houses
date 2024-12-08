package com.eripe14.houses.house.owner;

import com.eripe14.database.document.Document;

import java.time.Instant;
import java.util.UUID;

public class Owner implements Document {

    private final UUID uuid;
    private final String name;
    private final Instant ownerSince;

    public Owner(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.ownerSince = Instant.now();
    }

    public Owner(UUID uuid, String name, Instant ownerSince) {
        this.uuid = uuid;
        this.name = name;
        this.ownerSince = ownerSince;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

    public Instant getOwnerSince() {
        return this.ownerSince;
    }

    @Override
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}