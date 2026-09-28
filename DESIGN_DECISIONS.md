# Design decisions

## Assumptions

Where the spec is open. The basis is in brackets: the spec, a provided test, or our choice.

### DEPLOY and the report

- Commands before the first successful DEPLOY are ignored, including REPORT. [spec; `LeadingCommands_Ignore`]
- A failed DEPLOY still reports its error. A failed redeploy leaves the robot where it was, and pits stay on the board. [`DeployInvalid_Ignores`]
- `...: Ignored` rejections go into the report. Everything else that is dropped goes to `System.err`. [tests]

### PIT window

- A successful DEPLOY opens it. MOVE, LEFT and RIGHT close it, even when the MOVE itself is rejected. [spec; `PitOnlyAfterDeploy_Ignores`]
- A failed DEPLOY leaves it as it was. [not closing: `DeployInvalid_Ignores`; not opening: choice]
- REPORT and unparseable lines leave it open. [spec: REPORT "can happen at any time"]
- A PIT outside the window or the zone, or on an existing pit, is ignored and logged. The window is checked first, so a PIT on the robot after a MOVE isn't `ROBOT Detected`. [choice]

### Input

- Parsing is strict: upper-case keywords and exactly `DEPLOY X,Y,F` / `PIT X,Y`. Only leading and trailing whitespace is ignored. Blank lines and numbers too large for an `int` are parse failures. Negative coordinates give `Outside Zone`. [choice]
- The input is UTF-8. A leading byte-order mark is ignored, `\r\n` works, and a last line without `\n` is still processed. [choice]
- Every dropped line is logged with its line number, e.g. `Line 6: 'PIT 0,1' ignored: PIT must follow a DEPLOY or another PIT`. [choice; the spec only requires parse failures]
- Each `process()` call starts on an empty board and leaves the input stream open. [`ChangeReport_ShouldBeImmutable`]

## Changes to provided code

- `TestRobotSimulator` is renamed `RobotSimulatorTest`. Our `RobotSimulator` tests are added to it, and every test has a `@DisplayName`.
- **Fixed tests:**
  - Swapped `assertArrayEquals` arguments in three tests.
  - `ChangeReport_ShouldBeImmutable` now really asserts immutability. It used to swallow the exception.
- A TODO on `ChangeReport_ShouldBeImmutable`'s `reset()` check: closing a `ByteArrayInputStream` does nothing, so that check can never fail. `doesNotCloseInput` covers the requirement.
- `RobotSimulator(PrintStream)` added, so tests can capture the error log.
- `build.gradle`: Java 21 (`options.release`) and JaCoCo.

## Design

- **Layers:**
  - `RobotSimulator` handles input and output, and logs dropped lines.
  - `CommandParser` turns text into a `Command`.
  - `Simulation` holds the rules and state.

  They live in the packages `command`, `simulation` and `model`, and dependencies only go one way. `RobotSimulator` stays in the default package, where the provided test expects it.
- **Sealed `Command` with records**, handled in an exhaustive `switch`: adding a command without handling it won't compile.
- **Mutable `Robot`:** `Simulation` checks `nextPosition()` before calling `move()`. A redeploy moves the same robot.
- **Checked exceptions** for dropped lines (`CommandParseException`, `CommandIgnoredException`), logged in one place. The spec's rejections are normal outcomes (the `Rejection` enum), not exceptions.
- **Streaming:** input is read line by line, and the report is returned as an unmodifiable view, not a copy.
- `Zone` has a width and a height. REPORT output uses `Locale.ROOT`.
- Each test we added was checked by temporarily breaking the code it targets.

## Known limitations

- Each line is read whole, so a huge input with no `\n` could run out of memory.
