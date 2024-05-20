package com.eripe14.houses.house.owner;

import pl.craftcityrp.developerapi.data.DataBit;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class Owner extends DataBit {

    private final UUID uuid;
    private final String name;
    private final Instant ownerSince;

    public Owner(UUID uuid, String name) {
        super(null);
        this.uuid = uuid;
        this.name = name;
        this.ownerSince = Instant.now();
    }

    public Owner(UUID uuid, String name, Instant ownerSince) {
        super(null);
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
    public Object asJson() {
        return Map.of(
                "uuid", this.uuid,
                "name", this.name,
                "ownerSince", this.ownerSince.toString()
        );
    }
}