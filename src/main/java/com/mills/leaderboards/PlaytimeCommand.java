package com.mills.leaderboards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaytimeCommand implements CommandExecutor {

    public String prefix = ChatColor.translateAlternateColorCodes('&', "&c&lPlaytime &r&8» &7");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (!player.hasPermission("playtime.use")) {
                player.sendMessage(prefix + "you don't have permission to use this command!");
                return true;
            }

            if (args.length != 1) {
                player.sendMessage(prefix + "Usage: /playtime <player>");
                return true;
            }

            String inputName = args[0].toLowerCase();
            OfflinePlayer target = null;

            for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
                if (offlinePlayer.getName() != null && offlinePlayer.getName().equalsIgnoreCase(inputName)) {
                    target = offlinePlayer;
                    break;
                }
            }

            if (target == null || !target.hasPlayedBefore()) {
                player.sendMessage(prefix + "invalid player.");
                return true;
            }

            int ticks = target.getStatistic(Statistic.PLAY_ONE_MINUTE);
            long milliseconds = ticks * 50L;

            String readableTime = PlaytimeUtil.formatDuration(milliseconds);
            player.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', "Playtime for &6" + target.getName() + " &7is &6" + readableTime + "&7!"));

        }

        return false;
    }
}
