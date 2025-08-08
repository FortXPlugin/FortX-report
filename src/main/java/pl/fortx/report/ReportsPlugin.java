package pl.fortx.report;

import dev.dejvokep.boostedyaml.YamlDocument;
import lombok.AllArgsConstructor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import pl.fortx.report.config.MessagesConfig;
import pl.fortx.report.config.PluginConfig;

import java.io.File;


@AllArgsConstructor
public final class ReportsPlugin extends JavaPlugin {

    @Override
    public void onEnable() {

    }

    public final void initializeConfig() {
        try {
            PluginConfig config = new PluginConfig(this);
            getLogger().info("Configuration loaded successfully.");

            MessagesConfig messages = new MessagesConfig(this);
            getLogger().info("Messages loaded successfully.");


        } catch (Exception e) {
            getLogger().severe("An error occurred during plugin initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
