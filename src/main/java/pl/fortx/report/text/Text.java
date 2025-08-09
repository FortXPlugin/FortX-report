package pl.fortx.report.text;

import net.kyori.adventure.text.Component;

public class Text {


    // Should definitely not use colorize lol
    @Deprecated
    public String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.replace("&", "§");
    }


    public Component toComponent(String text) {
        return Component.text(colorize(text));
    }
}
