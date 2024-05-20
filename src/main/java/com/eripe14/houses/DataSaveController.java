package com.eripe14.houses;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import pl.craftcityrp.developerapi.data.DataManager;

import java.util.logging.Level;

public class DataSaveController implements Listener {

    private final DataManager dataManager;

    public DataSaveController(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        this.dataManager.updateAllToBackend();
        event.getPlugin().getLogger().log(Level.INFO, "Data saved to backend");
    }

}