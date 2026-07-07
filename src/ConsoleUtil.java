import java.io.IOException;

// Shared console rendering helpers: screen clearing, width detection, and text alignment.
public class ConsoleUtil {

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                // Executes the 'cls' command inside the Windows command prompt
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                // Executes the 'clear' command on UNIX-based systems (Linux/macOS)
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            e.printStackTrace();
        }
    }

    // Detects the real terminal width so centering matches the actual window
    // instead of a guessed fixed size. Falls back to 80 (the standard default) if it can't tell.
    public static int getConsoleWidth() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                Process p = new ProcessBuilder("cmd", "/c", "mode con").start();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(p.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim();
                        if (line.toLowerCase().startsWith("columns:")) {
                            return Integer.parseInt(line.split(":")[1].trim());
                        }
                    }
                }
            } else {
                Process p = new ProcessBuilder("sh", "-c", "tput cols 2>/dev/tty").start();
                try (java.io.BufferedReader reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(p.getInputStream()))) {
                    String line = reader.readLine();
                    if (line != null && !line.isBlank()) {
                        return Integer.parseInt(line.trim());
                    }
                }
            }
        } catch (Exception e) {
            // fall through to default below
        }
        return 80;
    }

    // Strips ANSI color codes so bracket text lines up cleanly
    public static String stripColor(String text) {
        return text.replaceAll("\\[[;\\d]*m", "");
    }

    // Greys out and dims an eliminated team label, replacing whatever color it already had.
    public static String strikethrough(String text) {
        String plain = stripColor(text);
        return "[2m[90m" + plain + "[0m";
    }

    // Centering the text
    public static String left(String word, int max) {
        // [ AI ] this will replace the invisible ANSI character with none to fix the padding issue
        int visible = word.replaceAll("\\[[;\\d]*m", "").length();
        int padding = Math.max(0, max - visible);
        return word + " ".repeat(padding);
    }

    // centering text
    public static String center(String word, String symbol, int max) {
        int total_padding = max - word.length();
        int left_padding = total_padding/2;
        int right_padding = total_padding - left_padding;
        return symbol.repeat(left_padding) + word + symbol.repeat(right_padding);
    }

    //using method overloading here for centering
    public static String center(String word, int max) {
        int total_padding = max - word.length();
        int left_padding = total_padding/2;
        int right_padding = total_padding - left_padding;
        return " ".repeat(left_padding) + word + " ".repeat(right_padding);
    }

    // Centers a line (which may carry ANSI color codes) using its visible length
    public static String centerVisible(String word, int width) {
        int visible = stripColor(word).length();
        int leftPad = Math.max(0, (width - visible) / 2);
        int rightPad = Math.max(0, width - leftPad - visible);
        return " ".repeat(leftPad) + word + " ".repeat(rightPad);
    }
}
