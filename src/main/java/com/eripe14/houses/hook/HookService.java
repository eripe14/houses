package com.eripe14.houses.hook;

import org.bukkit.Server;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;

public class HookService {

    private final Server server;
    private final Logger logger;

    public HookService(Server server, Logger logger) {
        this.server = server;
        this.logger = logger;
    }

    public void initialize(Hook hook) {
        PluginManager pluginManager = this.server.getPluginManager();
        String hookName = hook.pluginName();

        if (pluginManager.isPluginEnabled(hookName)) {
            hook.initialize();

            this.logger.info("Hooked into " + hookName);
        }
    }

}