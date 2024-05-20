package com.eripe14.houses.house.renovation.request;

import com.eripe14.houses.house.House;
import com.eripe14.houses.house.renovation.RenovationType;
import org.bukkit.entity.Player;
import panda.std.Option;
import pl.craftcityrp.developerapi.data.DataBit;
import pl.craftcityrp.developerapi.data.DataChunk;
import pl.craftcityrp.developerapi.data.DataManager;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RenovationRequestService {

    private final Map<String, RenovationRequest> renovations = new HashMap<>();
    private final DataManager dataManager;
    private final DataChunk dataChunk;

    public RenovationRequestService(DataManager dataManager) {
        this.dataManager = dataManager;

        if (!this.dataManager.getData().containsKey("renovationRequests")) {
            this.dataManager.getData().put("renovationRequests", new DataBit(new DataChunk()));
        }

        this.dataChunk = this.dataManager.getData().get("renovationRequests").asChunk();

        for (String key : this.dataChunk.getData().keySet()) {
            DataChunk renovationChunk = this.dataChunk.getBit(key).asChunk();
            String houseId = renovationChunk.getBit("houseId").asString();

            RenovationRequest renovationRequest = new RenovationRequest(
                    UUID.fromString(renovationChunk.getBit("sender").asString()),
                    renovationChunk.getBit("senderName").asString(),
                    houseId,
                    RenovationType.valueOf(renovationChunk.getBit("renovationType").asString()),
                    renovationChunk.getBit("renovationDays").asInt(),
                    renovationChunk.getBit("request").asString(),
                    Instant.parse(renovationChunk.getBit("creationTime").asString())
            );

            this.renovations.put(houseId, renovationRequest);
        }
    }

    public RenovationRequest addRequest(Player player, House house, RenovationType renovationType, int days, String requestMessage) {
        RenovationRequest renovationRequest = new RenovationRequest(
                player.getUniqueId(),
                player.getName(),
                house.getHouseId(),
                renovationType,
                days,
                requestMessage
        );
        this.renovations.put(house.getHouseId(), renovationRequest);
        this.dataChunk.updateBit(house.getHouseId(), renovationRequest);

        return renovationRequest;
    }

    public void removeRequest(String houseId) {
        this.renovations.remove(houseId);
        this.dataChunk.removeBit(houseId);
    }

    public Option<RenovationRequest> getRequest(String houseId) {
        return Option.of(this.renovations.get(houseId));
    }

    public Collection<RenovationRequest> getRenovations() {
        return Collections.unmodifiableCollection(this.renovations.values());
    }

}