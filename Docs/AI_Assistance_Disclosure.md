# AI Assistance Disclosure — Display.java

Dear Professor,

I want to be transparent about which parts of `Display.java` I wrote myself versus where I used AI assistance (Claude, by Anthropic).


## Code I wrote myself

- `CreateTeams()`, `AddTeams()`, `EnterName()`, `ColorCheck()`, `EditTeams()`, `DeleteTeams()`, `LoadSampleTeams()`
- `getColor()` — straightforward `switch` mapping color names to ANSI codes
- `DisplayTeams()` — basic list/box printing, including the "double columns" special case for 16 teams
- The overall structure of `menu()` (menu box drawing, `do...while` loop)
- `center(word, symbol, max)` / `center(word, max)`.  Simple overloads I wrote to practice method overloading

## Code where I used AI assistance

The following are the parts I received AI help with, since they go beyond what I could confidently write and debug on my own at this point in the course:

- `buildResultBracket()` + `BracketBlock` — the recursive algorithm that renders the bracket tree (computing centers, connectors, and merging subtrees)
- `stripColor()` / `strikethrough()` — handling ANSI color codes and the terminal-compatibility workaround for showing eliminated teams
- `getConsoleWidth()` / `clearScreen()` — cross-platform terminal-size detection and screen clearing (`mode con` / `tput cols`, `cmd /c cls` / `clear`)
- `printChampionBox()` / `printMatchBox()` — the layout math for centering and sizing these boxes dynamically
- The bit-trick expressions: `(n & (n-1)) != 0`, `Integer.numberOfTrailingZeros(size)`, `31 - Integer.numberOfLeadingZeros(teamCount)`
- `roundName()`'s modern `switch` expression syntax

Thank you for your understanding. I wanted to disclose this clearly rather than leave it ambiguous.

Sincerely,
Sopanha
