package com.eripe14.houses.house.invite;

import com.eripe14.houses.EventCaller;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.UUID;

public class HouseInviteService {

    private final EventCaller eventCaller;
    private final Cache<UUID, Invite> invites;

    public HouseInviteService(EventCaller eventCaller, PluginConfiguration pluginConfiguration) {
        this.eventCaller = eventCaller;
        this.invites = CacheBuilder
                .newBuilder()
                .expireAfterWrite(pluginConfiguration.timeToConfirmHouseInvite)
                .build();
    }

    public void addInvite(UUID player, Invite invite) {
        this.invites.put(player, invite);
    }

    public void removeInvite(UUID player) {
        this.invites.invalidate(player);
    }

    public Invite getInvite(UUID player) {
        return this.invites.getIfPresent(player);
    }

    public boolean hasSentInvite(UUID player) {
        return this.invites.getIfPresent(player) != null;
    }

}