package net.earthmc.connectionalerts.command;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import net.earthmc.connectionalerts.manager.ResidentMetadataManager;
import net.earthmc.connectionalerts.object.AlertLevel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class ConnectionAlertsCommand implements TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players can use this command", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Component.text("No arguments provided", NamedTextColor.RED));
            return true;
        }

        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) {
            player.sendMessage(Component.text("Command failed as your player has no associated Towny resident", NamedTextColor.RED));
            return true;
        }

        ResidentMetadataManager rmm = ResidentMetadataManager.getInstance();

        AlertLevel alertLevel;
        switch (args[0]) {
            case "none" -> alertLevel = AlertLevel.NONE;
            case "friends" -> {
                boolean shouldAlertForFriends = rmm.getShouldAlertForFriends(resident);
                rmm.setShouldAlertForFriends(resident, !shouldAlertForFriends);
                if (shouldAlertForFriends) {
                    player.sendMessage(Component.text("You will no longer be alerted when a friend connects or disconnects", NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("You will now be alerted when a friend connects or disconnects", NamedTextColor.GREEN));
                }

                return true;
            }
            case "party" -> {
                boolean shouldAlertForParty = rmm.getShouldAlertForParty(resident);
                rmm.setShouldAlertForParty(resident, !shouldAlertForParty);
                if (shouldAlertForParty) {
                    player.sendMessage(Component.text("You will no longer be alerted when a party member connects or disconnects", NamedTextColor.GREEN));
                } else {
                    player.sendMessage(Component.text("You will now be alerted when a party member connects or disconnects", NamedTextColor.GREEN));
                }

                return true;
            }
            case "town" -> alertLevel = AlertLevel.TOWN;
            case "nation" -> alertLevel = AlertLevel.NATION;
            case "all" -> alertLevel = AlertLevel.ALL;
            default -> {
                player.sendMessage(Component.text("Invalid argument", NamedTextColor.RED));
                return true;
            }
        }

        rmm.setResidentAlertLevel(resident, alertLevel);

        player.sendMessage(Component.text("Successfully changed your alert level to " + alertLevel, NamedTextColor.GREEN));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> availableArguments = List.of("none", "friends", "party", "town", "nation", "all");

        if (args.length == 1) {
            if (args[0].isEmpty()) return availableArguments;

            return availableArguments.stream()
                    .filter(string -> string.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return null;
    }
}
