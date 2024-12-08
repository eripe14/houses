package com.eripe14.houses.alert;

import com.eripe14.database.Database;
import com.eripe14.database.document.Document;
import com.eripe14.database.document.DocumentCollection;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AlertService {

    private final Set<Alert> alerts = new HashSet<>();
    private final DocumentCollection documentCollection;

    public AlertService(Database database) {
       this.documentCollection = database.getOrCreateCollection("houses_alerts");
        for (Document document : this.documentCollection.getAllDocuments()) {
            Alert alert = (Alert) document;
            this.alerts.add(alert);
        }
    }

    public void addAlert(Alert alert) {
        this.alerts.add(alert);
        this.documentCollection.addDocument(alert.getUuid().toString(), alert);
    }

    public void removeAlert(Alert alert) {
        this.alerts.remove(alert);
        this.documentCollection.removeDocument(alert.getUuid().toString());
    }

    public Set<Alert> getAllPlayerAlerts(UUID uuid) {
        return this.alerts.stream()
                .filter(alert -> alert.getTarget().equals(uuid))
                .collect(Collectors.toSet());
    }

    public Optional<Alert> getPlayerAlert(UUID uuid) {
        return this.alerts.stream()
                .filter(alert -> alert.getTarget().equals(uuid))
                .findFirst();
    }

    public Set<Alert> getAlerts() {
        return Collections.unmodifiableSet(this.alerts);
    }

}