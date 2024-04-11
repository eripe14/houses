package com.eripe14.houses.configuration.implementation;

import com.sk89q.worldguard.protection.flags.StateFlag;
import net.dzikoysk.cdn.entity.Contextual;

@Contextual
public class StateFlagConfiguration {

    private final String flagName;
    private final StateFlag.State flagState;

    public StateFlagConfiguration(String flagName, StateFlag.State flagState) {
        this.flagName = flagName;
        this.flagState = flagState;
    }

    public String getFlagName() {
        return this.flagName;
    }

    public StateFlag.State getFlagState() {
        return this.flagState;
    }

}