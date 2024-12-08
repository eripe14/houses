package com.eripe14.houses.history;

import com.eripe14.database.document.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HistoryUser implements Document {

    private final UUID uuid;
    private final String name;
    private final Map<String, HistorySell> historyPurchase;
    private final Set<String> leftHouses;

    public HistoryUser(UUID uuid, String name) {
        this(uuid, name, new HashMap<>(), new HashSet<>());
    }

    public HistoryUser(UUID uuid, String name, Map<String, HistorySell> historyPurchase, Set<String> leftHouses) {
        this.uuid = uuid;
        this.name = name;
        this.historyPurchase = historyPurchase;
        this.leftHouses = leftHouses;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Map<String, HistorySell> getHistoryPurchase() {
        return historyPurchase;
    }

    public Set<String> getLeftHouses() {
        return leftHouses;
    }

    public void addHistoryPurchase(HistorySell historySell) {
        this.historyPurchase.put(historySell.getHouseId(), historySell);
    }

    public void addLeftHouse(String houseId) {
        this.leftHouses.add(houseId);
    }

    @Override
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}