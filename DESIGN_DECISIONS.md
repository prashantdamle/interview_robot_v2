# Design decisions

## Assumptions

The spec leaves some behaviour open. Each assumption below is based on the spec's wording, on what a provided test
implies, or, where neither settles it, on our own choice.

| # | Assumption | Basis |
| --- | ---------- | ----- |
| 1 | The returned report holds `REPORT` lines and the three `...: Ignored` messages, in order. Parse failures and dropped commands go to `System.err`, never to the report. | Provided tests expect the `Ignored` messages in the returned list; the spec sends parse failures to `System.err`. |
| 2 | Until a DEPLOY succeeds, every other command is ignored, including `REPORT`, which then outputs nothing. | Spec: "Commands before this can be safely ignored"; `testRobotSimulator_LeadingCommands_Ignore` expects no output for a leading `REPORT`. |
| 3 | A DEPLOY that fails before any robot exists still reports its error. | `testRobotSimulator_DeployInvalid_Ignores` and `testRobotSimulator_PitOnlyAfterDeploy_Ignores` both start with `Outside Zone: Ignored`. |
| 4 | A failed redeploy leaves the robot where it was, and pits stay on the board across redeploys. | `testRobotSimulator_DeployInvalid_Ignores`; spec: DEPLOY "redeploys the same robot". |
| 5 | **PIT window.** A PIT is accepted only while the window is open. A successful DEPLOY opens it; MOVE, LEFT and RIGHT close it, even when the MOVE itself is rejected. REPORT and lines that fail to parse leave it unchanged. | Spec: PIT must "follow a DEPLOY command and precede MOVE, LEFT, RIGHT" and REPORT "can happen at any time"; `testRobotSimulator_PitOnlyAfterDeploy_Ignores` ignores a PIT after a rejected MOVE. |
| 6 | A failed DEPLOY leaves the PIT window as it was: it doesn't close an open window, and doesn't open a closed one. | Not closing: `testRobotSimulator_DeployInvalid_Ignores` accepts a PIT after a failed DEPLOY. Not opening: our choice; no provided test covers it. |
| 7 | A PIT after a failed DEPLOY with no robot yet is ignored. | Our choice, following assumption 2. `testRobotSimulator_PitOnlyAfterDeploy_Ignores` has this case but can't observe the outcome. |
| 8 | A PIT outside the zone and a PIT where one already exists are ignored and logged, with no report line. | Our choice: the spec's `Outside Zone` message is for "deploy/move" only, and it names no message for duplicates. |
| 9 | Every dropped command is logged to `System.err` with its line number, not just parse failures, e.g. `Line 6: 'PIT 0,1' ignored: PIT must follow a DEPLOY or another PIT`. | Our choice: the spec only asks for parse failures, but one consistent log makes every dropped line traceable to the input. |
| 10 | Parsing is strict: upper-case keywords and exactly `DEPLOY X,Y,F` / `PIT X,Y`. Only leading and trailing whitespace is ignored. Blank lines are parse failures. | Our choice; the spec only shows this exact form. |
| 11 | Negative coordinates parse, then fail with `Outside Zone`. Coordinates too large for an `int` are parse failures. | Our choice. |
| 12 | Each `process()` call starts on an empty board, and the input stream is left open for the caller. | `testRobotSimulator_ChangeReport_ShouldBeImmutable` calls `process()` twice on one instance and resets the stream in between. |
| 13 | Input is read as UTF-8. | Our choice; the spec doesn't name an encoding. |

## Changes to provided code

| Change | Why |
| ------ | --- |
| `TestRobotSimulator` renamed to `RobotSimulatorTest`, and our extra `RobotSimulator` tests added to it | Keeps all `RobotSimulator` tests in one conventionally named class. The provided test methods are unchanged apart from the fixes below. |
| `assertArrayEquals` arguments swapped in `testRobotSimulator_MoveOffBoard_Ignores`, `testRobotSimulator_InvalidCommands_Ignores` and `testRobotSimulator_HitAllWalls_Ignores` | They passed `(actual, expected)`, so failure messages would have shown our output as the expected value. |
| `testRobotSimulator_ChangeReport_ShouldBeImmutable` now asserts `add()` throws `UnsupportedOperationException` | It caught any exception from `add()` and printed a message, so it could never fail on a mutable list. |
| `@DisplayName` added to each provided test | Every test in the project describes what it checks in the same way; the method names are kept. |
| TODO added in `testRobotSimulator_ChangeReport_ShouldBeImmutable` on the `reset()` check | Closing a `ByteArrayInputStream` has no effect, so that check can never fail. `doesNotCloseInput` covers the requirement properly. |
| `RobotSimulator`: implemented `process()`; added a `RobotSimulator(PrintStream)` constructor | The constructor lets tests capture the error log without replacing `System.err`. The no-argument constructor still logs to `System.err`. |
| `build.gradle`: `options.release = 21` | Pins the language level (records, sealed interfaces, pattern-matching `switch`) while still building on any newer JDK. |
| Added `.gitignore`; `gradlew` marked executable | Build output and IDE files aren't tracked, and `./gradlew` runs after unzipping. |

## Design

- **Three layers:**
  - `RobotSimulator` handles input and output. It reads lines, logs failures with line numbers, and returns the report.
  - `CommandParser` turns a line of text into a `Command`.
  - `Simulation` holds the rules and the state of a single run.

  `Simulation` never sees text or streams, so each rule is tested directly with commands in `SimulationTest`.
- **Packages** under `com.nuix.interview.robot`. Dependencies only go one way, down this list:
  - `simulation`: the rules and their outcomes (`Simulation`, `Rejection`, `CommandIgnoredException`).
  - `command`: parsing and the commands (`CommandParser`, `Command` and its records, `CommandParseException`).
  - `model`: the board (`Direction`, `Position`, `Robot`, `Zone`).

  `Rejection` is package-private because only `Simulation` uses it. `RobotSimulator` stays in the default
  package, where the provided test expects it.
- **Commands** are records implementing a sealed `Command` interface. `Simulation` handles them in an exhaustive
  `switch`, so a new command that isn't handled fails to compile.
- **Robot** is mutable, like the one physical rover on the board. It knows nothing of the zone or pits:
  `Simulation` checks `robot.nextPosition()` and only then calls `robot.move()`. A redeploy moves the same robot
  (`deployTo`) rather than creating a new one.
- **Zone** has a width and a height, although the spec's zone is 10x10, so it isn't tied to a square.
- **Two checked exceptions:**
  - `CommandParseException` for malformed lines.
  - `CommandIgnoredException` for well-formed commands the rules drop.

  Both are handled in one place, in `RobotSimulator`, which logs them and continues. The three spec rejections
  (`Outside Zone`, `PIT Detected`, `ROBOT Detected`) aren't exceptions: they're normal outcomes and go into the
  report, from the `Rejection` enum.
- **Memory:** input is streamed line by line, and the report is returned as an unmodifiable view rather than a
  copy, so the 1M-line test doesn't hold the whole input or a second list in memory.
- **`REPORT` formatting** uses `Locale.ROOT`, so the digits are ASCII whatever the JVM's default locale is.
- **Tests:** each test we added was checked by temporarily breaking the code it targets and watching it fail.
