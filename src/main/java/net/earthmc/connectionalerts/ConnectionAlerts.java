package net.earthmc.connectionalerts;

import net.earthmc.connectionalerts.command.ConnectionAlertsCommand;
import net.earthmc.connectionalerts.listener.PlayerConnectionListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class ConnectionAlerts extends JavaPlugin {

    @Override
    public void onEnable() {
        registerCommands();
        registerListeners();
    }

    private void registerCommands() {
        getCommand("connectionalerts").setExecutor(new ConnectionAlertsCommand());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(), this);
    }
}
