package com.eripe14.houses.house.renovation;

import com.eripe14.database.Database;
import com.eripe14.database.document.Document;
import com.eripe14.database.document.DocumentCollection;
import com.eripe14.houses.house.renovation.request.RenovationRequest;
import panda.std.Option;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RenovationService {

    private final Map<String, Renovation> renovations = new HashMap<>();
    private final DocumentCollection documentCollection;

    public RenovationService(Database database) {
        this.documentCollection = database.getOrCreateCollection("houses_renovations");
        for (Document document : this.documentCollection.getAllDocuments()) {
            Renovation renovation = (Renovation) document;
            this.renovations.put(renovation.getHouseId(), renovation);
        }
    }

    public Renovation addRenovation(RenovationRequest renovationRequest) {
        Duration duration = Duration.ofDays(renovationRequest.getRenovationDays());
        Instant endMoment = Instant.now().plus(duration);

        Renovation renovation = new Renovation(
                renovationRequest.getSender(),
                renovationRequest.getHouseId(),
                renovationRequest.getRenovationType(),
                endMoment
        );

        this.renovations.put(renovation.getHouseId(), renovation);
        this.documentCollection.addDocument(renovation.getHouseId(), renovation);
        return renovation;
    }

    public void removeRenovation(Renovation renovation) {
        this.removeRenovation(renovation.getHouseId());
    }

    public void removeRenovation(String houseId) {
        this.renovations.remove(houseId);
        this.documentCollection.removeDocument(houseId);
    }

    public Option<Renovation> getRenovation(String houseId) {
        return Option.of(this.renovations.get(houseId));
    }

    public Collection<Renovation> getRenovations() {
        return Collections.unmodifiableCollection(this.renovations.values());
    }

}