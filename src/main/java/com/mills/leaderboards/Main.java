package com.mills.leaderboards;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private static Main instance;

    @Override
    public void onEnable() {

        instance = this;

        LeaderboardHandler.initializeCacheUpdater();

        Bukkit.getPluginManager().registerEvents(new LeaderboardHandler(), this);
        Bukkit.getPluginManager().registerEvents(new KillListener(), this);

        getCommand("leaderboard").setExecutor(new LeaderboardCommand());
        getCommand("playtime").setExecutor(new PlaytimeCommand());

    }

    public static Main getInstance() {
        return instance;
    }
}
