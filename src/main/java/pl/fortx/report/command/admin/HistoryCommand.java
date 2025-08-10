package pl.fortx.report.command.admin;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.config.MessagesConfig;
import pl.fortx.report.config.PluginConfig;


@RequiredArgsConstructor
public class HistoryCommand {
    private final @NotNull PluginConfig pluginConfig;
    private final @NotNull MessagesConfig messages;

    @Permission("report.history")
    @Command("history|h|reports <player>")
    public void run(@NotNull final Player player, @Argument(value = "player", description = "The player whose report history you want to view") final @NotNull Player target) {
        if (!target.isOnline()) {
            String rawMessage = messages.getMessages().getString("history.offline");
            player.sendMessage(rawMessage);
            return;
        }
    }

}
