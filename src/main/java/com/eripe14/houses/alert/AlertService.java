package com.eripe14.houses.alert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class AlertService {

    private final Set<Alert> alerts = new HashSet<>();

    public void addAlert(Alert alert) {
        this.alerts.add(alert);
    }

    public void removeAlert(Alert alert) {
        this.alerts.remove(alert);
    }

    public Set<Alert> getAllPlayerAlerts(UUID uuid) {
        return this.alerts.stream()
                .filter(alert -> alert.target().equals(uuid))
                .collect(Collectors.toSet());
    }

    public Optional<Alert> getPlayerAlert(UUID uuid) {
        return this.alerts.stream()
                .filter(alert -> alert.target().equals(uuid))
                .findFirst();
    }

    public Set<Alert> getAlerts() {
        return Collections.unmodifiableSet(this.alerts);
    }

}