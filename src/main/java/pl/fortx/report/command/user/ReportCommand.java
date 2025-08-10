package pl.fortx.report.command.user;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.config.MessagesConfig;
import pl.fortx.report.service.ReportService;
import pl.fortx.report.service.ReportLimiter;
import pl.fortx.report.text.TextHelper;

@RequiredArgsConstructor
public class ReportCommand {
    private final @NotNull MessagesConfig messages;
    private final @NotNull TextHelper textHelper;
    private final ReportService reportService;
    private final @NotNull ReportLimiter limiter;


    // Report command for players to report other players
    // This command allows players to report other players for inappropriate behavior or rule violations
    @Permission("report.use")
    @Command("report|zglos <player> <reason>")
    public void run(@NotNull Player player, @Argument(value = "player", description = "The player u want to report") final @NotNull Player target , @Argument(value = "reason", description = "The report reason") @NotNull String[] reason) {
        if (!limiter.canReport(player)) {
            String rawMessage = messages.getMessages().getString("report.cooldown");
            Component message = textHelper.toComponent(rawMessage);
            player.sendMessage(message);
            return;
        }
        if (player == target) {
            String rawMessage = messages.getMessages().getString("report.self");
            Component message = textHelper.toComponent(rawMessage);
            player.sendMessage(message);
            return;
        }
        if (!target.isOnline()) {
            String rawMessage = messages.getMessages().getString("report.offline");
            Component message = textHelper.toComponent(rawMessage);
            player.sendMessage(message);
            return;
        }
        if (target.hasPermission("report.bypass")) {
            String rawMessage = messages.getMessages().getString("report.bypass");
            Component message = textHelper.toComponent(rawMessage);
            player.sendMessage(message);
            return;
        }
        {
            reportService.sendReportMessage(player, target, reason);
            return;
        }
    }
}
