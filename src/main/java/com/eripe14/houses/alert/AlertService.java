package com.eripe14.houses.alert;

import pl.craftcityrp.developerapi.data.DataBit;
import pl.craftcityrp.developerapi.data.DataChunk;
import pl.craftcityrp.developerapi.data.DataManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AlertService {

    private final DataManager dataManager;
    private final DataChunk dataChunk;
    private final Set<Alert> alerts = new HashSet<>();

    public AlertService(DataManager dataManager) {
        this.dataManager = dataManager;

        if (!this.dataManager.getData().containsKey("alerts")) {
            this.dataManager.getData().put("alerts", new DataBit(new DataChunk()));
        }

        this.dataChunk = this.dataManager.getData().get("alerts").asChunk();

        for (String key : this.dataChunk.getData().keySet()) {
            DataChunk alertDataChunk = this.dataChunk.getBit(key).asChunk();

            Map<String, String> placeholders = new HashMap<>();

            for (DataBit formatter : alertDataChunk.getBit("formatter").asChunk().getData().values()) {
                DataChunk placeholdersDataChunk = formatter.asChunk();

                placeholdersDataChunk.getData().forEach((placeholder, value) -> placeholders.put(placeholder, value.asString()));
            }

            AlertFormatter alertFormatter = new AlertFormatter(placeholders);

            Alert alert = new Alert(
                    UUID.fromString(alertDataChunk.getBit("uuid").asString()),
                    UUID.fromString(alertDataChunk.getBit("target").asString()),
                    alertDataChunk.getBit("subject").asString(),
                    alertDataChunk.getBit("message").asString(),
                    alertFormatter
            );

            this.alerts.add(alert);
        }
    }

    public void addAlert(Alert alert) {
        this.alerts.add(alert);

        this.dataChunk.updateBit(alert.getUuid().toString(), alert);
    }

    public void removeAlert(Alert alert) {
        this.alerts.remove(alert);

        this.dataChunk.removeBit(alert.getUuid().toString());
        this.dataManager.updateAllToBackend();
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