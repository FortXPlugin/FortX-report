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
import pl.fortx.report.service.AdminChatService;
import pl.fortx.report.service.ReportService;
import pl.fortx.report.service.ReportLimiter;
import pl.fortx.report.text.TextHelper;

public final class ReportPlugin extends JavaPlugin {
    private PluginConfig pluginConfig;
    private MessagesConfig messagesConfig;
    private RedisManager redisManager;
    private ReportService reportService;
    private TextHelper textHelper;
    private AdminChatService adminChatService;

    @Override
    public void onEnable() {
        textHelper = new TextHelper();

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

                reportService = new ReportService(pluginConfig, messagesConfig, textHelper, redisManager);
                adminChatService = new AdminChatService(pluginConfig, messagesConfig, textHelper, redisManager);
                redisManager.startListening(reportService, adminChatService);
                getLogger().info("Redis listening for reports and admin chat messages");
            } else {
                reportService = new ReportService(pluginConfig, messagesConfig, textHelper, null);
                adminChatService = new AdminChatService(pluginConfig, messagesConfig, textHelper, null);
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
        annotationParser.parse(new ReportCommand(messagesConfig, textHelper, reportService, limiter));
        annotationParser.parse(new AdminChatCommand(adminChatService));
    }

    @Override
    public void onDisable() {
        if (redisManager != null && pluginConfig.getConfig().getBoolean("multiserver.enabled")) {
            redisManager.shutdown();
            getLogger().info("Redis connection closed.");
        }
    }
}