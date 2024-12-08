package com.eripe14.houses.history;

import com.eripe14.database.Database;
import com.eripe14.database.document.DocumentCollection;
import panda.std.Option;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HistoryUserService {

    private final Map<UUID, HistoryUser> historyUsers = new HashMap<>();
    private final DocumentCollection documentCollection;

    public HistoryUserService(Database database) {
        this.documentCollection = database.getOrCreateCollection("houses_history_users");
        this.documentCollection.getDocuments().forEach((id, document) -> {
            HistoryUser historyUser = (HistoryUser) document;
            this.historyUsers.put(historyUser.getUuid(), historyUser);
        });
    }

    public HistoryUser create(UUID uuid, String name) {
        return new HistoryUser(uuid, name);
    }

    public boolean exists(UUID uuid) {
        return this.historyUsers.containsKey(uuid);
    }

    public void addUser(HistoryUser historyUser) {
        this.historyUsers.put(historyUser.getUuid(), historyUser);
        this.documentCollection.addDocument(historyUser.getUuid().toString(), historyUser);
    }

    public Option<HistoryUser> getUser(UUID uuid) {
        return Option.of(this.historyUsers.get(uuid));
    }

    public Collection<HistoryUser> getUsers() {
        return this.historyUsers.values();
    }
}