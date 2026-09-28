# Robot Simulator

A rover on a 10x10 zone, driven by `DEPLOY`, `PIT`, `MOVE`, `LEFT`, `RIGHT` and `REPORT` commands read from an input
stream. The full spec is in the javadoc of [`RobotSimulator`](src/main/java/RobotSimulator.java).

## Requirements

JDK 21 or later. The Gradle wrapper downloads Gradle itself.

## Running the tests

```sh
./gradlew test
```

To also get a coverage report, run `./gradlew jacocoTestReport`, then open
`build/reports/jacoco/test/html/index.html`.

## Where to look

- [`RobotSimulator`](src/main/java/RobotSimulator.java): the entry point. It reads the input and logs lines that
  are malformed or ignored.
- [`Simulation`](src/main/java/com/nuix/interview/robot/simulation/Simulation.java): the rules.
- [`CommandParser`](src/main/java/com/nuix/interview/robot/command/CommandParser.java): turns a line of text into a command.
- [`RobotSimulatorTest`](src/test/java/RobotSimulatorTest.java): the provided scenarios, plus tests for logging and
  stream handling.
- [`SimulationTest`](src/test/java/com/nuix/interview/robot/simulation/SimulationTest.java): one test per rule.

[DESIGN_DECISIONS.md](DESIGN_DECISIONS.md) lists the assumptions we made where the spec is open, the changes we
made to the provided code, and the main design choices.
