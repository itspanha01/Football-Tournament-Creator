// ANSI color helper for console team labels.
public class ColorUtil {

    public static String getColor(String text, String color) {

        // Color codes in ANSI
        String RED = "[31m";
        String GREEN = "[32m";
        String YELLOW = "[33m";
        String BLUE = "[94m"; // bright blue
        String PURPLE = "[35m";
        String CYAN = "[36m";
        String RESET = "[0m";

        //Removes initial colors
        text = text.replaceAll("\\[[;\\d]*m", "");

        // adds color
        switch (color) {
            case "RED" -> text = RED + text + RESET;
            case "GREEN" -> text = GREEN + text + RESET;
            case "YELLOW" -> text = YELLOW + text + RESET;
            case "BLUE" -> text = BLUE + text + RESET;
            case "PURPLE" -> text = PURPLE + text + RESET;
            case "CYAN" -> text = CYAN + text + RESET;
        }

        return text; //RESETS to switch colors again
    }
}
