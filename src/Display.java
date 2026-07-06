import java.awt.*;
import java.util.*;
import java.io.IOException;

public class Display {
    public static Scanner scan = new Scanner(System.in); // Make the scanner available for all
    public static String[] TeamNames = new String[0];
    static int MAX_WIDTH = 32;

    // Sample 16-team list for quick testing

    public static final String[] SampleTeams16 = {
        getColor("ARG", "BLUE"),   getColor("BRA", "YELLOW"),
        getColor("ESP", "RED"),    getColor("POR", "GREEN"),
        getColor("ENG", "RED"),    getColor("FRA", "BLUE"),
        getColor("GER", "PURPLE"), getColor("ITA", "CYAN"),
        getColor("NED", "YELLOW"), getColor("CRO", "RED"),
        getColor("URU", "BLUE"),   getColor("BEL", "YELLOW"),
        getColor("JPN", "RED"),    getColor("KOR", "BLUE"),
        getColor("MEX", "GREEN"),  getColor("USA", "PURPLE")
    };

    // Loads the sample 16-team list into TeamNames
    public static void LoadSampleTeams() {
        TeamNames = SampleTeams16.clone();
        System.out.println();
        System.out.printf("+ Loaded %d sample teams for testing.%n", TeamNames.length);
        System.out.println();
    }

    // Clears the current team list after confirmation
    public static void DeleteTeams() {
        scan.nextLine(); // consume the leftovers from the menu input

        if (TeamNames.length == 0) {
            System.out.println("Empty list. No teams to delete.");
            return;
        }

        DisplayTeams();
        System.out.print("Are you sure you want to delete all teams? (Y/N): ");
        String confirm = scan.nextLine().strip().toUpperCase();

        if (confirm.equals("Y")) {
            TeamNames = new String[0];
            System.out.println();
            System.out.println("+ All teams have been deleted.");
            System.out.println();
        } else {
            System.out.println("Cancelled.");
        }
    }

    public static void bracket_all() {
        String name1 = "ESP";
        String name2 = "ARG";
        String name3 = "POR";
        String name4 = "ENG";
    }

    // Maps the number of teams still in a round to its stage name
    public static String roundName(int teamsInRound) {
        return switch (teamsInRound) {
            case 2 -> "Final";
            case 4 -> "Semi-Finals";
            case 8 -> "Quarter-Finals";
            case 16 -> "Round of 16";
            case 32 -> "Round of 32";
            default -> "Round of " + teamsInRound;
        };
    }

    // A rendered sub-bracket: its lines (root connector on top, leaves at bottom),
    // its total visible width, and the visible column of its root connector.
    private static class BracketBlock {
        String[] lines;
        int width;
        int center;
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

    // Renders the bracket tree with known winners filled in at each connector,
    // and "■" wherever a match hasn't been decided yet. Eliminated leaf teams are struck through.
    // rounds.get(0) = original leaves (n teams); rounds.get(r) = winners of round r.
    private static void printResultBracket(java.util.List<String[]> rounds, int n, int consoleWidth, java.util.Set<String> eliminated) {
        BracketBlock root = buildResultBracket(0, n, rounds, eliminated);
        int margin = Math.max(0, (consoleWidth - root.width) / 2);
        String pad = " ".repeat(margin);
        for (String line : root.lines) {
            System.out.println(pad + line);
        }
    }

    private static BracketBlock buildResultBracket(int lo, int hi, java.util.List<String[]> rounds, java.util.Set<String> eliminated) {
        int size = hi - lo;
        if (size == 1) {
            String label = rounds.get(0)[lo];
            if (eliminated.contains(label)) {
                label = strikethrough(label);
            }
            int visibleWidth = stripColor(label).length();
            BracketBlock leaf = new BracketBlock();
            leaf.lines = new String[] { label };
            leaf.width = visibleWidth;
            leaf.center = visibleWidth / 2;
            return leaf;
        }

        int mid = lo + size / 2;
        BracketBlock left = buildResultBracket(lo, mid, rounds, eliminated);
        BracketBlock right = buildResultBracket(mid, hi, rounds, eliminated);

        int gap = 4;
        int leftCenter = left.center;
        int rightCenterAbs = left.width + gap + right.center;
        int combinedWidth = left.width + gap + right.width;
        int nodeMid = (leftCenter + rightCenterAbs) / 2;

        StringBuilder connector = new StringBuilder(" ".repeat(combinedWidth));
        for (int c = leftCenter + 1; c < rightCenterAbs; c++) {
            connector.setCharAt(c, '─');
        }
        connector.setCharAt(leftCenter, '┌');
        connector.setCharAt(rightCenterAbs, '┐');
        connector.setCharAt(nodeMid, '┴');

        // Which round/match this connector represents, and whether it's decided yet
        int round = Integer.numberOfTrailingZeros(size);
        int matchIndex = lo / size;
        String resultText = "■";
        if (rounds.size() > round) {
            String[] roundResults = rounds.get(round);
            if (matchIndex < roundResults.length && roundResults[matchIndex] != null) {
                resultText = roundResults[matchIndex];
                if (eliminated.contains(resultText)) {
                    resultText = strikethrough(resultText);
                }
            }
        }

        // Center resultText (which may carry a team's ANSI color codes) using its
        // visible length, so hidden color codes don't throw off column alignment.
        int visibleResultLen = stripColor(resultText).length();
        int start = Math.max(0, Math.min(nodeMid - visibleResultLen / 2, combinedWidth - visibleResultLen));
        int rightSpaces = Math.max(0, combinedWidth - start - visibleResultLen);
        String resultRow = " ".repeat(start) + resultText + " ".repeat(rightSpaces);

        int height = left.lines.length;
        String[] merged = new String[2 + height];
        merged[0] = resultRow;
        merged[1] = connector.toString();
        String gapSpaces = " ".repeat(gap);
        for (int i = 0; i < height; i++) {
            merged[2 + i] = left.lines[i] + gapSpaces + right.lines[i];
        }

        BracketBlock node = new BracketBlock();
        node.lines = merged;
        node.width = combinedWidth;
        node.center = nodeMid;
        return node;
    }

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

    public static void menu(){
        int choice;

        // ASCII text generated online
        clearScreen();
        String title = """
                  ______
                 /_  __/___  __  ___________  ____ _____ ___  ___  ____  / /_
                  / / / __ \\/ / / / ___/ __ \\/ __ `/ __ `__ \\/ _ \\/ __ \\/ __/
                 / / / /_/ / /_/ / /  / / / / /_/ / / / / / /  __/ / / / /_ \s
                /_/  \\____/\\__,_/_/  /_/_/_/\\__,_/_/ /_/ /_/\\___/_/ /_/\\__/ \s
                  _____________  ____ _/ /_____  _____                      \s
                 / ___/ ___/ _ \\/ __ `/ __/ __ \\/ ___/                      \s
                / /__/ /  /  __/ /_/ / /_/ /_/ / /                          \s
                \\___/_/   \\___/\\__,_/\\__/\\____/_/                           \s
                """;

        int consoleWidth = getConsoleWidth();

        // Center the whole ASCII-art block as one unit (keeps its internal alignment intact)
        String[] titleLines = title.split("\n", -1);
        int titleMaxLen = 0;
        for (String l : titleLines) titleMaxLen = Math.max(titleMaxLen, l.length());
        String titlePad = " ".repeat(Math.max(0, (consoleWidth - titleMaxLen) / 2));
        for (String l : titleLines) System.out.println(titlePad + l);

        System.out.println();

        // Center the menu box
        String boxPad = " ".repeat(Math.max(0, (consoleWidth - MAX_WIDTH) / 2));
        System.out.println(boxPad + "┌" + "─".repeat(MAX_WIDTH-2) + "┐");
        System.out.println(boxPad + "│" + left(" 1. Play bracket", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + left(" 2. Create teams", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + left(" 3. Edit teams", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + left(" 4. Show teams", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + left(" 5. Show menu", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + left(" 6. Delete teams", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + left(" 7. Exit", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "└" + "─".repeat(MAX_WIDTH-2) + "┘");

        do {
            System.out.print("Choose operation (5 for menu): ");
            choice = scan.nextInt();
            switch (choice) {
                case 1 : PlayBracket(); break;
                case 2 : CreateTeams(); break;
                case 3 : EditTeams(); break;
                case 4 : DisplayTeams(); break;
                case 5 : menu();
                case 6 : DeleteTeams(); break;
            }
        } while (choice != 7);
    }

    // Lets the user pick the winner of every match, round by round, redrawing
    // the ASCII bracket tree with results filled in after each round.
    public static void PlayBracket() {
        System.out.printf("You currently have %d teams.%n", TeamNames.length);
        System.out.println();

        int n = TeamNames.length;
        if (n < 2 || (n & (n - 1)) != 0) {
            return;
        }

        clearScreen();

        int consoleWidth = getConsoleWidth();

        // Stage labels, left-to-right in the order teams progress toward the Final
        java.util.List<String> stages = new java.util.ArrayList<>();
        for (int round = 2; round <= n; round *= 2) {
            stages.add(0, roundName(round));
        }
        System.out.println();
        System.out.println(center(String.join("   >   ", stages), consoleWidth));
        System.out.println();

        String[] leaves = TeamNames.clone(); // keep each team's assigned color

        java.util.List<String[]> rounds = new java.util.ArrayList<>();
        rounds.add(leaves);

        java.util.Set<String> eliminated = new java.util.HashSet<>();

        System.out.println("Bracket:");
        printResultBracket(rounds, n, consoleWidth, eliminated);
        System.out.println();

        String[] current = leaves;
        String runnerUp = null;
        while (current.length > 1) {
            System.out.println(center(roundName(current.length), consoleWidth));
            System.out.println();

            String[] next = new String[current.length / 2];
            for (int i = 0; i < current.length; i += 2) {
                String a = current[i];
                String b = current[i + 1];

                int pick;
                do {
                    printMatchBox((i / 2) + 1, a, b, consoleWidth);
                    String prompt = "Winner (1/2): ";
                    System.out.print(" ".repeat(Math.max(0, (consoleWidth - prompt.length()) / 2)) + prompt);

                    if (!scan.hasNextInt()) {
                        System.out.println("Please enter 1 or 2.");
                        scan.nextLine();
                        pick = -1;
                        continue;
                    }
                    pick = scan.nextInt();

                    if (pick != 1 && pick != 2) {
                        System.out.println("Please enter 1 or 2.");
                    }
                } while (pick != 1 && pick != 2);

                next[i / 2] = (pick == 1) ? a : b;
                String loser = (pick == 1) ? b : a;
                eliminated.add(loser);
                if (current.length == 2) {
                    runnerUp = loser;
                }
                System.out.println();
                String advanceMsg = next[i / 2] + " advances!";
                int advancePad = Math.max(0, (consoleWidth - stripColor(advanceMsg).length()) / 2);
                System.out.println(" ".repeat(advancePad) + advanceMsg);
                System.out.println();
            }

            rounds.add(next);
            current = next;

            System.out.println("Bracket:");
            printResultBracket(rounds, n, consoleWidth, eliminated);
            System.out.println();
        }

        printChampionBox(current[0], runnerUp, n, consoleWidth);
        System.out.println();
    }

    // ASCII trophy art shown above the champion banner
    public static final String TrophyArt = """
    \s
    \s
    \s
    \s
    \s
    \s
    \s
                         +;X&&&&x;X           \s
                       +::;+xXX+;+++          \s
                      X;:x&&+:;;$&$;X         \s
                     ;x .;x+..;:;x+;+X        \s
                     X;    +;     : :X        \s
                     XX.    ;     ;;.+        \s
                     :;;:    ;;xX;;.;         \s
                      ;;;;:  ; ;;;X :         \s
                       ;;;.  :+;++:;;         \s
                       :.;;: ;..:;;:          \s
                       ;;;;;;    :;:          \s
                        x$+;+    :;           \s
                         X.;x.:.;:;           \s
                         x:+;:. ;;            \s
                          +;; ;;.             \s
                          ;;.;;;x             \s
                          :::;;;;             \s
                         +;++;x+xX            \s
                        :;.;+.:+: ;           \s
                        x .+;..+x ++          \s
                       ;  ;x;.:+x; +:         \s
                      ;..     . :.  ;;        \s
    \s
    \s
    \s
    \s
            """;

    // Centers a line (which may carry ANSI color codes) using its visible length
    private static String centerVisible(String word, int width) {
        int visible = stripColor(word).length();
        int leftPad = Math.max(0, (width - visible) / 2);
        int rightPad = Math.max(0, width - leftPad - visible);
        return " ".repeat(leftPad) + word + " ".repeat(rightPad);
    }

    // Draws a large banner announcing the tournament champion, with the trophy art beside it
    private static void printChampionBox(String champion, String runnerUp, int teamCount, int consoleWidth) {
        // Trim blank rows from the trophy art so it sits snugly next to the box
        String[] rawTrophyLines = TrophyArt.split("\n", -1);
        int first = 0, last = rawTrophyLines.length - 1;
        while (first < last && rawTrophyLines[first].isBlank()) first++;
        while (last > first && rawTrophyLines[last].isBlank()) last--;
        String[] trophyLines = java.util.Arrays.copyOfRange(rawTrophyLines, first, last + 1);
        int trophyWidth = 0;
        for (String l : trophyLines) trophyWidth = Math.max(trophyWidth, l.length());

        String title = " * CHAMPION * ";
        String champLine = "***  " + champion + "  ***";
        String runnerUpLine = (runnerUp != null) ? "Runner-up: " + runnerUp : "";
        int rounds = 31 - Integer.numberOfLeadingZeros(teamCount);
        String statsLine = teamCount + " teams  -  " + rounds + " rounds played";

        int inner = Math.max(60, Math.max(stripColor(champLine).length(),
                Math.max(stripColor(runnerUpLine).length(), statsLine.length())) + 8);

        String[] boxLines = {
            "╔" + center(title, "═", inner) + "╗",
            "║" + " ".repeat(inner) + "║",
            "║" + centerVisible(champLine, inner) + "║",
            "║" + " ".repeat(inner) + "║",
            "║" + centerVisible(runnerUpLine, inner) + "║",
            "║" + center(statsLine, inner) + "║",
            "║" + " ".repeat(inner) + "║",
            "╚" + "═".repeat(inner) + "╝",
        };
        int boxWidth = inner + 2;

        int gap = 2;
        int height = Math.max(trophyLines.length, boxLines.length);
        int trophyTopPad = (height - trophyLines.length) / 2;
        int boxTopPad = (height - boxLines.length) / 2;

        int totalWidth = trophyWidth + gap + boxWidth;
        String pad = " ".repeat(Math.max(0, (consoleWidth - totalWidth) / 2));

        System.out.println();
        for (int i = 0; i < height; i++) {
            String trophyLine = (i >= trophyTopPad && i < trophyTopPad + trophyLines.length)
                ? trophyLines[i - trophyTopPad] : "";
            String boxLine = (i >= boxTopPad && i < boxTopPad + boxLines.length)
                ? boxLines[i - boxTopPad] : "";

            String trophyCell = trophyLine + " ".repeat(Math.max(0, trophyWidth - trophyLine.length()));
            System.out.println(pad + trophyCell + " ".repeat(gap) + boxLine);
        }
    }

    // Draws a centered box around a single match's two options, sized to fit the team labels
    private static void printMatchBox(int matchNum, String a, String b, int consoleWidth) {
        String content = String.format(" ".repeat(9) + "[1] %s    [2] %s", a, b);
        int inner = Math.max(36, stripColor(content).length());
        String title = " Match " + matchNum + " ";
        String pad = " ".repeat(Math.max(0, (consoleWidth - (inner + 2)) / 2));

        System.out.println(pad + "┌" + center(title, "─", inner) + "┐");
        System.out.println(pad + "│" + left(content, inner) + "│");
        System.out.println(pad + "└" + "─".repeat(inner) + "┘");
    }

    // Centering the text
    public static String left(String word, int max) {
        // [ AI ] this will replace the invisible ANSI character with none to fix the padding issue
        int visible = word.replaceAll("\u001B\\[[;\\d]*m", "").length();
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

    // Display team names in terminal
    public static void DisplayTeams() {
        if (TeamNames.length == 0) {
            System.out.println();
            System.out.println("┌" + center(" List of teams ", "─", MAX_WIDTH-2) + "┐");
            System.out.println("│" + " ".repeat(MAX_WIDTH-2) + "│");
            System.out.println("│" + center("(Empty list)", MAX_WIDTH-2) + "│");
            System.out.println("│" + " ".repeat(MAX_WIDTH-2) + "│");
            System.out.println("└" + "─".repeat(MAX_WIDTH-2) + "┘");
        }
        else if (TeamNames.length == 16) {
            System.out.println();
            System.out.println("┌" + center(" List of teams ", "─", MAX_WIDTH-2) + "┐");

            // Double columns
            int j = 9;

            for (int i = 0; i <= 7; i++) {
                // format first then print
                String name = String.format(" [%d] %s     [%d] %s", i+1, TeamNames[i], i+j, TeamNames[i+j-1]);
                System.out.println("│" + left(name, MAX_WIDTH-2) + "│");
            }
            // "\u001B[7m" and "\u001B[0m" to highlight
            System.out.println("└" + "─".repeat(MAX_WIDTH-2) + "┘");
        }
        else {
            System.out.println();
            System.out.println("┌" + center(" List of teams ", "─", MAX_WIDTH-2) + "┐");
            for (int i = 0; i < TeamNames.length; i++) {
                String name = String.format(" [%d] %s", i+1, TeamNames[i]);
                System.out.println("│" + left(name, MAX_WIDTH-2) + "│");
            }
            System.out.println("└" + "─".repeat(MAX_WIDTH-2) + "┘");
        }
        System.out.println();
    }

    public static String getColor(String text, String color) {

        // Color codes in ANSI
        String RED = "\u001B[31m";
        String GREEN = "\u001B[32m";
        String YELLOW = "\u001B[33m";
        String BLUE = "\u001B[94m"; // bright blue - the standard blue (34m) is too dark to read
        String PURPLE = "\u001B[35m";
        String CYAN = "\u001B[36m";
        String RESET = "\u001B[0m";

        //Removes initial colors
        text = text.replaceAll("\u001B\\[[;\\d]*m", "");

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

    public static void CreateTeams() {
        scan.nextLine(); // consume the leftovers from the menu input
        boolean ErrorCheck;

        // Warn before overwriting an existing team list
        if (TeamNames.length > 0) {
            System.out.print("A team list already exists. Overwrite it? (Y/N): ");
            String confirm = scan.nextLine().strip().toUpperCase();

            if (!confirm.equals("Y")) {
                System.out.println("Cancelled.");
                return;
            }
        }

        // Let the user skip manual entry and load the sample teams instead
        int mode;
        do {
            System.out.print("1. Enter teams manually, 2. Load sample teams: ");
            mode = scan.nextInt();
            scan.nextLine();

            if (mode != 1 && mode != 2) {
                System.out.println("Select the given range!");
            }
        } while (mode != 1 && mode != 2);

        if (mode == 2) {
            LoadSampleTeams();
            return;
        }

        // Get number of teams
        do {
            ErrorCheck = false;
            System.out.print("Select the number of teams (2, 4, 8, 16): ");
            int TeamCount = scan.nextInt();

            // clears buffer
            scan.nextLine();

            if (TeamCount == 2 || TeamCount == 4 || TeamCount == 8 || TeamCount == 16) {
                TeamNames = new String[TeamCount];
                // Add team in function
                AddTeams(TeamNames.length);
            } else {
                System.out.println("Select the given range!");
                ErrorCheck = true;
            }
        } while (ErrorCheck);
    }

    public static void AddTeams(int length) {
        System.out.println("Enter team abbreviation (ARG, FRA etc.)");

        for (int i = 0; i < length; i++) {
            String Abr;

            // Condition: check the abbreviations
            Abr = EnterName(i+1);

            // check get colors only if the upper condition succeeds
            Abr = ColorCheck(Abr);

            // save those team names to the array
            TeamNames[i] = Abr;
        }
        // notify users about the input
        System.out.println();
        System.out.printf("+ %d teams has been added.%n", TeamNames.length);
        System.out.println();
    }

    public static String EnterName(int count) {
        boolean ErrorCheck;
        String Abr;
        do {
            ErrorCheck = false; // resets everytime
            System.out.printf("Team [%d] : ", count);
            Abr = scan.nextLine().strip().toUpperCase();

            // Check empty spaces
            if (Abr.trim().isEmpty()) {
                System.out.println("Abbreviation can't be empty!");
                ErrorCheck = true;
            }
            else if (Abr.length() < 3){
                System.out.println("Abbreviation too short. 3 characters only!");
                ErrorCheck = true;
            }
            // accepts only abr length 3
            if (Abr.length() > 3) {
                System.out.println("Abbreviation too long. Shorten to 3 characters!");
                ErrorCheck = true;
            }

            // Check for any duplicates
            for (String name : TeamNames) {
                if (name != null && name.replaceAll("\u001B\\[[;\\d]*m", "").equals(Abr)) { //strip color codes before comparing
                    System.out.println("Name already exists. Please select again!");
                    ErrorCheck = true;
                    break;
                }
            }

        } while (ErrorCheck); // While not duplicated
        return Abr;
    }

    public static String ColorCheck(String Abr) {
        boolean ErrorCheck;

        do {
            ErrorCheck = false;
            System.out.printf("Choose a color (%s R, %s Y, %s G, %s B, %s P, %s Default): ", getColor("■","RED"), getColor("■", "YELLOW"),getColor("■","GREEN"), getColor("■","BLUE"), getColor("■","PURPLE"), getColor("■", "DEFAULT"));
            String ColorSelect = scan.nextLine().strip().toUpperCase();

            switch (ColorSelect) {
                case "RED", "R" -> Abr = getColor(Abr, "RED");
                case "YELLOW", "Y" -> Abr = getColor(Abr, "YELLOW");
                case "GREEN", "G" -> Abr = getColor(Abr, "GREEN");
                case "BLUE", "B" -> Abr = getColor(Abr, "BLUE");
                case "PURPLE", "P" -> Abr = getColor(Abr, "PURPLE");
                case "DEFAULT", "D" -> Abr = getColor(Abr, "default");
                default -> {
                    System.out.println("Invalid");
                    ErrorCheck = true;
                }
            }
        } while (ErrorCheck);
        return Abr;
    }

    public static void EditTeams() {
        int choice;
        int index;

        // Display list
        DisplayTeams();

        // user to select
        while (true) {
            if (TeamNames.length == 0) {
                System.out.println("Empty list. No teams to modify");
                break;
            }

            System.out.println("┌" + "─".repeat(MAX_WIDTH - 2) + "┐");
            System.out.println("│" + left(" 1. Edit names", MAX_WIDTH - 2) + "│");
            System.out.println("│" + left(" 2. Edit colors", MAX_WIDTH - 2) + "│");
            System.out.println("│" + left(" 3. Exit", MAX_WIDTH - 2) + "│");
            System.out.println("└" + "─".repeat(MAX_WIDTH - 2) + "┘");

            System.out.print("Choose edit operation: ");
            choice = scan.nextInt();

            if (choice == 1) {
                System.out.print("Enter index to edit: ");
                index = scan.nextInt();

                scan.nextLine();

                if (index < 0 || index > TeamNames.length) {
                    System.out.println("Invalid index.");
                    continue;
                }

                System.out.printf("%nRe-enter name for ");

                String new_name = EnterName(index+1);
                new_name = ColorCheck(new_name);

                TeamNames[index-1] = new_name;

                System.out.println("+ Team name has been updated.");
                DisplayTeams();
            } else if (choice == 2) {
                System.out.print("Enter index to edit: ");
                index = scan.nextInt();
                index -= 1;

                scan.nextLine();

                if (index < 0 || index >= TeamNames.length) {
                    System.out.println("Invalid index.");
                    continue;
                }

                TeamNames[index] = ColorCheck(TeamNames[index]);

                System.out.printf("%n+ %s team color has been updated.%n", TeamNames[index]);
                DisplayTeams();

            } else if (choice == 3) {
                System.out.println("Exiting...");
                break;
            } else {
                System.out.println("\nInvalid operation.");
            }
        }
    }
}
