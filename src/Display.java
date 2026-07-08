public class Display {
    static int MAX_WIDTH = 32;

    public static void menu(){
        int choice;

        printMenu();

        do {
            System.out.print("Choose operation (5 for menu): ");

            if (!TeamRegistry.scan.hasNextInt()) {
                System.out.println("Please enter a number from the menu.");
                TeamRegistry.scan.nextLine(); // throw away whatever bad input was typed
                choice = -1;
                continue;
            }

            choice = TeamRegistry.scan.nextInt();

            switch (choice) {
                case 1 : Bracket.PlayBracket(); break;
                case 2 : TeamRegistry.CreateTeams(); break;
                case 3 : TeamRegistry.EditTeams(); break;
                case 4 : TeamRegistry.DisplayTeams(); break;
                case 5 : printMenu(); break;
                case 6 : TeamRegistry.DeleteTeams(); break;
                case 7 : return;
            }
        } while (choice != 7);
    }

    private static void printMenu() {
        // ASCII text generated online
        ConsoleUtil.clearScreen();
        String title = """

                
                
                █████▄ ▄▄▄▄   ▄▄▄   ▄▄▄▄ ▄▄ ▄▄ ▄▄▄▄▄ ▄▄▄▄▄▄                ■\s
                ██▄▄██ ██▄█▄ ██▀██ ██▀▀▀ ██▄█▀ ██▄▄    ██           ┌──────┴──────┐\s
                ██▄▄█▀ ██ ██ ██▀██ ▀████ ██ ██ ██▄▄▄   ██           ■             ■\s
                ▄█████ ▄▄▄▄  ▄▄▄▄▄  ▄▄▄ ▄▄▄▄▄▄ ▄▄▄  ▄▄▄▄         ┌──┴───┐      ┌──┴───┐\s
                ██     ██▄█▄ ██▄▄  ██▀██  ██  ██▀██ ██▄█▄        ■      ■      ■      ■\s
                ▀█████ ██ ██ ██▄▄▄ ██▀██  ██  ▀███▀ ██ ██  v1\s
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
    }
}
