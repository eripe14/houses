package com.eripe14.houses.house.renovation;

import com.eripe14.houses.house.renovation.request.RenovationRequest;
import panda.std.Option;
import pl.craftcityrp.developerapi.data.DataBit;
import pl.craftcityrp.developerapi.data.DataChunk;
import pl.craftcityrp.developerapi.data.DataManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RenovationService {

    private final Map<String, Renovation> renovations = new HashMap<>();
    private final DataManager dataManager;
    private final DataChunk dataChunk;

    public RenovationService(DataManager dataManager) {
        this.dataManager = dataManager;

        if (!this.dataManager.getData().containsKey("renovations")) {
            this.dataManager.getData().put("renovations", new DataBit(new DataChunk()));
        }

        this.dataChunk = this.dataManager.getData().get("renovations").asChunk();

        for (String key : this.dataChunk.getData().keySet()) {
            DataChunk renovationChunk = this.dataChunk.getBit(key).asChunk();
            String houseId = renovationChunk.getBit("houseId").asString();

            Renovation renovation = new Renovation(
                    UUID.fromString(renovationChunk.getBit("sender").asString()),
                    houseId,
                    RenovationType.valueOf(renovationChunk.getBit("renovationType").asString()),
                    Instant.parse(renovationChunk.getBit("startMoment").asString()),
                    Instant.parse(renovationChunk.getBit("endMoment").asString())
            );

            this.renovations.put(houseId, renovation);
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
        this.dataChunk.updateBit(renovation.getHouseId(), renovation);
        return renovation;
    }

    public void removeRenovation(Renovation renovation) {
        this.renovations.remove(renovation.getHouseId());
        this.dataChunk.removeBit(renovation.getHouseId());
    }

    public void removeRenovation(String houseId) {
        this.renovations.remove(houseId);
        this.dataChunk.removeBit(houseId);
    }

    public Option<Renovation> getRenovation(String houseId) {
        return Option.of(this.renovations.get(houseId));
    }

    public Collection<Renovation> getRenovations() {
        return Collections.unmodifiableCollection(this.renovations.values());
    }

}