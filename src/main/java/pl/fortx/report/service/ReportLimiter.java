package pl.fortx.report.service;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.config.PluginConfig;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class ReportLimiter {
    private final @NotNull PluginConfig config;
    @Getter
    Map<Player, Time> lastReport = new HashMap<>();


    public boolean canReport(Player player) {
        if (lastReport.containsKey(player)) {
            Time lastTime = lastReport.get(player);
            Time currentTime = new Time(System.currentTimeMillis());
            long difference = currentTime.getTime() - lastTime.getTime();

            if (difference < config.getConfig().getLong("report.cooldown") * 1000) {
                return false; // Player cannot report yet
            } else {
                lastReport.put(player, currentTime); // Update the last report time
                return true; // Player can report
            }
        } else {
            lastReport.put(player, new Time(System.currentTimeMillis())); // First report, set current time
            return true; // Player can report
        }
    }

}
