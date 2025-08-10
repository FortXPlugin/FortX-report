package pl.fortx.report.text;

import net.kyori.adventure.text.Component;

public class TextHelper {


    // Should definitely not use colorize lol
    @Deprecated
    public static String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.replace("&", "§");
    }


    public static Component toComponent(String text) {
        return Component.text(colorize(text));
    }
}
