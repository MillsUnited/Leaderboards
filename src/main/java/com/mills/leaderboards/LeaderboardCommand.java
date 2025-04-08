package com.mills.leaderboards;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LeaderboardCommand implements CommandExecutor {

    public String prefix = ChatColor.translateAlternateColorCodes('&', "&e&lLeaderboard &r&8» &7");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("leaderboard.use")) {
                player.sendMessage(prefix + "you don't have permission to use!");
                return true;
            }

            player.openInventory(LeaderboardHandler.leaderboardMainGUI());

        }
        return false;
    }
}
