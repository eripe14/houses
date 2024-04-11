package com.eripe14.houses.house.member;

import java.util.Map;
import java.util.UUID;

public class HouseMember {

    private final String memberName;
    private final UUID memberUuid;
    private final String houseId;
    private Map<HouseMemberPermission, Boolean> permissions;
    private boolean isCoOwner;

    public HouseMember(String memberName, UUID memberUuid, String houseId, Map<HouseMemberPermission, Boolean> permissions, boolean isCoOwner) {
        this.memberName = memberName;
        this.memberUuid = memberUuid;
        this.houseId = houseId;
        this.permissions = permissions;
        this.isCoOwner = isCoOwner;
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

    public void setCoOwner(boolean isCoOwner) {
        this.isCoOwner = isCoOwner;
    }

    public void setPermission(Map<HouseMemberPermission, Boolean> permissions) {
        this.permissions = permissions;
    }

}