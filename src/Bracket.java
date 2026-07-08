public class Bracket {
    // One array per round, holding whoever won that round's match and filled in by AskWinners() before anything gets drawn.

    static String[] round16Winners; // the 8 teams that win their Round of 16 match
    static String[] quarterWinners; // the 4 teams that win their Quarter-Final
    static String[] semiWinners;    // the 2 teams that make the Final
    static String[] champion;       // the 1 team that wins it all

    public static void PlayBracket() {
        int n = TeamRegistry.TeamNames.length;

        if (n != 2 && n != 4 && n != 8 && n != 16) {
            System.out.println("This bracket only supports exactly 2, 4, 8, or 16 teams.");
            return;
        }

        // Ask for winners round by round, bottom-up, starting from whatever
        // round the current team count actually begins at.
        String[] current = TeamRegistry.TeamNames;

        if (n > 8) {
            System.out.println(ConsoleUtil.center(" Round of 16 ", "─",31));
            round16Winners = AskWinners(current);
            current = round16Winners;
        }
        if (n > 4) {
            System.out.println();
            System.out.println(ConsoleUtil.center(" Quarter-finals ", "─",31));
            quarterWinners = AskWinners(current);
            current = quarterWinners;
        }
        if (n > 2) {
            System.out.println();
            System.out.println(ConsoleUtil.center(" Semi-finals ", "─",31));
            semiWinners = AskWinners(current);
            current = semiWinners;
        }
        System.out.println();
        System.out.println(ConsoleUtil.center(" Finals ", "─",31));
        champion = AskWinners(current);

        SelectRound();
    }

    // Asks "who wins?" for every pair in the given round, and returns any
    // array half the size holding just the winners.
    public static String[] AskWinners(String[] currentRound) {
        String[] winners = new String[currentRound.length / 2];

        for (int i = 0; i < currentRound.length; i += 2) {
            String teamA = currentRound[i];
            String teamB = currentRound[i + 1];

            int pick;
            do {
                System.out.printf("%s vs %s - who wins? (1/2): ", teamA, teamB);

                if (!TeamRegistry.scan.hasNextInt()) {
                    System.out.println("Please enter 1 or 2.");
                    TeamRegistry.scan.nextLine(); // throw away whatever bad input was typed
                    pick = -1;
                    continue;
                }

                pick = TeamRegistry.scan.nextInt();
                TeamRegistry.scan.nextLine(); // clear the leftover newline so it doesn't confuse the next prompt
                if (pick != 1 && pick != 2) {
                    System.out.println("Please enter 1 or 2.");
                }
            } while (pick != 1 && pick != 2);

            winners[i / 2] = (pick == 1) ? teamA : teamB;
        }

        return winners;
    }

    public static void SelectRound() { //
        int n = TeamRegistry.TeamNames.length;
        switch (n) {
            case 2 -> Finals();
            case 4 -> SemiFinals();
            case 8 -> QuarterFinals();
            case 16 -> RoundOf16();
        }
        System.out.println();
    }
    public static void Finals() {
        // Prints the final
            System.out.println(" ".repeat(59) + champion[0]);
            System.out.print(" ".repeat(7));
            System.out.printf(" ".repeat(25) + "┌" + "─".repeat(27) + "┴" + "─".repeat(27) + "┐");
            System.out.println();
            for (int i = 0; i <= 1; i++) {
                // If there were rounds before the Final, show their winners;
                // otherwise (exactly 2 teams to start) these ARE the raw teams.
                String label = (TeamRegistry.TeamNames.length > 2) ? semiWinners[i] : TeamRegistry.TeamNames[i];
                System.out.printf(" ".repeat(31) + "%s" + " ".repeat(22), label);
            }
    }

    public static void SemiFinals() {
        Finals();
            // Prints the sixth row
            System.out.println();
            System.out.print(" ".repeat(7));
            for (int i = 0; i <= 1; i++) {
                System.out.printf(" ".repeat(11)+ "┌" + "─".repeat(13) + "┴" + "─".repeat(13) + "┐" + " ".repeat(16));
            }

            //prints the fifth row
            System.out.println();
            System.out.print(" ".repeat(7));
            for (int i = 0; i <= 3; i++) {
                // If there was a Quarter-Final round, show its winners, otherwise (exactly 4 teams to start) these are the raw teams.
                String label = (TeamRegistry.TeamNames.length > 4) ? quarterWinners[i] : TeamRegistry.TeamNames[i];
                System.out.printf(" ".repeat(10) + "%s" + " ".repeat(15), label);
            }
    }

    public static void QuarterFinals() {
        SemiFinals();
            // Fourth row
            System.out.println();
            System.out.print(" ".repeat(7));
            for (int i = 0; i <= 3; i++) {
                System.out.printf("    ┌──────┴──────┐     " + " ".repeat(4));
            }
            //prints the third row
            System.out.println();
            System.out.print(" ".repeat(7));
            for (int i = 0; i <= 7; i++) {
                // If there was a Round of 16, show its winners, otherwise (exactly 8 teams to start) these are the raw teams.
                String label = (TeamRegistry.TeamNames.length > 8) ? round16Winners[i] : TeamRegistry.TeamNames[i];
                System.out.printf(" ".repeat(3) + "%s" + " ".repeat(8), label);
            }
    }
    public static void RoundOf16() {
        QuarterFinals();
        // Prints the second row
        System.out.println();
        System.out.print(" ".repeat(7));
        for (int i = 0; i <= 7; i++) {
            System.out.printf(" ┌──┴───┐ " + " ".repeat(4));
        }

        // Prints the bottom line
        System.out.println();
        System.out.print(" ".repeat(7));
        for (int i = 0; i <= 15; i++) {
            System.out.printf("%s" + " ".repeat(4), TeamRegistry.TeamNames[i]);
        }
        System.out.println();
        System.out.println();
    }
}