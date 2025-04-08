package com.mills.leaderboards;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class KillTracker {

    private static final long COOLDOWN = 5 * 60 * 1000L;
    private static final Map<UUID, LinkedHashMap<UUID, Long>> killHistory = new HashMap<>();
    private static final Map<UUID, Integer> killStreaks = new HashMap<>();
    private static final Map<UUID, Integer> highestStreaks = new HashMap<>();

    public static boolean isKillValid(Player killer, Player victim) {
        if (killer == null || victim == null || killer.equals(victim)) return false;

        if (isUnarmored(victim)) return false;

        cleanupOldKills(killer.getUniqueId());
        Long lastKilled = killHistory
                .getOrDefault(killer.getUniqueId(), new LinkedHashMap<>())
                .get(victim.getUniqueId());

        long now = System.currentTimeMillis();
        return lastKilled == null || now - lastKilled > COOLDOWN;
    }

    public static void handleKill(Player killer, Player victim) {
        UUID victimId = victim.getUniqueId();
        killStreaks.put(victimId, 0);

        if (killer == null || killer.equals(victim)) return;

        UUID killerId = killer.getUniqueId();

        if (isKillValid(killer, victim)) {
            int streak = killStreaks.getOrDefault(killerId, 0) + 1;
            killStreaks.put(killerId, streak);

            int highest = highestStreaks.getOrDefault(killerId, 0);
            if (streak > highest) {
                highestStreaks.put(killerId, streak);
            }

            killHistory
                    .computeIfAbsent(killerId, k -> new LinkedHashMap<>())
                    .put(victimId, System.currentTimeMillis());
        } else {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                killer.setStatistic(Statistic.PLAYER_KILLS, Math.max(0, killer.getStatistic(Statistic.PLAYER_KILLS) - 1));
                victim.setStatistic(Statistic.DEATHS, Math.max(0, victim.getStatistic(Statistic.DEATHS) - 1));
            }, 1L);
        }
    }

    public static int getKillStreak(UUID playerId) {
        return killStreaks.getOrDefault(playerId, 0);
    }

    public static int getHighestKillStreak(UUID playerId) {
        return highestStreaks.getOrDefault(playerId, 0);
    }

    private static boolean isUnarmored(Player player) {
        ItemStack[] armor = player.getInventory().getArmorContents();
        for (ItemStack item : armor) {
            if (item != null) return false;
        }
        return true;
    }

    private static void cleanupOldKills(UUID killerId) {
        LinkedHashMap<UUID, Long> map = killHistory.get(killerId);
        if (map == null) return;

        long now = System.currentTimeMillis();
        map.entrySet().removeIf(e -> now - e.getValue() > COOLDOWN);
    }

}
