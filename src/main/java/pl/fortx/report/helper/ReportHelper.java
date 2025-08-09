package pl.fortx.report.helper;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.config.MessagesConfig;
import pl.fortx.report.config.PluginConfig;
import pl.fortx.report.database.RedisManager;
import pl.fortx.report.text.Text;

import java.util.HashMap;
import java.util.Map;


@RequiredArgsConstructor
public class ReportHelper {
    private final @NotNull PluginConfig config;
    private final @NotNull MessagesConfig messages;
    private final @NotNull Text text;
    private final @NotNull RedisManager redisManager;
    private final Gson gson = new Gson();

    public final void sendReportMessage(@NotNull Player player, @NotNull Player target, @NotNull String[] reason) {
        if (config.getConfig().getBoolean("multiserver.enabled")) {
            Map<String, String> reportData = new HashMap<>();
            reportData.put("playerName", player.getName());
            reportData.put("targetName", target.getName());
            reportData.put("reason", String.join(" ", reason));
            reportData.put("server", Bukkit.getServer().getName());

            String jsonReport = gson.toJson(reportData);
            redisManager.publishReport(jsonReport);

            String rawMessage = messages.getMessages().getString("report.message");
            rawMessage = rawMessage.replace("{Player}", player.getName())
                    .replace("{Target}", target.getName())
                    .replace("{Reason}", String.join(" ", reason));
            Component message = text.toComponent(rawMessage);
            player.sendMessage(message);
        } else {
            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p.hasPermission("report.admin")) {
                    String rawMessage = messages.getMessages().getString("report.message");
                    rawMessage = rawMessage.replace("{Player}", player.getName())
                            .replace("{Target}", target.getName())
                            .replace("{Reason}", String.join(" ", reason));
                    Component message = text.toComponent(rawMessage);
                    p.sendMessage(message);
                }
            });
        }
    }

    public void processRedisReport(String jsonReport) {
        try {
            Map<String, String> reportData = gson.fromJson(jsonReport, Map.class);
            String playerName = reportData.get("playerName");
            String targetName = reportData.get("targetName");
            String reason = reportData.get("reason");
            String server = reportData.get("server");

            Bukkit.getOnlinePlayers().forEach(p -> {
                if (p.hasPermission("report.admin")) {
                    String rawMessage = messages.getMessages().getString("report.message");
                    rawMessage = rawMessage.replace("{Player}", playerName)
                            .replace("{Target}", targetName)
                            .replace("{Reason}", reason);

                    rawMessage += " &7[Serwer: " + server + "]";

                    Component message = text.toComponent(rawMessage);
                    p.sendMessage(message);
                }
            });
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error processing Redis report: " + e.getMessage());
        }
    }
}