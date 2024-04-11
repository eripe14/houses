package com.eripe14.houses.house.invite;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class InviteExpireEvent extends Event implements Cancellable {

    private final static HandlerList HANDLER_LIST = new HandlerList();

    private final UUID sender;
    private final Invite invite;

    private boolean cancelled;

    public InviteExpireEvent(UUID sender, Invite invite) {
        super(true);
        this.sender = sender;
        this.invite = invite;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public UUID getSender() {
        return this.sender;
    }

    public Invite getInvite() {
        return this.invite;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}