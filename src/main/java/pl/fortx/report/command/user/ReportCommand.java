package pl.fortx.report.command.user;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.config.MessagesConfig;
import pl.fortx.report.config.PluginConfig;
import pl.fortx.report.helper.ReportHelper;
import pl.fortx.report.text.Text;

@RequiredArgsConstructor
public class ReportCommand {
    private final @NotNull PluginConfig config;
    private final @NotNull MessagesConfig messages;
    private final @NotNull Text text;
    private final ReportHelper reportHelper;

    @Permission("report.use")
    @Command("report <player> <reason>")
    public void run(@NotNull Player player, @Argument("player") final @NotNull Player target , @Argument("reason") @NotNull String[] reason) {
        if (player == target) {
            String rawMessage = messages.getMessages().getString("report.self");
            Component message = text.toComponent(rawMessage);
            player.sendMessage(message);

        } else if (!target.isOnline()) {
            String rawMessage = messages.getMessages().getString("report.offline");
            Component message = text.toComponent(rawMessage);
            player.sendMessage(message);
        } else if (target.hasPermission("report.bypass")) {
            String rawMessage = messages.getMessages().getString("report.bypass");
            Component message = text.toComponent(rawMessage);
            player.sendMessage(message);
        } else {
            reportHelper.sendReportMessage(player, target, reason);
        }
    }
}
