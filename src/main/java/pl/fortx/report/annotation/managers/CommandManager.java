package pl.fortx.report.annotation.managers;

import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import pl.fortx.report.ReportsPlugin;
import pl.fortx.report.annotation.impl.RegisteredCommand;

import java.util.Set;

public class CommandManager {
    private final ReportsPlugin plugin;

    public CommandManager(final @NotNull ReportsPlugin plugin, final @NotNull AnnotationParser<CommandSender> annotationParser) {
        this.plugin = plugin;

        register(annotationParser);
    }

    private void register(final @NotNull AnnotationParser<CommandSender> annotationParser) {
        for (final String cmdPackage : CommandPaths.commands) {
            registerFromPackages(CommandPaths.MAIN_PATH + "." + cmdPackage, annotationParser);
        }
    }

    private void registerFromPackages(final @NotNull String commandPackage, final @NotNull AnnotationParser<CommandSender> annotationParser) {
        final Reflections reflections = new Reflections(commandPackage, Scanners.TypesAnnotated);
        final Set<Class<?>> commands = reflections.getTypesAnnotatedWith(RegisteredCommand.class);

        for (final Class<?> cmdClass : commands) {
            try {
                final Object command = create(cmdClass, plugin);

                annotationParser.parse(command);
            } catch (final Exception e) {
                plugin.getLogger().severe(e.getMessage());
            }
        }
    }

    private Object create(final @NotNull Class<?> clazz, final @NotNull ReportsPlugin plugin) throws Exception {
        try {
            return clazz.getConstructor(
                    ReportsPlugin.class
            ).newInstance(plugin);
        } catch (final NoSuchMethodException exception) {
            return clazz.getConstructor().newInstance();
        }
    }
}