package com.eripe14.houses.house.renovation.request.acceptance;

import com.eripe14.houses.house.renovation.RenovationType;
import pl.craftcityrp.developerapi.data.DataBit;

import java.time.Instant;
import java.util.Map;

public class RenovationAcceptanceRequest extends DataBit {

    private final String houseId;
    private final RenovationType renovationType;
    private final Instant startMoment;
    private final Instant endMoment;

    public RenovationAcceptanceRequest(
            String houseId,
            RenovationType renovationType,
            Instant startMoment,
            Instant endMoment
    ) {
        super(null);
        this.houseId = houseId;
        this.renovationType = renovationType;
        this.startMoment = startMoment;
        this.endMoment = endMoment;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public RenovationType getRenovationType() {
        return this.renovationType;
    }

    public Instant getStartMoment() {
        return this.startMoment;
    }

    public Instant getEndMoment() {
        return this.endMoment;
    }

    @Override
    public Object asJson() {
        return Map.of(
                "houseId", this.houseId,
                "renovationType", this.renovationType.name(),
                "startMoment", this.startMoment.toString(),
                "endMoment", this.endMoment.toString()
        );
    }
}