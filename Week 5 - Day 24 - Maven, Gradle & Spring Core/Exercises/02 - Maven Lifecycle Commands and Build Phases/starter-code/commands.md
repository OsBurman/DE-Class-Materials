# Maven Lifecycle Commands — Worksheet

## Part 1 — Command Reference Table

Fill in the `Command` and `Output/Effect` columns for each task.

| # | Task | Command | Output / Effect |
|---|---|---|---|
| 1 | Delete all previously compiled output (the `target/` directory) | TODO | TODO |
| 2 | Download all declared dependencies to the local cache | TODO | TODO |
| 3 | Compile all Java source files in `src/main/java/` | TODO | TODO |
| 4 | Run all unit tests | TODO | TODO |
| 5 | Package the compiled code into a `.jar` file | TODO | TODO |
| 6 | Install the `.jar` into the local Maven repository | TODO | TODO |
| 7 | Compile sources but skip test execution | TODO | TODO |
| 8 | Print the full effective (merged) POM to the console | TODO | TODO |

---

## Part 2 — Lifecycle Phase Order

List the six core **default** lifecycle phases in the correct execution order (earliest first):

| Position | Phase Name |
|---|---|
| 1 | TODO |
| 2 | TODO |
| 3 | TODO |
| 4 | TODO |
| 5 | TODO |
| 6 | TODO |

---

## Part 3 — Short Answer Questions

**Q1.** If you run `mvn package`, which earlier phases are automatically executed before packaging starts?

> TODO

**Q2.** What is the difference between a Maven **lifecycle phase** (e.g., `compile`) and a Maven **plugin goal** (e.g., `dependency:resolve`)?

> TODO

**Q3.** Where does Maven store downloaded dependencies on your local machine, and why does this cache matter for offline/team builds?

> TODO
