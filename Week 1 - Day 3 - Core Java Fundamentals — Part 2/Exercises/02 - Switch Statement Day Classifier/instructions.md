# Exercise 02: Switch Statement Day Classifier

## Objective
Use a `switch` statement to classify days of the week into categories, and understand fall-through behavior and the `default` case.

## Background
The `switch` statement is a cleaner alternative to long if-else chains when you're matching a single variable against a fixed set of values. Java's `switch` supports `int`, `char`, `String`, and enum types. Understanding when values "fall through" from one `case` to the next (when you omit a `break`) is a key concept.

## Requirements

1. Declare an `int` variable named `dayNumber` with the value `3` (representing Wednesday).

2. Write a `switch` statement on `dayNumber` that assigns the day name to a `String` variable named `dayName`:
   - 1 → `"Monday"`, 2 → `"Tuesday"`, 3 → `"Wednesday"`, 4 → `"Thursday"`, 5 → `"Friday"`, 6 → `"Saturday"`, 7 → `"Sunday"`
   - `default` → `"Unknown Day"`
   Each case must have a `break`.

3. Write a **second `switch` statement** on `dayNumber` that assigns a day type to a `String` variable named `dayType`, using **intentional fall-through** (no `break` between cases that share the same result):
   - 1, 2, 3, 4, 5 → `"Weekday"`
   - 6, 7 → `"Weekend"`

4. Print the day number, day name, and day type in the format shown in Expected Output.

5. **Bonus:** Change `dayNumber` to `9` and confirm that `dayName` becomes `"Unknown Day"` and `dayType` would also need a default. Add a `default` case to the second switch that assigns `"Invalid"`.

## Hints
- Omitting `break` at the end of a `case` causes execution to "fall through" into the next case — this is intentional in the day-type switch.
- The `default` case runs when none of the `case` values match — treat it like the `else` in an if-else chain.
- Each `case` label is followed by a colon (`:`), not curly braces.
- In the fall-through switch, put the assignment (`dayType = "Weekday";`) and `break` only in the **last** case of the group.

## Expected Output
*(dayNumber = 3)*
```
Day Number : 3
Day Name   : Wednesday
Day Type   : Weekday
```
*(dayNumber = 6)*
```
Day Number : 6
Day Name   : Saturday
Day Type   : Weekend
```
*(dayNumber = 9)*
```
Day Number : 9
Day Name   : Unknown Day
Day Type   : Invalid
```
