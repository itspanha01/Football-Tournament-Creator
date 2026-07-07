import java.util.Scanner;

// listing, editing, and deleting teams
public class TeamRegistry {
    public static Scanner scan = new Scanner(System.in); // Make the scanner available for all
    public static String[] TeamNames = new String[0];
    static int MAX_WIDTH = 32;

    // Sample 16-team list for quick testing
    public static final String[] SampleTeams16 = {
        ColorUtil.getColor("PAR", "BLUE"),   ColorUtil.getColor("FRA", "BLUE"),
        ColorUtil.getColor("CAN", "RED"),    ColorUtil.getColor("MOR", "RED"),
        ColorUtil.getColor("POR", "RED"),    ColorUtil.getColor("ESP", "RED"),
        ColorUtil.getColor("USA", "BLUE"), ColorUtil.getColor("BEL", "YELLOW"),
        ColorUtil.getColor("BRA", "YELLOW"), ColorUtil.getColor("NOR", "RED"),
        ColorUtil.getColor("MEX", "GREEN"),   ColorUtil.getColor("ENG", "DEFAULT"),
        ColorUtil.getColor("ARG", "BLUE"),    ColorUtil.getColor("EGY", "RED"),
        ColorUtil.getColor("SUI", "RED"),  ColorUtil.getColor("COL", "YELLOW")
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

    // Display team names in terminal
    public static void DisplayTeams() {
        if (TeamNames.length == 0) {
            System.out.println();
            System.out.println("┌" + ConsoleUtil.center(" List of teams ", "─", MAX_WIDTH-2) + "┐");
            System.out.println("│" + " ".repeat(MAX_WIDTH-2) + "│");
            System.out.println("│" + ConsoleUtil.center("(Empty list)", MAX_WIDTH-2) + "│");
            System.out.println("│" + " ".repeat(MAX_WIDTH-2) + "│");
            System.out.println("└" + "─".repeat(MAX_WIDTH-2) + "┘");
        }
        else if (TeamNames.length == 16) {
            System.out.println();
            System.out.println("┌" + ConsoleUtil.center(" List of teams ", "─", MAX_WIDTH-2) + "┐");

            // Double columns
            int j = 9;

            for (int i = 0; i <= 7; i++) {
                // format first then print
                String name = String.format(" [%d] %s     [%d] %s", i+1, TeamNames[i], i+j, TeamNames[i+j-1]);
                System.out.println("│" + ConsoleUtil.left(name, MAX_WIDTH-2) + "│");
            }
            // "[7m" and "[0m" to highlight
            System.out.println("└" + "─".repeat(MAX_WIDTH-2) + "┘");
        }
        else {
            System.out.println();
            System.out.println("┌" + ConsoleUtil.center(" List of teams ", "─", MAX_WIDTH-2) + "┐");
            for (int i = 0; i < TeamNames.length; i++) {
                String name = String.format(" [%d] %s", i+1, TeamNames[i]);
                System.out.println("│" + ConsoleUtil.left(name, MAX_WIDTH-2) + "│");
            }
            System.out.println("└" + "─".repeat(MAX_WIDTH-2) + "┘");
        }
        System.out.println();
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
            System.out.println();
            System.out.print("[1] Enter teams manually\n[2] Load sample teams");
            System.out.println();
            System.out.print("\nChoose operation: ");

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
                if (name != null && name.replaceAll("\\[[;\\d]*m", "").equals(Abr)) { //strip color codes before comparing
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
            System.out.printf("Choose a color (%s R, %s Y, %s G, %s B, %s P, %s Default): ", ColorUtil.getColor("■","RED"), ColorUtil.getColor("■", "YELLOW"),ColorUtil.getColor("■","GREEN"), ColorUtil.getColor("■","BLUE"), ColorUtil.getColor("■","PURPLE"), ColorUtil.getColor("■", "DEFAULT"));
            String ColorSelect = scan.nextLine().strip().toUpperCase();

            switch (ColorSelect) {
                case "RED", "R" -> Abr = ColorUtil.getColor(Abr, "RED");
                case "YELLOW", "Y" -> Abr = ColorUtil.getColor(Abr, "YELLOW");
                case "GREEN", "G" -> Abr = ColorUtil.getColor(Abr, "GREEN");
                case "BLUE", "B" -> Abr = ColorUtil.getColor(Abr, "BLUE");
                case "PURPLE", "P" -> Abr = ColorUtil.getColor(Abr, "PURPLE");
                case "DEFAULT", "D" -> Abr = ColorUtil.getColor(Abr, "default");
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
            System.out.println("│" + ConsoleUtil.left(" 1. Edit names", MAX_WIDTH - 2) + "│");
            System.out.println("│" + ConsoleUtil.left(" 2. Edit colors", MAX_WIDTH - 2) + "│");
            System.out.println("│" + ConsoleUtil.left(" 3. Exit", MAX_WIDTH - 2) + "│");
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

                String new_name = EnterName(index);
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
