package com.eripe14.houses.house.invite;

import com.eripe14.houses.EventCaller;
import com.eripe14.houses.configuration.implementation.PluginConfiguration;
import net.jodah.expiringmap.ExpiringMap;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HouseInviteService {

    private final EventCaller eventCaller;
    private final Map<UUID, Invite> invites;

    public HouseInviteService(EventCaller eventCaller, PluginConfiguration pluginConfiguration) {
        this.eventCaller = eventCaller;
        this.invites = ExpiringMap.builder()
                .expiration(pluginConfiguration.timeToConfirmHouseInvite.getSeconds(), TimeUnit.SECONDS)
                .expirationListener((player, invite) -> {
                    this.eventCaller.callEvent(new InviteExpireEvent((UUID) player, (Invite) invite));
                })
                .build();
    }

    public void addInvite(UUID player, Invite invite) {
        this.invites.put(player, invite);
    }

    public void removeInvite(UUID player) {
        this.invites.remove(player);
    }

    public Invite getInvite(UUID player) {
        return this.invites.get(player);
    }

    public boolean hasSentInvite(UUID player) {
        return this.invites.get(player) != null;
    }

}