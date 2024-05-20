package com.eripe14.houses.robbery;

import panda.std.Option;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RobberyService {

    private final Map<String, Robbery> robberies = new HashMap<>(); // key is house id

    public void addRobbery(Robbery robbery) {
        this.robberies.put(robbery.getHouseId(), robbery);
    }

    public void removeRobbery(String houseId) {
        this.robberies.remove(houseId);
    }

    public Option<Robbery> getRobbery(String houseId) {
        return Option.of(this.robberies.get(houseId));
    }

    public Optional<Robbery> getRobbery(UUID thiefUuid) {
        return this.robberies.values().stream().filter(robbery -> robbery.getThief().equals(thiefUuid)).findFirst();
    }

    public boolean isPlayerRobbing(UUID playerId) {
        return this.robberies.values().stream().map(Robbery::getThief).anyMatch(thief -> thief.equals(playerId));
    }

    public boolean isHouseRobbed(String houseId) {
        return this.robberies.containsKey(houseId);
    }

    public Collection<Robbery> getRobberies() {
        return Collections.unmodifiableCollection(this.robberies.values());
    }

}