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
public class AdminChatHelper {
    private final @NotNull PluginConfig config;
    private final @NotNull MessagesConfig messages;
    private final @NotNull Text text;
    private final RedisManager redisManager;
    private final Gson gson = new Gson();

    public void sendAdminChatMessage(@NotNull Player sender, @NotNull String message) {
        if (config.getConfig().getBoolean("multiserver.enabled") && redisManager != null) {
            Map<String, String> chatData = new HashMap<>();
            chatData.put("playerName", sender.getName());
            chatData.put("message", message);
            chatData.put("server", config.getConfig().getString("server.name", Bukkit.getServer().getName()));

            String jsonMessage = gson.toJson(chatData);
            redisManager.publishAdminChat(jsonMessage);
        } else {
            broadcastLocalAdminMessage(sender.getName(), message, null);
        }
    }

    public void processAdminChatMessage(String jsonMessage) {
        try {
            Map<String, String> chatData = gson.fromJson(jsonMessage, Map.class);
            String playerName = chatData.get("playerName");
            String message = chatData.get("message");
            String server = chatData.get("server");

            broadcastLocalAdminMessage(playerName, message, server);
        } catch (Exception e) {
            Bukkit.getLogger().warning("Error processing admin chat message: " + e.getMessage());
        }
    }

    private void broadcastLocalAdminMessage(String playerName, String message, String server) {
        Bukkit.getOnlinePlayers().forEach(p -> {
            if (p.hasPermission("adminchat.see")) {
                String rawMessage = messages.getMessages().getString("adminchat.format");
                rawMessage = rawMessage.replace("{Player}", playerName)
                        .replace("{Message}", message);

                if (server != null) {
                    rawMessage += " &7[" + server + "]";
                }

                Component formattedMessage = text.toComponent(rawMessage);
                p.sendMessage(formattedMessage);
            }
        });
    }
}