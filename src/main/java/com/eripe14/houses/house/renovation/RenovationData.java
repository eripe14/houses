package com.eripe14.houses.house.renovation;

import com.eripe14.houses.position.Position;
import com.eripe14.houses.position.PositionAdapter;
import org.bukkit.Location;
import pl.craftcityrp.developerapi.data.DataBit;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RenovationData extends DataBit {

    private final String houseId;
    private final Set<Position> placedBlocks;

    public RenovationData(String houseId) {
        super(null);
        this.houseId = houseId;
        this.placedBlocks = new HashSet<>();
    }

    public RenovationData(String houseId, Set<Position> placedBlocks) {
        super(null);
        this.houseId = houseId;
        this.placedBlocks = placedBlocks;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public void addLocation(Location location) {
        this.placedBlocks.add(PositionAdapter.convert(location));
    }

    public void removeLocation(Location location) {
        this.placedBlocks.remove(PositionAdapter.convert(location));
    }

    public boolean containsLocation(Location location) {
        return this.placedBlocks.contains(PositionAdapter.convert(location));
    }

    @Override
    public Object asJson() {
        return Map.of(
                "houseId", this.houseId,
                "placedBlocks", this.placedBlocks
        );
    }
}