package com.eripe14.houses.house.renovation.request.acceptance;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.renovation.Renovation;
import com.eripe14.houses.house.renovation.RenovationType;
import panda.std.Option;
import pl.craftcityrp.developerapi.data.DataBit;
import pl.craftcityrp.developerapi.data.DataChunk;
import pl.craftcityrp.developerapi.data.DataManager;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RenovationAcceptanceService {

    private final Map<String, RenovationAcceptanceRequest> renovationAcceptanceRequests = new HashMap<>();
    private final DataManager dataManager;
    private final DataChunk dataChunk;

    public RenovationAcceptanceService(DataManager dataManager) {
        this.dataManager = dataManager;

        if (!this.dataManager.getData().containsKey("renovationAcceptanceRequests")) {
            this.dataManager.getData().put("renovationAcceptanceRequests", new DataBit(new DataChunk()));
        }

        this.dataChunk = this.dataManager.getData().get("renovationAcceptanceRequests").asChunk();

        for (String key : this.dataChunk.getData().keySet()) {
            DataChunk renovationAcceptanceRequestChunk = this.dataChunk.getBit(key).asChunk();
            String houseId = renovationAcceptanceRequestChunk.getBit("houseId").asString();

            RenovationAcceptanceRequest renovationAcceptanceRequest = new RenovationAcceptanceRequest(
                    houseId,
                    RenovationType.valueOf(renovationAcceptanceRequestChunk.getBit("renovationType").asString()),
                    Instant.parse(renovationAcceptanceRequestChunk.getBit("startMoment").asString()),
                    Instant.parse(renovationAcceptanceRequestChunk.getBit("endMoment").asString())
            );

            this.renovationAcceptanceRequests.put(houseId, renovationAcceptanceRequest);
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
        this.dataChunk.updateBit(house.getHouseId(), renovationAcceptanceRequest);
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
        this.dataChunk.removeBit(houseId);
    }

    public Collection<RenovationAcceptanceRequest> getRenovationAcceptanceRequests() {
        return Collections.unmodifiableCollection(this.renovationAcceptanceRequests.values());
    }

}