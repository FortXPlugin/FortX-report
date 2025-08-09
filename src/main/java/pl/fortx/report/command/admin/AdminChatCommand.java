package pl.fortx.report.command.admin;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.helper.AdminChatHelper;

@RequiredArgsConstructor
public class AdminChatCommand {
    private final @NotNull AdminChatHelper adminChatHelper;

    @Permission("adminchat.use")
    @Command("adminchat <message>")
    public void run(@NotNull Player player, @Argument("message") @NotNull String[] messageArray) {
        String message = String.join(" ", messageArray);
        adminChatHelper.sendAdminChatMessage(player, message);
    }

    @Permission("adminchat.use")
    @Command("ac <message>")
    public void runAlias(@NotNull Player player, @Argument("message") @NotNull String[] messageArray) {
        String message = String.join(" ", messageArray);
        adminChatHelper.sendAdminChatMessage(player, message);
    }
}