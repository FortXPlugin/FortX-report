package pl.fortx.report.command.admin;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.service.AdminChatService;

@RequiredArgsConstructor
public class AdminChatCommand {
    private final @NotNull AdminChatService adminChatService;


    // Adminchat command for admins to communicate with each other
    // This command allows admins to send messages to each other in a separate chat channel
    @Permission("adminchat.use")
    @Command("adminchat|ac|achat <message>")
    public void run(@NotNull Player player, @Argument("message") @NotNull String[] messageArray) {
        String message = String.join(" ", messageArray);
        adminChatService.sendAdminChatMessage(player, message);
    }

    @Permission("adminchat.use")
    @Command("ac <message>")
    public void runAlias(@NotNull Player player, @Argument("message") @NotNull String[] messageArray) {
        String message = String.join(" ", messageArray);
        adminChatService.sendAdminChatMessage(player, message);
    }
}