// Renders the ASCII tournament bracket and drives the console "play the bracket" flow.
public class BracketRenderer {

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

    // Renders the bracket tree with known winners filled in at each connector
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
                label = ConsoleUtil.strikethrough(label);
            }
            int visibleWidth = ConsoleUtil.stripColor(label).length();
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
                    resultText = ConsoleUtil.strikethrough(resultText);
                }
            }
        }

        // Center resultText (which may carry a team's ANSI color codes) using its
        // visible length, so hidden color codes don't throw off column alignment.
        int visibleResultLen = ConsoleUtil.stripColor(resultText).length();
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

    // Lets the user pick the winner of every match, round by round, redrawing
    // the ASCII bracket tree with results filled in after each round.
    public static void PlayBracket() {
        System.out.printf("You currently have %d teams.%n", TeamRegistry.TeamNames.length);
        System.out.println();

        int n = TeamRegistry.TeamNames.length;
        if (n < 2 || (n & (n - 1)) != 0) {
            return;
        }

        ConsoleUtil.clearScreen();

        int consoleWidth = ConsoleUtil.getConsoleWidth();

        // Stage labels, left-to-right in the order teams progress toward the Final
        java.util.List<String> stages = new java.util.ArrayList<>();
        for (int round = 2; round <= n; round *= 2) {
            stages.add(0, roundName(round));
        }
        System.out.println();
        System.out.println(ConsoleUtil.center(String.join("   >   ", stages), consoleWidth));
        System.out.println();

        String[] leaves = TeamRegistry.TeamNames.clone(); // keep each team's assigned color

        java.util.List<String[]> rounds = new java.util.ArrayList<>();
        rounds.add(leaves);

        java.util.Set<String> eliminated = new java.util.HashSet<>();

        System.out.println("Bracket:");
        printResultBracket(rounds, n, consoleWidth, eliminated);
        System.out.println();

        String[] current = leaves;
        String runnerUp = null;
        while (current.length > 1) {
            System.out.println(ConsoleUtil.center(roundName(current.length), consoleWidth));
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

                    if (!TeamRegistry.scan.hasNextInt()) {
                        System.out.println("Please enter 1 or 2.");
                        TeamRegistry.scan.nextLine();
                        pick = -1;
                        continue;
                    }
                    pick = TeamRegistry.scan.nextInt();

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
                int advancePad = Math.max(0, (consoleWidth - ConsoleUtil.stripColor(advanceMsg).length()) / 2);
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

        int inner = Math.max(60, Math.max(ConsoleUtil.stripColor(champLine).length(),
                Math.max(ConsoleUtil.stripColor(runnerUpLine).length(), statsLine.length())) + 8);

        String[] boxLines = {
            "╔" + ConsoleUtil.center(title, "═", inner) + "╗",
            "║" + " ".repeat(inner) + "║",
            "║" + ConsoleUtil.centerVisible(champLine, inner) + "║",
            "║" + " ".repeat(inner) + "║",
            "║" + ConsoleUtil.centerVisible(runnerUpLine, inner) + "║",
            "║" + ConsoleUtil.center(statsLine, inner) + "║",
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
        int inner = Math.max(36, ConsoleUtil.stripColor(content).length());
        String title = " Match " + matchNum + " ";
        String pad = " ".repeat(Math.max(0, (consoleWidth - (inner + 2)) / 2));

        System.out.println(pad + "┌" + ConsoleUtil.center(title, "─", inner) + "┐");
        System.out.println(pad + "│" + ConsoleUtil.left(content, inner) + "│");
        System.out.println(pad + "└" + "─".repeat(inner) + "┘");
    }
}
