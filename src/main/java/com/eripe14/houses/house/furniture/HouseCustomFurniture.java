package com.eripe14.houses.house.furniture;

import com.eripe14.houses.position.Position;
import pl.craftcityrp.developerapi.data.DataBit;

import java.util.Map;

public class HouseCustomFurniture extends DataBit {

    private final String namespacedId;
    private final Position position;

    public HouseCustomFurniture(String namespacedId, Position position) {
        super(null);
        this.namespacedId = namespacedId;
        this.position = position;
    }

    public String getNamespacedId() {
        return this.namespacedId;
    }

    public Position getPosition() {
        return this.position;
    }

    @Override
    public Object asJson() {
        return Map.of(
                "namespacedId", this.namespacedId,
                "position", this.position.asJson()
        );
    }
}