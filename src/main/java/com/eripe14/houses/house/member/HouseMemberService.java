package com.eripe14.houses.house.member;

import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.eripe14.houses.house.House;
import com.eripe14.houses.house.HouseService;
import com.eripe14.houses.house.owner.Owner;
import panda.std.Option;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HouseMemberService {

    private final HouseService houseService;
    private final PluginConfiguration pluginConfiguration;

    public HouseMemberService(HouseService houseService, PluginConfiguration pluginConfiguration) {
        this.houseService = houseService;
        this.pluginConfiguration = pluginConfiguration;
    }

    public void changePermissionStatus(House house, HouseMember member, HouseMemberPermission permission) {
        Map<HouseMemberPermission, Boolean> permissions = member.getPermissions();
        Map<HouseMemberPermission, Boolean> newPermissions = new HashMap<>(permissions);

        newPermissions.put(permission, !member.getPermissions().get(permission));
        member.setPermission(newPermissions);

        this.addHouseMember(house, member);
    }

    public void addHouseMember(House house, HouseMember member) {
        house.getMembers().put(member.getMemberUuid(), member);
        this.houseService.addHouse(house);
    }

    public void addDefaultMember(House house, HouseMember member) {
        member.setPermission(this.pluginConfiguration.defaultHouseMemberPermission);
        house.getMembers().put(member.getMemberUuid(), member);
        this.houseService.addHouse(house);
    }

    public void removeHouseMember(House house, UUID uuid) {
        house.getMembers().remove(uuid);
        this.houseService.addHouse(house);
    }

    public void changeOwner(House house, HouseMember member) {
        Owner owner = new Owner(member.getMemberUuid(), member.getMemberName());

        house.setOwner(owner);
        house.getMembers().remove(member.getMemberUuid());
        this.houseService.addHouse(house);
    }

    public boolean isCoOwner(House house, UUID uuid) {
        Option<HouseMember> houseMemberOption = Option.of(house.getMembers().get(uuid));

        if (houseMemberOption.isEmpty()) {
            return false;
        }

        return houseMemberOption.get().isCoOwner();
    }

    public void addCoOwner(House house, HouseMember member) {
        member.setCoOwner(true);
        member.getPermissions().forEach((permission, value) -> member.getPermissions().put(permission, true));

        house.getMembers().put(member.getMemberUuid(), member);
        this.houseService.addHouse(house);
    }

    public void removeCoOwner(House house, HouseMember member) {
        Map<HouseMemberPermission, Boolean> defaultHouseMemberPermission = this.pluginConfiguration.defaultHouseMemberPermission;

        member.setCoOwner(false);
        member.getPermissions().clear();
        member.getPermissions().putAll(defaultHouseMemberPermission);

        house.getMembers().put(member.getMemberUuid(), member);
        this.houseService.addHouse(house);
    }

    public boolean hasPermission(HouseMember member, HouseMemberPermission permission) {
        return member.getPermissions().getOrDefault(permission, false);
    }

    public boolean isHouseMember(House house, UUID uuid) {
        return house.getMembers().containsKey(uuid);
    }

    public Option<HouseMember> getHouseMember(House house, UUID uuid) {
        return Option.of(house.getMembers().get(uuid));
    }

}