package com.eripe14.houses.house.furniture;

import com.eripe14.database.document.Document;
import com.eripe14.houses.position.Position;

public class HouseCustomFurniture implements Document {

    private final String namespacedId;
    private final Position position;

    public HouseCustomFurniture(String namespacedId, Position position) {
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
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}