package pl.fortx.report;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import pl.fortx.report.annotation.managers.CommandManager;
import pl.fortx.report.command.admin.AdminChatCommand;
import pl.fortx.report.command.user.ReportCommand;
import pl.fortx.report.config.MessagesConfig;
import pl.fortx.report.config.PluginConfig;
import pl.fortx.report.database.RedisManager;
import pl.fortx.report.helper.AdminChatHelper;
import pl.fortx.report.helper.ReportHelper;
import pl.fortx.report.helper.ReportLimiter;
import pl.fortx.report.text.Text;

public final class ReportsPlugin extends JavaPlugin {
    private PluginConfig pluginConfig;
    private MessagesConfig messagesConfig;
    private RedisManager redisManager;
    private ReportHelper reportHelper;
    private Text text;
    private AdminChatHelper adminChatHelper;

    @Override
    public void onEnable() {
        // Inicjalizacja Text na początku
        text = new Text();

        initializeConfig();
        initializeManagers();
        registerCommands();
    }

    public void initializeConfig() {
        try {
            pluginConfig = new PluginConfig(this);
            getLogger().info("Configuration loaded successfully.");

            messagesConfig = new MessagesConfig(this);
            getLogger().info("Messages loaded successfully.");

        } catch (Exception e) {
            getLogger().severe("An error occurred during plugin initialization: " + e.getMessage());
        }
    }

    public void initializeManagers() {
        try {
            if (pluginConfig.getConfig().getBoolean("multiserver.enabled")) {
                redisManager = new RedisManager(pluginConfig, this);
                redisManager.initialize();
                getLogger().info("Redis manager initialized successfully.");

                reportHelper = new ReportHelper(pluginConfig, messagesConfig, text, redisManager);
                adminChatHelper = new AdminChatHelper(pluginConfig, messagesConfig, text, redisManager);
                redisManager.startListening(reportHelper, adminChatHelper);
                getLogger().info("Redis listening for reports and admin chat messages");
            } else {
                reportHelper = new ReportHelper(pluginConfig, messagesConfig, text, null);
                adminChatHelper = new AdminChatHelper(pluginConfig, messagesConfig, text, null);
                getLogger().info("Running in single server mode.");
            }

        } catch (Exception e) {
            getLogger().severe("An error occurred during managers initialization: " + e.getMessage());
        }
    }

    private void registerCommands() {
        final LegacyPaperCommandManager<CommandSender> manager = new LegacyPaperCommandManager<>(
                this,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );

        manager.captionRegistry().registerProvider(MinecraftHelp.defaultCaptionsProvider());

        final var annotationParser = new AnnotationParser<>(manager, CommandSender.class);

        new CommandManager(this, annotationParser);
        ReportLimiter limiter = new ReportLimiter(pluginConfig);
        annotationParser.parse(new ReportCommand(messagesConfig, text, reportHelper, limiter));
        annotationParser.parse(new AdminChatCommand(adminChatHelper));
    }

    @Override
    public void onDisable() {
        if (redisManager != null && pluginConfig.getConfig().getBoolean("multiserver.enabled")) {
            redisManager.shutdown();
            getLogger().info("Redis connection closed.");
        }
    }
}