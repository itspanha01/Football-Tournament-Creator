# How This Program Works — A Beginner's Guide

This document explains the Football Tournament Creator in plain language, assuming you're still learning Java. It walks through what each piece does and why, using simple analogies where it helps.

## 1. The Big picture

This is a **console app** (it runs in a terminal window, no graphics) that lets you:
1. Type in a list of football teams (like "ARG", "BRA", "ESP"...) and give each one a color.
2. Pick winners match-by-match through a single-elimination bracket (like a World Cup knockout stage).
3. See an ASCII-art tournament tree redraw itself after every round, with a trophy screen at the end for the champion.

The console app is split across **six files**, and each one has one job:

| File | Job |
|---|---|
| `App.java` | The "on switch" — starts the program |
| `Display.java` | The main menu — draws it and routes each choice to the right class |
| `TeamRegistry.java` | The team manager — create, edit, delete, and show teams |
| `BracketRenderer.java` | The tournament engine — runs the matches and draws the bracket tree |
| `ConsoleUtil.java` | Shared console helpers — clearing the screen, measuring width, padding/centering text |
| `ColorUtil.java` | Wraps a team name in ANSI color codes |

There are two other files in the folder worth knowing about but not covered in depth here:
- `DisplayV2.java` — an earlier, all-in-one draft of the menu/team/bracket logic. It's no longer called from anywhere (`App.java` uses `Display`, not `DisplayV2`); it's just kept around as a before/after reference for how the code looked before it was split up.
- `AppGUI.java` / `TournamentGUI.java` — a separate **graphical** (Swing) version of the same idea, with buttons and windows instead of typing numbers into a terminal. It doesn't share code with the console version — it's a different `main()` entry point entirely.

Splitting a program into multiple files/classes like this is called **separation of concerns**: each class only worries about its own job, instead of one giant file trying to do everything.

## 2. `App.java` — where it all begins

```java
public class App {
    public static void main(String[] args) {
        Display.menu();
    }
}
```

Every Java program needs a `main` method — it's the very first thing that runs. Here, `main` does one thing: call `Display.menu()`. That's it. This file is intentionally tiny; its only job is to kick things off.

## 3. `Display.java` — the main menu

`Display` no longer holds the Scanner or the alignment helpers itself — those moved out to `TeamRegistry` and `ConsoleUtil` respectively (see below). Its own job is just to draw the menu and route the user's choice.

### `menu()`
This method:
1. Clears the screen (`ConsoleUtil.clearScreen()`) and prints the ASCII-art title, centered to the real terminal width.
2. Draws a box listing the 7 options (Play bracket, Create teams, etc.).
3. Enters a loop (`do { ... } while (choice != 7)`) that keeps asking "Choose operation" and running the matching action, until the user picks `7` (Exit).

A `do...while` loop is just a loop that always runs its body **at least once** before checking the condition — perfect here, since we always want to show the menu once before deciding whether to keep going.

The `switch` statement is how it decides what to do with the number the user typed:
```java
switch (choice) {
    case 1 : BracketRenderer.PlayBracket(); break;
    case 2 : TeamRegistry.CreateTeams(); break;
    ...
}
```
Each `case` matches one menu number to one action, and each ends with `break;` — without it, Java would keep running the *next* case too. (An earlier version of this method had `case 5` missing its `break`, which made it silently fall through into `case 6`'s `DeleteTeams()` — a real bug that got fixed during this project.)

## 4. `ConsoleUtil.java` — shared console tools

This class exists so that "how do I clear the screen" or "how do I pad this text" is written **once** and reused everywhere, instead of copy-pasted into every file that needs it.

- `clearScreen()` — runs the OS's own clear command (`cls` on Windows, `clear` on Linux/macOS) via `ProcessBuilder`, since Java has no built-in "clear the terminal" call.
- `getConsoleWidth()` — asks the OS how wide the terminal window actually is (`mode con` on Windows, `tput cols` on Unix), falling back to `80` columns if it can't tell. This is what lets titles and boxes stay centered no matter how big your terminal window is.
- `left(word, max)` — pads spaces only on the right, so text lines up against the left edge (used inside boxes).
- `center(word, max)` / `center(word, symbol, max)` — centers text in a `max`-character-wide line, either with plain spaces or with a repeated symbol (like `─`) as filler. These are two versions of the same method name with different parameters — this is called **method overloading**: Java picks the right one based on what arguments you pass.
- `centerVisible(word, width)` — same idea as `center`, but for text that might contain **hidden color codes** (see below).
- `stripColor(text)` — removes ANSI color codes from a string, used purely to *measure* a team name's real on-screen length.
- `strikethrough(text)` — used for eliminated teams: strips whatever color a team had and repaints it dim grey.

One tricky detail: team names can have hidden color codes baked into the string (invisible characters that tell the terminal "make this text red"). If you just used `word.length()`, it would count those invisible characters too and throw off the padding. That's why `stripColor()` exists — it removes those hidden codes just for the purpose of measuring length, without changing what actually gets printed.

## 5. `ColorUtil.java` — painting team names

```java
public static String getColor(String text, String color) {
    String RED = "[31m";
    ...
    return RED + text + RESET;
}
```
Terminals understand special invisible character sequences (called **ANSI escape codes**) that mean "start printing in red" or "stop coloring." So a "red ARG" is really just the string: `[invisible red code]ARG[invisible reset code]`. When printed, the terminal shows red "ARG"; as plain text, you'd see the color codes mixed in with the letters.

Before applying a new color, `getColor()` first strips out any color codes already in `text` — that's what lets you **re-color** a team later (in `EditTeams()`) instead of stacking codes on top of each other.

## 6. `TeamRegistry.java` — managing the team list

### The team list itself
```java
public static Scanner scan = new Scanner(System.in);
public static String[] TeamNames = new String[0];
```
`scan` reads whatever the user types on the keyboard. Since almost every part of the program needs to ask the user something (a team name, a color, a match winner), this one `Scanner` is shared by everyone (`TeamRegistry.scan`), instead of each method creating its own.

`TeamNames` is an **array of Strings** — one string per team (e.g. `"ARG"`, already wrapped in its color codes). It starts empty (`new String[0]` means "an array with zero slots"). Every method in this file reads from or writes to this same array.

### `SampleTeams16` and `LoadSampleTeams()`
A ready-made list of 16 colored team names, so you can jump straight into `PlayBracket()` while testing instead of typing 16 names by hand every time.

### Creating teams: `CreateTeams()`, `AddTeams()`, `EnterName()`, `ColorCheck()`
These methods work together like an assembly line:
1. `CreateTeams()` warns before overwriting an existing list, then lets you choose manual entry or the sample list. For manual entry it asks "how many teams?" (must be 2, 4, 8, or 16 — powers of two, since it's a knockout bracket) and hands off to `AddTeams()`.
2. `AddTeams()` loops once per team slot, calling `EnterName()` then `ColorCheck()` for each one.
3. `EnterName()` asks for a 3-letter code and keeps asking until it's exactly 3 letters and not already used (comparing against the *stripped*, colorless version of existing names).
4. `ColorCheck()` asks which color to paint that team's name and wraps it using `ColorUtil.getColor()`.

This is a common pattern in programming: break a big task ("set up N teams") into small, single-purpose helper methods that call each other.

### `EditTeams()` and `DeleteTeams()`
These let you change a team's name/color after creation, or wipe the whole list (with a "are you sure?" confirmation first, since deleting is hard to undo).

### `DisplayTeams()`
Prints the current team list inside a box. It has a special case: if there are exactly 16 teams, it prints them in **two columns** instead of one long list, so they fit better on screen.

## 7. `BracketRenderer.java` — running the tournament

This is the most advanced file, so let's take it slowly.

### The core idea: rounds
A knockout tournament is really just **repeatedly cutting the field in half**:
- 16 teams → 8 winners (Round of 16)
- 8 → 4 (Quarter-Finals)
- 4 → 2 (Semi-Finals)
- 2 → 1 (Final → Champion!)

`roundName(teamsInRound)` maps how many teams are left in a round to its stage name (`"Final"`, `"Semi-Finals"`, etc.), using a `switch` **expression** (notice it uses `->` and directly returns a value, rather than the older `case ... : ... break;` style used in `Display.java`).

`PlayBracket()` runs a loop that keeps halving the team list until only one team is left:
```java
while (current.length > 1) {
    // ask for a winner in every match this round
    // build "next" — the array of winners
    current = next;
}
```

### Picking a winner, safely
For every pair of teams, this loop keeps asking until the user types `1` or `2`:
```java
do {
    // print the match box, ask "Winner (1/2)?"
    ...
} while (pick != 1 && pick != 2);
```
It also checks `scan.hasNextInt()` before reading — this protects against the user typing something that *isn't* a number at all (like accidentally hitting a letter key), which would otherwise crash the program.

### Remembering who lost: the `eliminated` Set
```java
java.util.Set<String> eliminated = new java.util.HashSet<>();
```
A `Set` is like a list, but it only cares about *"is this thing in here or not?"* — no duplicates, no particular order. Every time someone loses a match, their name goes into `eliminated`. Later, when drawing the bracket, if a team's name is in this set, `ConsoleUtil.strikethrough()` redraws it in dim grey instead of its normal color — that's how you can visually tell who's been knocked out.

### Drawing the bracket tree: `buildResultBracket()` (the tricky part)
This is the most complex method in the whole program, so don't worry if it takes a few reads to click. Here's the idea, in plain English:

Imagine you have 8 teams. To draw their bracket:
1. Split them into a left half (4 teams) and a right half (4 teams).
2. Draw *each half's* bracket the same way — split it again into two groups of 2, then those into single teams.
3. Once you're down to a **single team** (this is the simplest case, called the **base case**), just print its name — nothing more to split.
4. As you work back up, connect each pair of halves with a little line drawing (`┌─┴─┐`-style connectors) and show the winner of that matchup in the middle (or a `■` block if that match hasn't been played yet).

This technique — a method that solves a big problem by splitting it into smaller versions of the *same* problem, then combining the results — is called **recursion**. The method calls itself:
```java
private static BracketBlock buildResultBracket(int lo, int hi, ...) {
    if (size == 1) {
        return /* just this one team */;
    }
    BracketBlock left = buildResultBracket(lo, mid, ...);   // solve the left half
    BracketBlock right = buildResultBracket(mid, hi, ...);  // solve the right half
    // combine left + right into one bigger picture
}
```
Every recursive method needs a **base case** (the "stop splitting, just answer directly" rule — here, `size == 1`) or it would call itself forever. `BracketBlock` is a small private helper class that bundles together the rendered lines of a sub-bracket, its total width, and where its connector sits — everything the parent call needs to stitch two halves together.

### The champion screen: `printChampionBox()`
Once the `while` loop in `PlayBracket()` finishes (only one team left), this method draws a decorative box with the champion's name, runner-up, and some stats, plus an ASCII-art trophy (`TrophyArt`) next to it. It's mostly about careful spacing math — lining up two blocks of text (the trophy art and the box) side by side, even though they're different heights.

## 8. How it all connects — a quick walkthrough

1. `App.main()` calls `Display.menu()`.
2. `Display.menu()` shows the menu and waits for a number.
3. User picks `2` → `TeamRegistry.CreateTeams()` runs, asks questions, fills `TeamRegistry.TeamNames`.
4. Control returns to the menu loop; user picks `1` → `BracketRenderer.PlayBracket()` runs.
5. `PlayBracket()` reads `TeamRegistry.TeamNames`, runs round after round, asking for winners and redrawing the bracket via `buildResultBracket()`.
6. When one team remains, `printChampionBox()` shows the winner.
7. Control returns to the menu; user picks `7` → the loop ends, the program exits.

## 9. Java concepts you'll see used here (glossary)

- **`static`** — means "belongs to the class itself, not to any one object of it." That's why you can call `TeamRegistry.CreateTeams()` without ever writing `new TeamRegistry()` first.
- **Array (`String[]`)** — a fixed-size list of values of the same type, accessed by index (`TeamNames[0]` is the first team).
- **`Scanner`** — reads text typed by the user.
- **`do...while` loop** — runs the body once, *then* checks whether to repeat.
- **`switch` statement / expression** — picks one branch to run (or one value to return) based on a value, like a multiple-choice menu. `Display.java` uses the classic `case ... : ... break;` form; `BracketRenderer.roundName()` and `ColorUtil`/`TeamRegistry`'s color logic use the newer `case ... ->` form, which doesn't need `break;`.
- **Method overloading** — having two methods with the same name but different parameters (like `ConsoleUtil.center(word, max)` vs. `center(word, symbol, max)`); Java picks the right one based on the arguments you pass.
- **`Set`** — a collection with no duplicates, good for "have I seen this before?" questions.
- **Recursion** — a method that calls itself on a smaller piece of the problem, with a base case to stop.
- **ANSI escape codes** — invisible character sequences that tell the terminal to change text color; they add characters to a string that don't show up as visible letters, which is why length-based math has to strip them out first (`ConsoleUtil.stripColor()`).
