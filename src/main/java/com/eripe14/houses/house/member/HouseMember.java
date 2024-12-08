package com.eripe14.houses.house.member;

import com.eripe14.database.document.Document;
import panda.std.Option;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class HouseMember implements Document {

    private final Instant joinedAt;
    private final String memberName;
    private final UUID memberUuid;
    private final String houseId;
    private Map<HouseMemberPermission, Boolean> permissions;
    private boolean isCoOwner;
    private Option<Instant> coOwnerAt;

    public HouseMember(String memberName, UUID memberUuid, String houseId, Map<HouseMemberPermission, Boolean> permissions, boolean isCoOwner) {
        this.joinedAt = Instant.now();
        this.memberName = memberName;
        this.memberUuid = memberUuid;
        this.houseId = houseId;
        this.permissions = permissions;
        this.isCoOwner = isCoOwner;
        this.coOwnerAt = Option.none();
    }

    public HouseMember(
            Instant joinedAt,
            String memberName,
            UUID memberUuid,
            String houseId,
            Map<HouseMemberPermission, Boolean> permissions,
            boolean isCoOwner,
            Option<Instant> coOwnerAt
    ) {
        this.joinedAt = joinedAt;
        this.memberName = memberName;
        this.memberUuid = memberUuid;
        this.houseId = houseId;
        this.permissions = permissions;
        this.isCoOwner = isCoOwner;
        this.coOwnerAt = coOwnerAt;
    }

    public Instant getJoinedAt() {
        return this.joinedAt;
    }

    public String getMemberName() {
        return this.memberName;
    }

    public UUID getMemberUuid() {
        return this.memberUuid;
    }

    public String getHouseId() {
        return this.houseId;
    }

    public Map<HouseMemberPermission, Boolean> getPermissions() {
        return this.permissions;
    }

    public boolean isCoOwner() {
        return this.isCoOwner;
    }

    public Option<Instant> getCoOwnerAt() {
        return this.coOwnerAt;
    }

    public void setCoOwner(boolean isCoOwner) {
        this.isCoOwner = isCoOwner;
    }

    public void setCoOwnerAt(Instant coOwnerAt) {
        this.coOwnerAt = Option.of(coOwnerAt);
    }

    public void setPermission(Map<HouseMemberPermission, Boolean> permissions) {
        this.permissions = permissions;
    }

    @Override
    public Class<? extends Document> getType() {
        return this.getClass();
    }
}