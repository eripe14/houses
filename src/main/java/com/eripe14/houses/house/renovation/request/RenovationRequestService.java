package com.eripe14.houses.house.renovation.request;

import com.eripe14.database.Database;
import com.eripe14.database.document.Document;
import com.eripe14.database.document.DocumentCollection;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.renovation.RenovationType;
import org.bukkit.entity.Player;
import panda.std.Option;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RenovationRequestService {

    private final Map<String, RenovationRequest> renovations = new HashMap<>();
    private final DocumentCollection documentCollection;

    public RenovationRequestService(Database database) {
        this.documentCollection = database.getOrCreateCollection("houses_renovation_requests");
        for (Document document : this.documentCollection.getAllDocuments()) {
            RenovationRequest renovationRequest = (RenovationRequest) document;
            this.renovations.put(renovationRequest.getHouseId(), renovationRequest);
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
        this.documentCollection.addDocument(house.getHouseId(), renovationRequest);

        return renovationRequest;
    }

    public void removeRequest(String houseId) {
        this.renovations.remove(houseId);
        this.documentCollection.removeDocument(houseId);
    }

    public Option<RenovationRequest> getRequest(String houseId) {
        return Option.of(this.renovations.get(houseId));
    }

    public Collection<RenovationRequest> getRenovations() {
        return Collections.unmodifiableCollection(this.renovations.values());
    }

}