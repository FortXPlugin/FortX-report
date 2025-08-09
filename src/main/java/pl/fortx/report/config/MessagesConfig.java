package pl.fortx.report.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MessagesConfig {
    @Getter
    private final YamlDocument messages;

        // Messages configuration class that loads the messages.yml file


        public MessagesConfig(final @NotNull JavaPlugin plugin) throws IOException {
            final File configFile = new File(plugin.getDataFolder(), "messages.yml");

            final InputStream defaultConfig = plugin.getResource("messages.yml");

            this.messages = YamlDocument.create(
                    configFile,
                    defaultConfig,
                    GeneralSettings.builder().setKeyFormat(GeneralSettings.KeyFormat.OBJECT).build(),
                    LoaderSettings.DEFAULT,
                    DumperSettings.DEFAULT,
                    UpdaterSettings.DEFAULT
            );
        }


    }
