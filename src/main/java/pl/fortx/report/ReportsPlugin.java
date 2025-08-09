package pl.fortx.report;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.CommandExecutor;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.LegacyPaperCommandManager;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.annotation.managers.CommandManager;
import pl.fortx.report.command.user.ReportCommand;
import pl.fortx.report.config.MessagesConfig;
import pl.fortx.report.config.PluginConfig;
import pl.fortx.report.database.RedisManager;
import pl.fortx.report.helper.ReportHelper;
import pl.fortx.report.text.Text;

import java.io.File;


public final class ReportsPlugin extends JavaPlugin {
    private PluginConfig pluginConfig;
    private MessagesConfig messagesConfig;
    private RedisManager redisManager;
    private ReportHelper reportHelper;
    private Text text;

    @Override
    public void onEnable() {
        initializeConfig();
        initializeManagers();
        registerCommands();
    }

    public final void initializeConfig() {
        try {
            pluginConfig = new PluginConfig(this);
            getLogger().info("Configuration loaded successfully.");

            messagesConfig = new MessagesConfig(this);
            getLogger().info("Messages loaded successfully.");

            text = new Text(); // Zakładam, że istnieje taka klasa

        } catch (Exception e) {
            getLogger().severe("An error occurred during plugin initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public final void initializeManagers() {
        try {
            if (pluginConfig.getConfig().getBoolean("multiserver.enabled")) {
                redisManager = new RedisManager(pluginConfig, this);
                redisManager.initialize();
                getLogger().info("Redis manager initialized successfully.");

                reportHelper = new ReportHelper(pluginConfig, messagesConfig, text, redisManager);
                redisManager.startListening(reportHelper);
                getLogger().info("Redis listening for reports on channel: " +
                        pluginConfig.getConfig().getString("redis.channels.report"));
            } else {
                reportHelper = new ReportHelper(pluginConfig, messagesConfig, text, null);
                getLogger().info("Running in single server mode.");
            }
        } catch (Exception e) {
            getLogger().severe("An error occurred during managers initialization: " + e.getMessage());
            e.printStackTrace();
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
        annotationParser.parse(new ReportCommand(pluginConfig, messagesConfig, text, reportHelper));
    }

    @Override
    public void onDisable() {
        if (redisManager != null && pluginConfig.getConfig().getBoolean("multiserver.enabled")) {
            redisManager.shutdown();
            getLogger().info("Redis connection closed.");
        }
    }
}