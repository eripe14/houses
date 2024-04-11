package com.eripe14.houses.house.owner;

import java.util.UUID;

public class Owner {

    private final UUID uuid;
    private final String name;

    public Owner(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String getName() {
        return this.name;
    }

}