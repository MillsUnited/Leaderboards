package com.mills.leaderboards;

public class PlaytimeUtil {

    public static String formatDuration(long milliseconds) {
        long seconds = milliseconds / 1000 % 60;
        long minutes = milliseconds / (1000 * 60) % 60;
        long hours = milliseconds / (1000 * 60 * 60) % 24;
        long days = milliseconds / (1000 * 60 * 60 * 24);

        StringBuilder sb = new StringBuilder();

        if (days > 0) sb.append(days).append(" day").append(days > 1 ? "s" : "").append(", ");
        if (hours > 0) sb.append(hours).append(" hour").append(hours > 1 ? "s" : "").append(", ");
        if (minutes > 0) sb.append(minutes).append(" minute").append(minutes > 1 ? "s" : "").append(", ");
        if (seconds > 0 || sb.length() == 0)
            sb.append(seconds).append(" second").append(seconds != 1 ? "s" : "");

        String result = sb.toString();
        if (result.endsWith(", ")) {
            result = result.substring(0, result.length() - 2);
        }

        return result;
    }

}
