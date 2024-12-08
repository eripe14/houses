package com.eripe14.houses;

import com.eripe14.database.Database;
import com.eripe14.database.data.DataService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class DataSaveController implements Listener {

    private final Plugin plugin;
    private final DataService dataService;
    private final Database database;

    public DataSaveController(Plugin plugin, DataService dataService, Database database) {
        this.plugin = plugin;
        this.dataService = dataService;
        this.database = database;
    }

    @EventHandler
    public void onDisable(PluginDisableEvent event) {
        if (!event.getPlugin().getName().equalsIgnoreCase(this.plugin.getName())) {
            return;
        }

        this.dataService.updateDatabase(this.database, "/");
        event.getPlugin().getLogger().log(Level.INFO, "Data saved to backend");
    }

}