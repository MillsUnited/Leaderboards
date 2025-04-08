package com.mills.leaderboards;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LeaderboardHandler implements Listener {

    private String statName;
    private static final Map<String, List<Map.Entry<OfflinePlayer, Double>>> cachedLeaderboards = new ConcurrentHashMap<>();

    public static void initializeCacheUpdater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<String> trackedStats = Arrays.asList("Damage Dealt", "Crafted Items", "Blocks Mined", "Mobs Killed", "Traded Villager", "Distance Walked", "Playtime", "Kill Streak", "Kill/Death");
                for (String stat : trackedStats) {
                    updateStatCache(stat);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 1200L); // 1 minute
    }

    public static void updateStatCache(String stat) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Map<OfflinePlayer, Double> statMap = new HashMap<>();
            for (OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                if (!p.hasPlayedBefore()) continue;

                double value = getStatValue(p, stat);
                statMap.put(p, value);
            }

            List<Map.Entry<OfflinePlayer, Double>> sorted = statMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(11)
                    .collect(Collectors.toList());

            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                cachedLeaderboards.put(stat.toLowerCase(), sorted);
            });
        });
    }

    public static Inventory leaderboardMainGUI(){

        Inventory inv = Bukkit.createInventory(null, 45, "Elemental SMP » Leaderboard");

        ItemStack panes = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta paneMeta = panes.getItemMeta();
        paneMeta.setDisplayName(" ");
        panes.setItemMeta(paneMeta);
        for (int i : new int[]{0,1,2,3,4,5,6,7,8,10,12,14,16,18,19,20,21,22,23,24,25,26,27,29,31,33,35,36,37,38,39,40,41,42,43,44}) {
            inv.setItem(i, panes);
        }

        ItemStack damageDealt = new ItemStack(Material.REDSTONE);
        ItemMeta damageMeta = damageDealt.getItemMeta();
        damageMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Damage Dealt");
        damageDealt.setItemMeta(damageMeta);
        inv.setItem(9, damageDealt);

        ItemStack killStreak = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta killStreakMeta = killStreak.getItemMeta();
        killStreakMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Kill Streak");
        killStreakMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        killStreak.setItemMeta(killStreakMeta);
        inv.setItem(11, killStreak);

        ItemStack kd = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta kdMeta = kd.getItemMeta();
        kdMeta.setDisplayName(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "KD");
        kdMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        kd.setItemMeta(kdMeta);
        inv.setItem(13, kd);

        ItemStack blocks = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemMeta blocksMeta = blocks.getItemMeta();
        blocksMeta.setDisplayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "Blocks Mined");
        blocksMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        blocks.setItemMeta(blocksMeta);
        inv.setItem(15, blocks);

        ItemStack modsKilled = new ItemStack(Material.ROTTEN_FLESH);
        ItemMeta mobsMeta = modsKilled.getItemMeta();
        mobsMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Mobs Killed");
        modsKilled.setItemMeta(mobsMeta);
        inv.setItem(17, modsKilled);

        ItemStack expGained = new ItemStack(Material.EMERALD);
        ItemMeta expMeta = expGained.getItemMeta();
        expMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Traded Villager");
        expGained.setItemMeta(expMeta);
        inv.setItem(28, expGained);

        ItemStack distanceTraveled = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta distanceMeta = distanceTraveled.getItemMeta();
        distanceMeta.setDisplayName(ChatColor.AQUA + ChatColor.BOLD.toString() + "Distance Traveled");
        distanceMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        distanceTraveled.setItemMeta(distanceMeta);
        inv.setItem(30, distanceTraveled);

        ItemStack itemsCrafted = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta craftedMeta = itemsCrafted.getItemMeta();
        craftedMeta.setDisplayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Items Crafted");
        itemsCrafted.setItemMeta(craftedMeta);
        inv.setItem(32, itemsCrafted);

        ItemStack playtime = new ItemStack(Material.FEATHER);
        ItemMeta playtimeMeta = playtime.getItemMeta();
        playtimeMeta.setDisplayName(ChatColor.GOLD + ChatColor.BOLD.toString() + "Playtime");
        playtime.setItemMeta(playtimeMeta);
        inv.setItem(34, playtime);

        return inv;
    }

    private Inventory statInv(String stat) {
        Inventory inv = Bukkit.createInventory(null, 45, "Elemental SMP » " + stat);

        ItemStack panes = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta paneMeta = panes.getItemMeta();
        paneMeta.setDisplayName(" ");
        panes.setItemMeta(paneMeta);
        for (int i : new int[]{1,2,3,4,5,6,7,8,9,10,11,12,14,15,16,17,18,19,20,24,25,26,27,35,36,37,38,39,40,41,42,43,44}) {
            inv.setItem(i, panes);
        }

        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "BACK");
        back.setItemMeta(backMeta);
        inv.setItem(0, back);

        List<Map.Entry<OfflinePlayer, Double>> leaderboard = cachedLeaderboards.getOrDefault(stat.toLowerCase(), new ArrayList<>());
        int[] slots = {13,21,22,23,28,29,30,31,32,33,34};

        for (int i = 0; i < leaderboard.size(); i++) {
            OfflinePlayer player = leaderboard.get(i).getKey();
            double value = leaderboard.get(i).getValue();

            ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) skull.getItemMeta();
            meta.setOwningPlayer(player);
            meta.setDisplayName(ChatColor.YELLOW + "" + (i + 1) + ". " + (player.getName() != null ? player.getName() : "Unknown"));

            List<String> lore = new ArrayList<>();
            if (stat.equalsIgnoreCase("playtime")) {
                long ticks = (long) value;
                long ms = ticks * 50L; // convert ticks to milliseconds
                String duration = PlaytimeUtil.formatDuration(ms);
                lore.add(ChatColor.GRAY + "Playtime: " + ChatColor.AQUA + duration);
            } else {
                lore.add(ChatColor.GRAY + "Stat: " + ChatColor.AQUA + value);
            }

            meta.setLore(lore);
            skull.setItemMeta(meta);
            inv.setItem(slots[i], skull);
        }

        return inv;
    }

    private static double getStatValue(OfflinePlayer player, String statName) {
        switch (statName.toLowerCase()) {
            case "damage dealt": return player.getStatistic(Statistic.DAMAGE_DEALT);
            case "crafted items": {
                int total = 0;
                for (Material mat : Material.values()) {
                    try { total += player.getStatistic(Statistic.CRAFT_ITEM, mat); } catch (IllegalArgumentException ignored) {}
                }
                return total;
            }
            case "blocks mined": {
                int total = 0;
                for (Material mat : Material.values()) {
                    if (mat.isBlock()) {
                        try { total += player.getStatistic(Statistic.MINE_BLOCK, mat); } catch (IllegalArgumentException ignored) {}
                    }
                }
                return total;
            }
            case "mobs killed": return player.getStatistic(Statistic.MOB_KILLS);
            case "traded villager": return player.getStatistic(Statistic.TRADED_WITH_VILLAGER);
            case "distance walked": return player.getStatistic(Statistic.WALK_ONE_CM);
            case "playtime": return player.getStatistic(Statistic.PLAY_ONE_MINUTE);
            case "kill streak": return KillTracker.getKillStreak(player.getUniqueId());
            case "kill/death": {
                int kills = player.getStatistic(Statistic.PLAYER_KILLS);
                int deaths = player.getStatistic(Statistic.DEATHS);
                if (deaths == 0) return kills;
                return Math.round(((double) kills / deaths) * 100.0) / 100.0;
            }
            default: return 0;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player) {
            Player player = (Player) e.getWhoClicked();
            InventoryView view = e.getView();
            String title = view.getTitle();
            int clickedSlot = e.getRawSlot();

            if (title.equalsIgnoreCase("Elemental SMP » Leaderboard")) {
                switch (clickedSlot) {
                    case 9: statName = "Damage Dealt"; break;
                    case 11: statName = "Kill Streak"; break;
                    case 13: statName = "Kill/Death"; break;
                    case 15: statName = "Blocks Mined"; break;
                    case 17: statName = "Mobs Killed"; break;
                    case 28: statName = "Traded Villager"; break;
                    case 30: statName = "Distance Walked"; break;
                    case 32: statName = "Crafted Items"; break;
                    case 34: statName = "Playtime"; break;
                    default: e.setCancelled(true); return;
                }
                player.openInventory(statInv(statName));
                return;
            }

            if (title.equalsIgnoreCase("Elemental SMP » " + statName)) {
                if (clickedSlot == 0) {
                    player.openInventory(leaderboardMainGUI());
                    return;
                }
                if (e.isShiftClick()) {
                    e.setCancelled(true);
                    return;
                }
                e.setCancelled(true);

            }
        }
    }
}