package pl.fortx.report.text;

public class Colorize {

    public String colorize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.replace("&", "§");
    }
}
