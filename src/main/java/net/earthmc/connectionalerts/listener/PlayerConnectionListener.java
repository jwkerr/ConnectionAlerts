package net.earthmc.connectionalerts.listener;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.party.PartyManager;
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

@SuppressWarnings("ConstantConditions") // Here just so Towny shuts up about residents potentially being null
public class PlayerConnectionListener implements Listener {

    private final TownyAPI townyAPI = TownyAPI.getInstance();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("connectionalerts.exempt")) return;

        Resident joiningResident = townyAPI.getResident(player);
        if (joiningResident == null) return;

        handleConnectionStateChange(joiningResident, player, ConnectionType.JOIN);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("connectionalerts.exempt")) return;

        Resident joiningResident = townyAPI.getResident(player);
        if (joiningResident == null) return;

        handleConnectionStateChange(joiningResident, player, ConnectionType.QUIT);
    }

    private void handleConnectionStateChange(Resident joiningResident, Player joiningPlayer, ConnectionType connectionType) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Resident playerResident = townyAPI.getResident(player);
            if (playerResident == null) continue;

            if (ResidentMetadataManager.getShouldAlertForFriends(playerResident) && playerResident.hasFriend(joiningResident)) {
                sendFriendConnectionAlert(player, joiningResident, connectionType);
                return;
            }

            if (ResidentMetadataManager.getShouldAlertForParty(playerResident) && arePlayersInSameParty(player, joiningPlayer)) {
                sendPartyConnectionAlert(player, joiningResident, connectionType);
                return;
            }

            AlertLevel alertLevel = ResidentMetadataManager.getResidentAlertLevel(playerResident);
            if (alertLevel == AlertLevel.NONE) continue;

            if (alertLevel == AlertLevel.TOWN) handleTownAlertLevel(playerResident, joiningResident, connectionType);
            if (alertLevel == AlertLevel.NATION) handleNationAlertLevel(playerResident, joiningResident, connectionType);
            if (alertLevel == AlertLevel.ALL) handleAllAlertLevel(playerResident, joiningResident, connectionType);
        }
    }

    private boolean arePlayersInSameParty(Player playerOne, Player playerTwo) {
        PartyManager pm = mcMMO.p.getPartyManager();

        Party party = pm.getParty(playerOne);
        if (party == null) return false;

        return party.hasMember(playerTwo.getUniqueId());
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
        if (player == null) return;
        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType, null);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.GREEN));
    }

    private void sendPartyConnectionAlert(Player player, Resident joiningResident, ConnectionType connectionType) {
        if (player == null) return;
        Player residentPlayer = joiningResident.getPlayer();

        Party party = mcMMO.p.getPartyManager().getParties() // Using this horrific method because mcMMO's API doesn't work correctly if the player just joined
                .stream()
                .filter(p -> p.hasMember(joiningResident.getUUID()))
                .findFirst()
                .orElse(null);

        if (party == null) return; // This party *should* never be null given the code before this method checks they are in one, but you never know with mcMMO

        String prefix = party.getLeader().getUniqueId().equals(residentPlayer.getUniqueId()) ? "\uD83D\uDC51" : null;

        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType, prefix);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.DARK_PURPLE));
    }

    private void sendTownConnectionAlert(Player player, Resident joiningResident, ConnectionType connectionType) {
        if (player == null) return;
        String prefix = joiningResident.isMayor() ? "\uD83D\uDC51" : null;
        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType, prefix);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.AQUA));
    }

    private void sendNationConnectionAlert(Player player, Resident joiningResident, ConnectionType connectionType) {
        if (player == null) return;
        String prefix = joiningResident.isKing() ? "\uD83D\uDC51" : null;
        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType, prefix);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.YELLOW));
    }

    private void sendGenericConnectionAlert(Player player, Resident joiningResident, ConnectionType connectionType) {
        if (player == null) return;
        String connectionAlertString = getConnectionAlertString(joiningResident, connectionType, null);
        player.sendMessage(Component.text(connectionAlertString, NamedTextColor.GRAY));
    }

    private String getConnectionAlertString(Resident joiningResident, ConnectionType connectionType, String prefix) {
        String joinedOrLeft = connectionType == ConnectionType.JOIN ? "joined" : "left";
        if (prefix == null) return joiningResident.getName() + " " + joinedOrLeft + " the game";

        return prefix + " " + joiningResident.getName() + " " + joinedOrLeft + " the game";
    }
}