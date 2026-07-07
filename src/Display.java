// Entry-point screen: draws the main menu and routes to the other classes
// (TeamRegistry for team CRUD, BracketRenderer for the bracket, ConsoleUtil/ColorUtil
// for shared console helpers).
public class Display {
    static int MAX_WIDTH = 32;

    public static void menu(){
        int choice;

        // ASCII text generated online
        ConsoleUtil.clearScreen();
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

        int consoleWidth = ConsoleUtil.getConsoleWidth();

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
        System.out.println(boxPad + "│" + ConsoleUtil.left(" 1. Play bracket", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + ConsoleUtil.left(" 2. Create teams", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + ConsoleUtil.left(" 3. Edit teams", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + ConsoleUtil.left(" 4. Show teams", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + ConsoleUtil.left(" 5. Show menu", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + ConsoleUtil.left(" 6. Delete teams", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "│" + ConsoleUtil.left(" 7. Exit", MAX_WIDTH-2) + "│");
        System.out.println(boxPad + "└" + "─".repeat(MAX_WIDTH-2) + "┘");

        do {
            System.out.print("Choose operation (5 for menu): ");
            choice = TeamRegistry.scan.nextInt();
            switch (choice) {
                case 1 : BracketRenderer.PlayBracket(); break;
                case 2 : TeamRegistry.CreateTeams(); break;
                case 3 : TeamRegistry.EditTeams(); break;
                case 4 : TeamRegistry.DisplayTeams(); break;
                case 5 : menu();
                case 6 : TeamRegistry.DeleteTeams(); break;
            }
        } while (choice != 7);
    }
}
