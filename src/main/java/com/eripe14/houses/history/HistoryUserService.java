package com.eripe14.houses.history;

import panda.std.Option;
import pl.craftcityrp.developerapi.data.DataBit;
import pl.craftcityrp.developerapi.data.DataChunk;
import pl.craftcityrp.developerapi.data.DataManager;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HistoryUserService {

    private final Map<UUID, HistoryUser> historyUsers = new HashMap<>();
    private final DataManager dataManager;
    private final DataChunk dataChunk;

    public HistoryUserService(DataManager dataManager) {
        this.dataManager = dataManager;

        if (!this.dataManager.getData().containsKey("historyUsers")) {
            this.dataManager.getData().put("historyUsers", new DataBit(new DataChunk()));
        }

        this.dataChunk = this.dataManager.getData().get("historyUsers").asChunk();

        for (String key : this.dataChunk.getData().keySet()) {
            DataChunk userChunk = this.dataChunk.getBit(key).asChunk();

            UUID uuid = UUID.fromString(userChunk.getBit("uuid").asString());
            String name = userChunk.getBit("name").asString();

            Map<String, HistorySell> historyPurchase = new HashMap<>();
            DataChunk historyPurchaseChunk = userChunk.getBit("historyPurchase").asChunk();

            for (String purchaseKey : historyPurchaseChunk.getData().keySet()) {
                DataChunk purchaseChunk = historyPurchaseChunk.getBit(purchaseKey).asChunk();

                String houseId = purchaseChunk.getBit("houseId").asString();
                double gainedMoney = purchaseChunk.getBit("gainedMoney").asDouble();
                Instant soldTime = Instant.parse(purchaseChunk.getBit("soldTime").asString());

                historyPurchase.put(houseId, new HistorySell(houseId, gainedMoney, soldTime));
            }

            Set<String> leftHouses = new HashSet<>();
            Set<DataBit> leftHousesChunk = userChunk.getBit("leftHouses").asBitSet();

            for (DataBit value : leftHousesChunk) {
                leftHouses.add(value.asString());
            }

            HistoryUser historyUser = new HistoryUser(uuid, name, historyPurchase, leftHouses);
            this.historyUsers.put(uuid, historyUser);
        }
    }

    public HistoryUser create(UUID uuid, String name) {
        return new HistoryUser(uuid, name);
    }

    public boolean exists(UUID uuid) {
        return this.historyUsers.containsKey(uuid);
    }

    public void addUser(HistoryUser historyUser) {
        this.historyUsers.put(historyUser.getUuid(), historyUser);
        this.dataChunk.updateBit(historyUser.getUuid().toString(), historyUser);
    }

    public Option<HistoryUser> getUser(UUID uuid) {
        return Option.of(this.historyUsers.get(uuid));
    }

    public Collection<HistoryUser> getUsers() {
        return this.historyUsers.values();
    }
}