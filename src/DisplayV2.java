import java.util.*;
import java.io.IOException;

public class DisplayV2 {
    public static Scanner scan = new Scanner(System.in);
    public static String[] TeamNames = new String[0];
    static int MAX_WIDTH = 32;

    // IMPROVEMENT 1: bracket now uses actual TeamNames instead of hardcoded names
    public static void bracket_all() {
        System.out.println();
        System.out.println(" ".repeat(35) + "World Cup Tournament" + " ".repeat(35));
        System.out.println();

        if (TeamNames.length == 0) {
            System.out.println("No teams yet. Create teams first (option 2)!");

        } else if (TeamNames.length == 2) {
            String t1 = stripColor(TeamNames[0]);
            String t2 = stripColor(TeamNames[1]);
            System.out.printf("               ┌──── FINAL ────┐%n");
            System.out.printf("               │  %s  vs  %s   │%n", t1, t2);
            System.out.printf("               └───────────────┘%n");

        } else if (TeamNames.length == 4) {
            String t1 = stripColor(TeamNames[0]);
            String t2 = stripColor(TeamNames[1]);
            String t3 = stripColor(TeamNames[2]);
            String t4 = stripColor(TeamNames[3]);
            System.out.printf("                       ┌──────────── FINAL ────────────┐%n");
            System.out.printf("                       │         ■ vs ■            │%n");
            System.out.printf("                       └───────────────────────────────┘%n");
            System.out.printf("            ┌──────────┴──────────┐                      ┌──────────┴──────────┐%n");
            System.out.printf("           %s                   %s                    %s                   %s%n", t1, t2, t3, t4);

        } else if (TeamNames.length == 8) {
            String t1 = stripColor(TeamNames[0]);
            String t2 = stripColor(TeamNames[1]);
            String t3 = stripColor(TeamNames[2]);
            String t4 = stripColor(TeamNames[3]);
            String t5 = stripColor(TeamNames[4]);
            String t6 = stripColor(TeamNames[5]);
            String t7 = stripColor(TeamNames[6]);
            String t8 = stripColor(TeamNames[7]);
            System.out.printf("                       ┌──────────── FINAL ────────────┐%n");
            System.out.printf("                       │         ■ vs ■            │%n");
            System.out.printf("                       └──────────────────────────────┘%n");
            System.out.printf("            ┌──────────┴──────────┐              ┌──────────┴──────────┐%n");
            System.out.printf("        ■ vs ■             ■ vs ■%n");
            System.out.printf("       ┌────┴──────┐         ┌────┴──────┐    ┌────┴──────┐         ┌────┴──────┐%n");
            System.out.printf("      %s    %s      %s    %s     %s    %s      %s    %s%n", t1, t2, t3, t4, t5, t6, t7, t8);

        } else {
            System.out.println("Bracket for " + TeamNames.length + " teams - group stage not yet implemented.");
        }
        System.out.println();
    }

    // IMPROVEMENT 2: helper method to strip ANSI color codes
    // This avoids copy-pasting the same regex everywhere in the code
    public static String stripColor(String text) {
        return text.replaceAll("\\[[;\\d]*m", "");
    }

    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                new ProcessBuilder("clear").inheritIO().start().waitFor();
            }
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public static void menu() {
        int choice;
        clearScreen();
        System.out.print("""
                  ______
                 /_  __/___  __  ___________  ____ _____ ___  ___  ____  / /_
                  / / / __ \\/ / / / ___/ __ \\/ __ `/ __ `__ \\/ _ \\/ __ \\/ __/
                 / / / /_/ / /_/ / /  / / / / /_/ / / / / / /  __/ / / / /_ \s
                /_/  \\____/\\__,_/_/  /_/_/_/\\__,_/_/ /_/ /_/\\___/_/ /_/\\__/ \s
                  _____________  ____ _/ /_____  _____                      \s
                 / ___/ ___/ _ \\/ __ `/ __/ __ \\/ ___/                      \s
                / /__/ /  /  __/ /_/ / /_/ /_/ / /                          \s
                \\___/_/   \\___/\\__,_/\\__/\\____/_/                           \s

                """);

        System.out.println();
        System.out.println("┌" + "─".repeat(MAX_WIDTH - 2) + "┐");
        System.out.println("│" + left(" 1. Show Bracket", MAX_WIDTH - 2) + "│");
        System.out.println("│" + left(" 2. Create Teams", MAX_WIDTH - 2) + "│");
        System.out.println("│" + left(" 3. Edit Teams", MAX_WIDTH - 2) + "│");
        System.out.println("│" + left(" 4. Show Teams", MAX_WIDTH - 2) + "│");
        System.out.println("│" + left(" 5. Show Menu", MAX_WIDTH - 2) + "│");
        System.out.println("│" + left(" 6. Exit", MAX_WIDTH - 2) + "│");
        System.out.println("└" + "─".repeat(MAX_WIDTH - 2) + "┘");

        do {
            System.out.print("Choose operation (5 for menu): ");

            // IMPROVEMENT 3: validate that input is a number BEFORE reading it
            // In your version, typing a letter would crash the whole program with an error
            if (!scan.hasNextInt()) {
                System.out.println("Please enter a number between 1 and 6.");
                scan.nextLine();
                choice = -1;
                continue;
            }

            choice = scan.nextInt();

            switch (choice) {
                case 1 -> { clearScreen(); bracket_all(); }
                case 2 -> { clearScreen(); CreateTeams(); }
                case 3 -> { clearScreen(); EditTeams(); }
                case 4 -> DisplayTeams();
                case 5 -> { clearScreen(); menu(); return; }
                case 6 -> System.out.println("Goodbye!");
                // IMPROVEMENT 4: tell the user their input was invalid instead of doing nothing
                default -> System.out.println("Invalid choice. Please enter 1 to 6.");
            }
        } while (choice != 6);
    }

    public static String left(String word, int max) {
        int visible = word.replaceAll("\u001B\\[[;\\d]*m", "").length();
        int padding = max - visible;
        return word + " ".repeat(padding);
    }

    public static String center(String word, String symbol, int max) {
        int total_padding = max - word.length();
        int left_padding = total_padding / 2;
        int right_padding = total_padding - left_padding;
        return symbol.repeat(Math.max(0, left_padding)) + word + symbol.repeat(Math.max(0, right_padding));
    }

    public static String center(String word, int max) {
        int total_padding = max - word.length();
        int left_padding = total_padding / 2;
        int right_padding = total_padding - left_padding;
        return " ".repeat(Math.max(0, left_padding)) + word + " ".repeat(Math.max(0, right_padding));
    }

    public static void DisplayTeams() {
        if (TeamNames.length == 0) {
            System.out.println();
            System.out.println("┌" + center(" List of teams ", "─", MAX_WIDTH - 2) + "┐");
            System.out.println("│" + " ".repeat(MAX_WIDTH - 2) + "│");
            System.out.println("│" + center("(Empty list)", MAX_WIDTH - 2) + "│");
            System.out.println("│" + " ".repeat(MAX_WIDTH - 2) + "│");
            System.out.println("└" + "─".repeat(MAX_WIDTH - 2) + "┘");
        } else if (TeamNames.length == 16) {
            System.out.println();
            System.out.println("┌" + center(" List of teams ", "─", MAX_WIDTH - 2) + "┐");
            int j = 9;
            for (int i = 0; i <= 7; i++) {
                String name = String.format(" [%d] %s     [%d] %s", i + 1, TeamNames[i], i + j, TeamNames[i + j - 1]);
                System.out.println("│" + left(name, MAX_WIDTH - 2) + "│");
            }
            System.out.println("└" + "─".repeat(MAX_WIDTH - 2) + "┘");
        } else {
            System.out.println();
            System.out.println("┌" + center(" List of teams ", "─", MAX_WIDTH - 2) + "┐");
            for (int i = 0; i < TeamNames.length; i++) {
                String name = String.format(" [%d] %s", i + 1, TeamNames[i]);
                System.out.println("│" + left(name, MAX_WIDTH - 2) + "│");
            }
            System.out.println("└" + "─".repeat(MAX_WIDTH - 2) + "┘");
        }
        System.out.println();
    }

    public static String getColor(String text, String color) {
        String RED    = "[31m";
        String GREEN  = "[32m";
        String YELLOW = "[33m";
        String BLUE   = "[34m";
        String PURPLE = "[35m";
        String CYAN   = "[36m";
        String RESET  = "[0m";

        text = stripColor(text);

        switch (color) {
            case "RED"    -> text = RED    + text + RESET;
            case "GREEN"  -> text = GREEN  + text + RESET;
            case "YELLOW" -> text = YELLOW + text + RESET;
            case "BLUE"   -> text = BLUE   + text + RESET;
            case "PURPLE" -> text = PURPLE + text + RESET;
            case "CYAN"   -> text = CYAN   + text + RESET;
        }
        return text;
    }

    // IMPROVEMENT 6: simplified CreateTeams — removed the pointless while(true)/break wrapper
    // and cleaned up the duplicate scan.nextLine() calls that caused confusing double-enter behavior
    public static void CreateTeams() {
        scan.nextLine(); // consume leftover newline from menu's scan.nextInt()

        while (true) {
            System.out.print("Select the number of teams (2, 4, 8, 16): ");

            if (!scan.hasNextInt()) {
                System.out.println("Please enter a number!");
                scan.nextLine();
                continue;
            }

            int TeamCount = scan.nextInt();
            scan.nextLine(); // clear buffer after reading int

            if (TeamCount == 2 || TeamCount == 4 || TeamCount == 8 || TeamCount == 16) {
                TeamNames = new String[TeamCount];
                AddTeams(TeamNames.length);
                break;
            } else {
                System.out.println("Please select 2, 4, 8, or 16.");
            }
        }
    }

    public static void AddTeams(int length) {
        System.out.println("Enter team abbreviation (ARG, FRA etc.)");
        for (int i = 0; i < length; i++) {
            String Abr = EnterName(i + 1);
            Abr = ColorCheck(Abr);
            TeamNames[i] = Abr;
        }
        System.out.println();
        System.out.printf("+ %d teams have been added.%n", TeamNames.length);
        System.out.println();
    }

    public static String EnterName(int count) {
        boolean ErrorCheck;
        String Abr;
        do {
            ErrorCheck = false;
            System.out.printf("Team [%d] : ", count);
            Abr = scan.nextLine().strip().toUpperCase();

            if (Abr.isEmpty()) {
                System.out.println("Abbreviation can't be empty!");
                ErrorCheck = true;
            } else if (Abr.length() < 3) {
                System.out.println("Abbreviation too short. 3 characters only!");
                ErrorCheck = true;
            } else if (Abr.length() > 3) {
                System.out.println("Abbreviation too long. Shorten to 3 characters!");
                ErrorCheck = true;
            }

            // IMPROVEMENT 7: duplicate check now strips color codes before comparing
            // In your version, "ARG" colored red stored as "[31mARG[0m", so adding
            // a plain "ARG" again would not be detected as a duplicate. Now it is.
            if (!ErrorCheck) {
                for (String name : TeamNames) {
                    if (name != null && stripColor(name).equals(Abr)) {
                        System.out.println("Name already exists. Please select again!");
                        ErrorCheck = true;
                        break;
                    }
                }
            }

        } while (ErrorCheck);
        return Abr;
    }

    public static String ColorCheck(String Abr) {
        boolean ErrorCheck;
        do {
            ErrorCheck = false;
            System.out.printf("Choose a color (%s R, %s Y, %s G, %s B, %s P, %s C, D Default): ",
                getColor("■", "RED"), getColor("■", "YELLOW"), getColor("■", "GREEN"),
                getColor("■", "BLUE"), getColor("■", "PURPLE"), getColor("■", "CYAN"));
            String ColorSelect = scan.nextLine().strip().toUpperCase();

            switch (ColorSelect) {
                case "RED",     "R" -> Abr = getColor(Abr, "RED");
                case "YELLOW",  "Y" -> Abr = getColor(Abr, "YELLOW");
                case "GREEN",   "G" -> Abr = getColor(Abr, "GREEN");
                case "BLUE",    "B" -> Abr = getColor(Abr, "BLUE");
                case "PURPLE",  "P" -> Abr = getColor(Abr, "PURPLE");
                case "CYAN",    "C" -> Abr = getColor(Abr, "CYAN");
                case "DEFAULT", "D" -> { /* keep no color */ }
                default -> {
                    System.out.println("Invalid color choice.");
                    ErrorCheck = true;
                }
            }
        } while (ErrorCheck);
        return Abr;
    }

    public static void EditTeams() {
        int choice;
        int index;

        DisplayTeams();

        while (true) {
            if (TeamNames.length == 0) {
                System.out.println("Empty list. No teams to modify.");
                break;
            }

            System.out.println("┌" + "─".repeat(MAX_WIDTH - 2) + "┐");
            System.out.println("│" + left(" 1. Edit name", MAX_WIDTH - 2) + "│");
            System.out.println("│" + left(" 2. Edit color", MAX_WIDTH - 2) + "│");
            System.out.println("│" + left(" 3. Delete a team", MAX_WIDTH - 2) + "│");
            System.out.println("│" + left(" 4. Exit", MAX_WIDTH - 2) + "│");
            System.out.println("└" + "─".repeat(MAX_WIDTH - 2) + "┘");

            System.out.print("Choose edit operation: ");

            if (!scan.hasNextInt()) {
                System.out.println("Please enter a number!");
                scan.nextLine();
                continue;
            }

            choice = scan.nextInt();
            scan.nextLine(); // clear buffer

            if (choice == 1) {
                System.out.print("Enter team number to edit: ");
                if (!scan.hasNextInt()) { scan.nextLine(); continue; }
                index = scan.nextInt() - 1;
                scan.nextLine();

                // IMPROVEMENT 8: added bounds check so entering a bad number doesn't crash
                if (index < 0 || index >= TeamNames.length) {
                    System.out.println("Invalid team number!");
                    continue;
                }

                String new_name = EnterName(index + 1);
                new_name = ColorCheck(new_name);
                TeamNames[index] = new_name;
                System.out.println("+ Team updated.");
                DisplayTeams();

            } else if (choice == 2) {
                System.out.print("Enter team number to edit: ");
                if (!scan.hasNextInt()) { scan.nextLine(); continue; }
                index = scan.nextInt() - 1;
                scan.nextLine();

                if (index < 0 || index >= TeamNames.length) {
                    System.out.println("Invalid team number!");
                    continue;
                }

                TeamNames[index] = ColorCheck(TeamNames[index]);
                System.out.printf("%n+ Team color updated.%n");
                DisplayTeams();

            } else if (choice == 3) {
                // IMPROVEMENT 9: delete was listed in the menu but never actually worked
                // Now it removes the team and shifts the array to fill the gap
                System.out.print("Enter team number to delete: ");
                if (!scan.hasNextInt()) { scan.nextLine(); continue; }
                index = scan.nextInt() - 1;
                scan.nextLine();

                if (index < 0 || index >= TeamNames.length) {
                    System.out.println("Invalid team number!");
                    continue;
                }

                String deleted = stripColor(TeamNames[index]);

                String[] newTeams = new String[TeamNames.length - 1];
                for (int i = 0, j = 0; i < TeamNames.length; i++) {
                    if (i != index) {
                        newTeams[j++] = TeamNames[i];
                    }
                }
                TeamNames = newTeams;

                System.out.printf("%n- \"%s\" has been deleted.%n", deleted);
                DisplayTeams();

            } else if (choice == 4) {
                System.out.println("Exiting edit menu...");
                break;
            } else {
                System.out.println("Invalid operation.");
            }
        }
    }
}
