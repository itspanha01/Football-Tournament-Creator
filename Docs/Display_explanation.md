# Display.java — Full Line-by-Line Explanation

This is a **console-based single-elimination football tournament bracket app**. It lets a user create/edit a list of teams (with ANSI colors), then walk through each round picking winners, while redrawing an ASCII-art bracket tree after every round, ending with a champion banner. Below is a walkthrough organized by section/method, in the order they appear.

## Imports & fields (lines 1–8)
- `import java.awt.*;` — imported but not actually used in this console app (leftover/unused import).
- `import java.util.*;` — brings in `Scanner`, `List`, `ArrayList`, `Set`, `HashSet`, etc.
- `import java.io.IOException;` — needed for the `ProcessBuilder` calls that can throw it.
- `public class Display {` — the whole program lives in one class.
- `public static Scanner scan = new Scanner(System.in);` — a single shared `Scanner` for all keyboard input, so every method can read from stdin without creating its own scanner (avoids resource conflicts).
- `public static String[] TeamNames = new String[0];` — the global team list, starts empty.
- `static int MAX_WIDTH = 32;` — the fixed width used for menu/list boxes drawn with box-drawing characters.

## Sample team data (lines 10–21)
- `SampleTeams16` — a hardcoded array of 16 country codes, each pre-colored via `getColor(...)`, so a demo run doesn't require manual typing and shows off the color feature immediately.

## `LoadSampleTeams()` (24–29)
- Copies `SampleTeams16` into `TeamNames` (`.clone()` so future edits don't mutate the constant array).
- Prints a blank line, a confirmation message with the count, another blank line.

## `DeleteTeams()` (32–52)
- `scan.nextLine();` — throws away the leftover newline sitting in the input buffer from the previous `scan.nextInt()` in the menu (classic Scanner gotcha).
- If `TeamNames` is empty, print a message and return early.
- Otherwise show the current teams (`DisplayTeams()`), ask for Y/N confirmation.
- Reads a line, strips whitespace, uppercases it.
- If `"Y"`, reset `TeamNames` to an empty array and print confirmation; otherwise print "Cancelled."

## `bracket_all()` (54–59)
- Declares four local `String` variables and does nothing with them. This is **dead code** — likely a leftover scratch/test method with no effect (it's never even called anywhere meaningfully).

## `roundName(int teamsInRound)` (62–71)
- A `switch` expression mapping how many teams remain in a round to a human label: 2→"Final", 4→"Semi-Finals", 8→"Quarter-Finals", 16→"Round of 16", 32→"Round of 32", else a generic `"Round of N"`.

## `BracketBlock` (75–79)
- A small private helper class representing one rendered piece of the bracket tree:
  - `lines` — the actual text rows for that sub-tree.
  - `width` — how wide (in visible characters) that block is.
  - `center` — the horizontal column (within the block) where its "root" connector line sits, used to align it with a sibling block.

## `stripColor(String text)` (82–84)
- Removes ANSI escape codes (pattern `\[[;\d]*m`, missing the literal ESC `` but works because it matches the digits/semicolons/`m` part following it in context) so that string-length calculations for alignment aren't thrown off by invisible color codes.

## `strikethrough(String text)` (86–92)
- Strips any existing color from a team label, then wraps it in ANSI **dim** (`[2m`) + **bright black/grey** (`[90m`), then reset. Used to visually mark eliminated teams. The comment explains why dim instead of actual strikethrough: some terminals mishandle the strikethrough SGR code and it "bleeds" onto later text.

## `printResultBracket(...)` (97–104)
- Builds the entire bracket via `buildResultBracket` starting from the whole team range `[0, n)`.
- Computes a left margin to horizontally center the bracket in the console (`consoleWidth`).
- Prints each line of the built bracket, prefixed with that margin's worth of spaces.

## `buildResultBracket(lo, hi, rounds, eliminated)` (106–174) — the recursive bracket renderer
This is the core algorithm: it recursively builds a "block" for the sub-bracket spanning teams `[lo, hi)`.
- `size = hi - lo` — how many teams this sub-tree covers.
- **Base case** (`size == 1`, a leaf/single team):
  - Get the team's label from round 0 (original leaves).
  - If eliminated, apply `strikethrough`.
  - Compute its visible width (ignoring color codes).
  - Build a one-line `BracketBlock` whose `center` is half its width (used for aligning connector lines above it).
- **Recursive case** (more than 1 team):
  - Split range in half (`mid`), recursively build `left` and `right` sub-blocks.
  - `gap = 4` — horizontal spacing between the two child blocks.
  - `leftCenter` / `rightCenterAbs` — the connector column of each child, with `right`'s shifted by `left.width + gap` since it sits to the right.
  - `combinedWidth` — total width of merging both blocks side-by-side with the gap.
  - `nodeMid` — midpoint between the two children's centers — this is where *this* node's own connector will point.
  - Build a `connector` line: start with all spaces, draw a horizontal line `─` between `leftCenter+1` and `rightCenterAbs-1`, then put a `┌` at the left child's center, `┐` at the right child's center, and `┴` at the midpoint (drawing a bracket "roof" connecting the two children up to a single point).
  - **Determine this connector's actual match result**:
    - `round = Integer.numberOfTrailingZeros(size)` — since `size` is always a power of 2, this converts size→round index (e.g. size=2 → round 1, size=4 → round 2).
    - `matchIndex = lo / size` — which match number within that round this node corresponds to.
    - Default `resultText = "■"` (an undecided/pending match marker).
    - If that round has been played (`rounds.size() > round`) and a result exists for that match index, use the actual winner's name as `resultText`, applying strikethrough if that winner later got eliminated in a further round.
  - **Center the result text** over `nodeMid`, accounting for the fact `resultText` may contain invisible ANSI codes (`visibleResultLen` uses `stripColor`). Clamp `start` so it doesn't go off either edge, then build a full-width row `resultRow` with the result text placed at that position.
  - **Merge** children lines under this node: total height is `2 + (left's line count)` (a result row + a connector row on top, then the left/right lines paired row-by-row below, joined with a `gap`-wide space).
  - Returns a new `BracketBlock` combining everything, with the new `combinedWidth` and `nodeMid` as its `center`.

This recursive divide-and-conquer approach builds the classic tournament-tree ASCII diagram bottom-up.

## `clearScreen()` (176–189)
- Detects the OS via `os.name` system property.
- On Windows: spawns `cmd /c cls` via `ProcessBuilder`, inherits IO (so its output goes straight to the console), waits for it to finish.
- On Unix-like systems: spawns `clear` the same way.
- Catches `IOException`/`InterruptedException`; on interruption, re-sets the thread's interrupt flag (good practice) and prints the stack trace.

## `getConsoleWidth()` (194–222)
- Tries to detect the real terminal width instead of guessing a fixed number.
- **Windows**: runs `cmd /c mode con`, reads its output line by line, looking for a line starting with `"columns:"`, parses the number after the colon.
- **Unix**: runs `tput cols 2>/dev/tty` (redirecting to the actual terminal device since stdout may be piped), reads the single output line, parses it as an integer.
- Any exception (missing command, parse failure, etc.) falls through silently to the `return 80;` default at the end (the classic terminal default width).

## `menu()` (224–276) — the main menu loop
- Clears the screen and defines a triple-quoted **text block** containing ASCII-art title text ("Tournament Scores" stylized banner, generated online per the comment).
- Gets console width, splits the title into lines, finds the longest line's length, computes left padding to center the whole block as one unit (preserving its internal alignment), prints each line with that same padding.
- Computes `boxPad` to center the menu box (fixed `MAX_WIDTH`), then draws a box using `┌─┐│└┘` characters with 7 menu options (`left(...)` pads each line's text to fill the box width).
- **Input loop** (`do...while`):
  - Prompts for a choice, reads an int.
  - `switch`: 1→`PlayBracket()`, 2→`CreateTeams()`, 3→`EditTeams()`, 4→`DisplayTeams()`, 5→`printMenu()` (redraws the title/box only, without starting a new input loop), 6→`DeleteTeams()`, 7→`return` (exits `menu()` immediately). Every case ends with `break`/`return`, so there's no fall-through between them.
  - (An earlier version had `case 5` calling `menu()` recursively with no `break`, which both stacked up nested input loops *and* fell through into `DeleteTeams()`. That's been fixed: the title/box drawing now lives in a separate `printMenu()` helper that `case 5` calls directly, so "Show menu" just redraws and loops instead of recursing.)
  - Loop repeats until `choice == 7` (Exit) or the `switch` itself returns out of the method.

## `PlayBracket()` (280–366) — runs the actual tournament
- Prints how many teams there currently are.
- `n = TeamNames.length`. Guards: if fewer than 2 teams, or `n` isn't a power of two (`(n & (n-1)) != 0` is the classic bit trick for "not a power of 2"), silently return (bracket requires exact powers of two).
- Clears screen, gets console width.
- Builds a `stages` list of round names from smallest to the full field, inserting each at index 0 so the list ends up ordered progressing *toward* the Final (e.g. "Round of 16 > Quarter-Finals > Semi-Finals > Final").
- Prints that stage progression, centered, joined by `"   >   "`.
- `leaves = TeamNames.clone()` — snapshot of teams (with their colors) as round-0 data.
- `rounds` — a list accumulating each round's results, starting with the leaves.
- `eliminated` — a `Set<String>` tracking losers' labels (for strikethrough rendering).
- Prints the initial (empty-result) bracket via `printResultBracket`.
- **Main round loop** (`while (current.length > 1)`):
  - Print the current round's name, centered.
  - `next` — array to hold this round's winners (half the size of `current`).
  - For each match (`i` stepping by 2 through `current`):
    - Grab the pair `a`, `b`.
    - **Input validation loop**: draw the match box (`printMatchBox`), prompt "Winner (1/2)", centered. If input isn't an int, warn and consume the bad token via `scan.nextLine()`, set `pick = -1` to loop again. Otherwise read the int; if not 1 or 2, warn and loop again.
    - Winner recorded in `next[i/2]`; loser computed and added to `eliminated`.
    - If this was the final match (`current.length == 2`), remember the loser as `runnerUp`.
    - Print "`<winner> advances!`" centered (using visible length via `stripColor`).
  - After processing all matches in the round, append `next` to `rounds`, set `current = next`.
  - Redraw the bracket with the newly-filled-in results.
- After the loop ends (only 1 team left = champion), call `printChampionBox` with the champion, runner-up, original team count, and console width.

## `TrophyArt` (369–403)
- A large ASCII-art trophy, stored as a text block constant, used purely for decoration next to the champion banner.

## `centerVisible(word, width)` (406–411)
- Same idea as `center` but computes visible length via `stripColor` first (so ANSI-colored strings center correctly), then pads left/right with spaces.

## `printChampionBox(...)` (414–463)
- Splits `TrophyArt` into lines and trims leading/trailing blank lines so the art sits snugly (rather than leaving big gaps from the text block's indentation).
- Computes `trophyWidth` as the max line length among the trimmed trophy lines.
- Builds display strings: title `" * CHAMPION * "`, `champLine` with the champion's name, `runnerUpLine` (empty string if no runner-up), and `rounds`/`statsLine` — `rounds = 31 - Integer.numberOfLeadingZeros(teamCount)` is another bit trick computing `log2(teamCount)` (number of rounds played).
- `inner` — the box's interior width: at least 60, or big enough to fit the longest of the three text lines plus 8 padding chars.
- Builds `boxLines[]` — a `╔═╗ ║ ║ ╚═╝` styled box with the title centered on top, blank padding rows, then the champion line, blank row, runner-up line, stats line, blank row, bottom border.
- `boxWidth = inner + 2` (interior plus the two border characters).
- Computes vertical centering (`trophyTopPad`, `boxTopPad`) so the shorter of the two blocks (trophy art vs. box) is vertically centered against the taller one when placed side by side.
- `totalWidth` — trophy width + gap (2) + box width; used to compute horizontal centering pad for the whole combined display.
- Loops over `height` rows, picking the correct trophy line and box line for each row (or empty string if outside that block's vertical range), pads the trophy cell to `trophyWidth`, and prints `pad + trophyCell + gapSpaces + boxLine` — effectively laying the trophy art and champion box side-by-side.

## `printMatchBox(matchNum, a, b, consoleWidth)` (466–475)
- Builds the match's content string: 9 spaces of indent then `"[1] <team a>    [2] <team b>"`.
- `inner` — box interior width: at least 36, or the visible length of `content` if longer.
- `title` — `" Match N "`.
- `pad` — horizontal centering offset for the whole box.
- Prints top border with the title centered inside dashes, then the content line (left-aligned/padded to fill), then the bottom border.

## `left(word, max)` (478–483)
- Left-aligns `word` within `max` columns by strip-measuring visible length (regex removes ANSI codes) and appending spaces to pad it out. Comment notes this specific fix (`[ AI ]`) was needed so padding lines up correctly around colored text.

## `center(word, symbol, max)` (486–491)
- Overload that centers `word` within `max` width using an arbitrary fill `symbol` string (e.g. `"─"` for box-drawing borders) — note it does *not* strip ANSI codes here, so this variant is only safe for plain (uncolored) text.

## `center(word, max)` (494–499)
- Overload (via **method overloading**, per its comment) that centers using plain spaces instead of a custom symbol — same caveat about not stripping color codes.

## `DisplayTeams()` (502–536)
- If no teams: prints an empty bordered box saying "(Empty list)".
- If exactly 16 teams: prints them in **two columns** (index `i` from 0–7 paired with `i+j-1` where `j=9`, i.e. team 1 next to team 9, team 2 next to team 10, etc.) — a special-cased layout just for the common 16-team case.
- Otherwise (any other non-zero count): prints one team per line, numbered.
- Ends with a closing border and blank line in each branch.

## `getColor(text, color)` (538–562)
- Defines local ANSI color constants (`RED`, `GREEN`, `YELLOW`, a brighter `BLUE` since standard blue is too dark to read, `PURPLE`, `CYAN`, `RESET`).
- Strips any existing ANSI codes from `text` first (so re-coloring doesn't stack escape sequences).
- `switch` on the `color` string name, wrapping `text` in the matching color code + reset. If `color` doesn't match any case (e.g. `"DEFAULT"`), `text` is returned uncolored (just the stripped plain text).
- Returns the (possibly) colored string.

## `CreateTeams()` (564–614)
- Consumes leftover newline from the menu's `nextInt()`.
- If teams already exist, confirms overwrite (Y/N) before proceeding; cancels otherwise.
- Asks user to choose: 1 = manual entry, 2 = load sample teams (loops until valid input).
- If sample mode chosen, calls `LoadSampleTeams()` and returns.
- Otherwise, loops asking for team count until it's one of 2/4/8/16, then allocates `TeamNames` of that size and calls `AddTeams(...)` to fill it in.

## `AddTeams(length)` (616–635)
- Prompts once for the abbreviation format.
- For each slot: gets a validated name via `EnterName`, then applies a color via `ColorCheck`, storing the final colored string into `TeamNames[i]`.
- Prints a summary of how many teams were added.

## `EnterName(count)` (637–671)
- Validation loop: prompts for team `[count]`, reads/trims/uppercases input.
- Checks empty, too short (`<3`), too long (`>3` — note this isn't `else if`, so both length checks could theoretically fire, but they're mutually exclusive by value anyway).
- Checks for duplicates against existing `TeamNames`, stripping ANSI codes before comparing so a colored existing name still matches a plain new one.
- Loops until valid; returns the accepted 3-letter code.

## `ColorCheck(Abr)` (673–695)
- Loop: shows a colored prompt (each color swatch `■` rendered in its own color) asking the user to pick R/Y/G/B/P/Default (by full word or single letter).
- Applies `getColor` to wrap `Abr` in the chosen color (note `"DEFAULT"`/`"D"` calls `getColor(Abr, "default")` — lowercase, which won't match any `case` in `getColor`'s switch, so it just returns the stripped/uncolored text — functionally correct but relies on `getColor`'s fallthrough behavior rather than an explicit path).
- Invalid input reprints "Invalid" and loops again.
- Returns the (possibly) colored abbreviation.

## `EditTeams()` (697–764)
- Displays current teams.
- Loop: if list is empty, message and breaks.
- Draws a small sub-menu: 1. Edit names, 2. Edit colors, 3. Exit.
- **Choice 1** (edit name): asks for an index, consumes buffer, validates range is `0..TeamNames.length` (note: this allows `index == 0` — off-by-one leniency, and later uses `index-1` to store, so entering `0` would underflow to `-1` and throw `ArrayIndexOutOfBoundsException` — a latent bug), prompts for a new validated name via `EnterName`/`ColorCheck`, stores at `TeamNames[index-1]`, confirms, redisplays.
- **Choice 2** (edit color): asks for index, decrements immediately to 0-based, validates `0 <= index < length` (this one is correctly bounded), re-colors that team via `ColorCheck`, confirms, redisplays.
- **Choice 3**: exits the loop.
- Any other input: "Invalid operation." and loops again.

---

## Summary

It's an interactive terminal app for running a single-elimination bracket (2/4/8/16 teams): you build a team list (manually with colors, or load a preset), optionally edit it, then play through the bracket picking a winner for each match while it redraws a live ASCII-art tournament tree (with eliminated teams struck through and byes shown as `■`), and finishes with a decorated champion/runner-up banner next to trophy ASCII art. It auto-detects and centers everything to the real terminal width and OS (Windows vs Unix) for `clear`/width detection.

### Likely bugs worth flagging
- `menu()` case `5` falls through into `DeleteTeams()` (no `break`).
- `bracket_all()` is dead code.
- `EditTeams()`'s name-edit path allows index `0`, which would crash on `TeamNames[-1]`.
