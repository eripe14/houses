package com.eripe14.houses.hook.implementation;

import com.eripe14.houses.hook.Hook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.concurrent.CompletableFuture;

public class VaultHook implements Hook {

    private final Server server;
    private Economy economy;

    public VaultHook(Server server) {
        this.server = server;
    }

    @Override
    public CompletableFuture<Boolean> initialize() {
        return CompletableFuture.supplyAsync(() -> {
            RegisteredServiceProvider<Economy> economyProvider = this.server.getServicesManager().getRegistration(Economy.class);

            if (economyProvider == null) {
                throw new IllegalStateException("Vault founded, but you don't have a plugin that supports economy");
            }

            return true;
        });
    }

    @Override
    public String pluginName() {
        return "Vault";
    }

    public Economy getEconomy() {
        return this.economy;
    }

}