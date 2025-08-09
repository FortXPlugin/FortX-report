package pl.fortx.report.command.user;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.config.MessagesConfig;
import pl.fortx.report.helper.ReportHelper;
import pl.fortx.report.text.Text;

@RequiredArgsConstructor
public class ReportCommand {
    private final @NotNull MessagesConfig messages;
    private final @NotNull Text text;
    private final ReportHelper reportHelper;


    // Report command for players to report other players
    // This command allows players to report other players for inappropriate behavior or rule violations
    @Permission("report.use")
    @Command("report|zglos <player> <reason>")
    public void run(@NotNull Player player, @Argument(value = "player", description = "The player u want to report") final @NotNull Player target , @Argument(value = "reason", description = "The report reason") @NotNull String[] reason) {
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
