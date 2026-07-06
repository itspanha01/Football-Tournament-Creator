# Display.java — Program Logic Flowchart

```mermaid
flowchart TD
    Start([Program Start]) --> Menu[menu]
    Menu --> Choice{Choose operation}

    Choice -->|1| PlayBracket[PlayBracket]
    Choice -->|2| CreateTeams[CreateTeams]
    Choice -->|3| EditTeams[EditTeams]
    Choice -->|4| DisplayTeams[DisplayTeams]
    Choice -->|5| Menu
    Choice -->|6| DeleteTeams[DeleteTeams]
    Choice -->|7| Exit([Exit])

    %% ---- Create Teams ----
    CreateTeams --> TeamsExist{Teams already exist?}
    TeamsExist -->|Yes| ConfirmOverwrite{Overwrite? Y/N}
    ConfirmOverwrite -->|N| Menu
    ConfirmOverwrite -->|Y| ModeChoice
    TeamsExist -->|No| ModeChoice{Manual or Sample?}

    ModeChoice -->|Sample| LoadSample[LoadSampleTeams] --> Menu
    ModeChoice -->|Manual| PickCount[Select team count: 2 / 4 / 8 / 16]
    PickCount --> AddTeams[AddTeams loop]
    AddTeams --> EnterName[EnterName - validate length/duplicates]
    EnterName --> ColorCheck1[ColorCheck - assign color]
    ColorCheck1 --> StoreTeam[Store in TeamNames array]
    StoreTeam -->|more slots| AddTeams
    StoreTeam -->|done| Menu

    %% ---- Edit Teams ----
    EditTeams --> EditChoice{Edit names / colors / exit}
    EditChoice -->|1 Names| EditName[EnterName + ColorCheck -> update index] --> EditTeams
    EditChoice -->|2 Colors| EditColor[ColorCheck -> update index] --> EditTeams
    EditChoice -->|3 Exit| Menu

    %% ---- Delete Teams ----
    DeleteTeams --> DeleteEmptyCheck{TeamNames empty?}
    DeleteEmptyCheck -->|Yes| Menu
    DeleteEmptyCheck -->|No| ConfirmDelete{Confirm delete? Y/N}
    ConfirmDelete -->|Y| ClearTeams[Reset TeamNames to empty] --> Menu
    ConfirmDelete -->|N| Menu

    %% ---- Display Teams ----
    DisplayTeams --> Menu

    %% ---- Play Bracket ----
    PlayBracket --> ValidCheck{n >= 2 AND power of 2?}
    ValidCheck -->|No| Menu
    ValidCheck -->|Yes| InitState[rounds = leaves, eliminated = empty set]
    InitState --> PrintBracket0[printResultBracket - empty results]
    PrintBracket0 --> RoundLoop{current round size > 1?}

    RoundLoop -->|Yes| MatchLoop[For each match pair in round]
    MatchLoop --> ShowMatch[printMatchBox]
    ShowMatch --> PromptWinner[Prompt Winner 1/2]
    PromptWinner --> ValidInput{Valid input? 1 or 2}
    ValidInput -->|No| PromptWinner
    ValidInput -->|Yes| RecordResult[Record winner in next-round array;\nadd loser to eliminated set]
    RecordResult --> MoreMatches{More matches this round?}
    MoreMatches -->|Yes| MatchLoop
    MoreMatches -->|No| AppendRound[rounds.add next round results]
    AppendRound --> PrintBracketN[printResultBracket - updated results]
    PrintBracketN --> RoundLoop

    RoundLoop -->|No: 1 team left| Champion[printChampionBox\nchampion + runner-up + trophy art]
    Champion --> Menu
```

## Notes on the diagram

- Every branch loops back to `Menu`, matching the actual code: each top-level action (`PlayBracket`, `CreateTeams`, `EditTeams`, `DisplayTeams`, `DeleteTeams`) returns control to the `do...while` loop in `menu()`.
- `buildResultBracket()`'s internal recursion (splitting the team range in half, rendering connectors, merging left/right subtrees) is collapsed into the single `printResultBracket` boxes above — it's called after every round to redraw the whole tree with newly-known results.
- The **bug noted in the disclosure/explanation docs** — `menu()`'s case `5` falling through into case `6` (`DeleteTeams`) because of a missing `break` — is not shown here, since this diagram represents the *intended* logic rather than that specific fall-through defect.
