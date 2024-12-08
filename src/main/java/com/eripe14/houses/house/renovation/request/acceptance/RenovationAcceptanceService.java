package com.eripe14.houses.house.renovation.request.acceptance;

import com.eripe14.database.Database;
import com.eripe14.database.document.Document;
import com.eripe14.database.document.DocumentCollection;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.renovation.Renovation;
import panda.std.Option;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RenovationAcceptanceService {

    private final Map<String, RenovationAcceptanceRequest> renovationAcceptanceRequests = new HashMap<>();
    private final DocumentCollection documentCollection;

    public RenovationAcceptanceService(Database database) {
        this.documentCollection = database.getOrCreateCollection("houses_renovation_acceptance_requests");
        for (Document document : this.documentCollection.getAllDocuments()) {
            RenovationAcceptanceRequest renovationAcceptanceRequest = (RenovationAcceptanceRequest) document;
            this.renovationAcceptanceRequests.put(renovationAcceptanceRequest.getHouseId(), renovationAcceptanceRequest);
        }
    }

    public RenovationAcceptanceRequest createRenovationAcceptanceRequest(House house, Renovation renovation) {
        RenovationAcceptanceRequest renovationAcceptanceRequest = new RenovationAcceptanceRequest(
                house.getHouseId(),
                renovation.getRenovationType(),
                renovation.getStartMoment(),
                renovation.getEndMoment()
        );

        this.renovationAcceptanceRequests.put(house.getHouseId(), renovationAcceptanceRequest);
        this.documentCollection.addDocument(house.getHouseId(), renovationAcceptanceRequest);

        return renovationAcceptanceRequest;
    }

    public Option<RenovationAcceptanceRequest> getRenovationAcceptanceRequest(House house) {
        return Option.of(this.renovationAcceptanceRequests.get(house.getHouseId()));
    }

    public Option<RenovationAcceptanceRequest> getRenovationAcceptanceRequest(String houseId) {
        return Option.of(this.renovationAcceptanceRequests.get(houseId));
    }

    public void removeRenovationAcceptanceRequest(String houseId) {
        this.renovationAcceptanceRequests.remove(houseId);
        this.documentCollection.removeDocument(houseId);
    }

    public Collection<RenovationAcceptanceRequest> getRenovationAcceptanceRequests() {
        return Collections.unmodifiableCollection(this.renovationAcceptanceRequests.values());
    }

}