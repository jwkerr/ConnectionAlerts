package net.earthmc.connectionalerts.listener;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import net.earthmc.connectionalerts.manager.ResidentMetadataManager;
import net.earthmc.connectionalerts.object.AlertLevel;
import net.earthmc.connectionalerts.object.ConnectionType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    private final TownyAPI townyAPI = TownyAPI.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("connectionalerts.exempt")) return;

        Resident joiningResident = townyAPI.getResident(player);
        if (joiningResident == null) return;

        handleConnectionStateChange(joiningResident, ConnectionType.JOIN);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("connectionalerts.exempt")) return;

        Resident joiningResident = townyAPI.getResident(player);
        if (joiningResident == null) return;

        handleConnectionStateChange(joiningResident, ConnectionType.QUIT);
    }

    private void handleConnectionStateChange(Resident joiningResident, ConnectionType connectionType) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Resident playerResident = townyAPI.getResident(player);
            if (playerResident == null) continue;

            ResidentMetadataManager rmm = new ResidentMetadataManager();
            if (rmm.getShouldAlertForFriends(playerResident)) sendFriendConnectionAlert(player, joiningResident, connectionType);

            AlertLevel alertLevel = rmm.getResidentAlertLevel(playerResident);
            if (alertLevel == AlertLevel.NONE) continue;

            if (alertLevel == AlertLevel.TOWN) handleTownAlertLevel(playerResident, joiningResident, connectionType);
            if (alertLevel == AlertLevel.NATION) handleNationAlertLevel(playerResident, joiningResident, connectionType);
            if (alertLevel == AlertLevel.ALL) handleAllAlertLevel(playerResident, joiningResident, connectionType);
        }
    }

    private void handleTownAlertLevel(Resident playerResident, Resident joiningResident, ConnectionType connectionType) {
        Town town = playerResident.getTownOrNull();
        if (town == null) return;

        if (town == joiningResident.getTownOrNull()) {
            sendTownConnectionAlert(playerResident.getPlayer(), joiningResident, connectionType);
        }
    }

    private void handleNationAlertLevel(Resident playerResident, Resident joiningResident, ConnectionType connectionType) {
        Town town = playerResident.getTownOrNull();
        Nation nation = playerResident.getNationOrNull();
        if (nation == null) return;

        if (town == joiningResident.getTownOrNull()) {
            sendTownConnectionAlert(playerResident.getPlayer(), joiningResident, connectionType);
            return;
        }

        if (nation == joiningResident.getNationOrNull()) {
            sendNationConnectionAlert(playerResident.getPlayer(), joiningResident, connectionType);
        }
    }

    private void handleAllAlertLevel(Resident playerResident, Resident joiningResident, ConnectionType connectionType) {
        Town town = playerResident.getTownOrNull();
        Nation nation = playerResident.getNationOrNull();

        if (town != null) {
            if (town == joiningResident.getTownOrNull()) {
                sendTownConnectionAlert(playerResident.getPlayer(), joiningResident, connectionType);
                return;
            }

            if (nation != null && nation == joiningResident.getNationOrNull()) {
                sendNationConnectionAlert(playerResident.getPlayer(), joiningResident, connectionType);
                return;
            }
        }

        sendGenericConnectionAlert(playerResident.getPlayer(), joiningResident, connectionType);
    }

    private void sendFriendConnectionAlert(Player player, Resident joiningResident, ConnectionType connectionType) {
        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.GREEN));
    }

    private void sendTownConnectionAlert(Player player, Resident joiningResident, ConnectionType connectionType) {
        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.AQUA));
    }

    private void sendNationConnectionAlert(Player player, Resident joiningResident, ConnectionType connectionType) {
        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.YELLOW));
    }

    private void sendGenericConnectionAlert(Player player, Resident joiningResident, ConnectionType connectionType) {
        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.GRAY));
    }

    private String getConnectionAlertString(Resident joiningResident, ConnectionType connectionType) {
        String joinedOrLeft = connectionType == ConnectionType.JOIN ? "joined" : "left";
        return joiningResident.getName() + " " + joinedOrLeft + " the game";
    }
}
