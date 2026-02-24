# Exercise 06: DateTime API — LocalDate, LocalTime, LocalDateTime, Period & Duration

## Objective
Work with Java's modern date-time API (`java.time`) to create, manipulate, format, and calculate differences between dates and times.

## Background
Before Java 8, `java.util.Date` and `Calendar` were infamous for being mutable, not thread-safe, and counter-intuitive (months starting at 0, etc.). Java 8 replaced them with an immutable, fluent API split into logical types:
- **`LocalDate`** — a date without time (e.g., `2024-01-15`)
- **`LocalTime`** — a time without date (e.g., `14:30:00`)
- **`LocalDateTime`** — date + time combined
- **`DateTimeFormatter`** — parse and format patterns
- **`Period`** — a date-based amount (years, months, days)
- **`Duration`** — a time-based amount (hours, minutes, seconds)

## Requirements

1. **LocalDate**:
   - Print `LocalDate.now()`
   - Create `LocalDate birthday = LocalDate.of(2000, 6, 15)` and print it
   - Print the date 100 days after birthday using `plusDays(100)`
   - Print whether birthday is before today using `isBefore()`
   - Print `birthday.getDayOfWeek()` and `birthday.getMonth()`

2. **LocalTime**:
   - Print `LocalTime.now()`
   - Create `LocalTime meeting = LocalTime.of(14, 30)` and print `"Meeting at: " + meeting`
   - Print the time 90 minutes later using `plusMinutes(90)`
   - Print whether the meeting is before noon using `meeting.isBefore(LocalTime.NOON)`

3. **LocalDateTime**:
   - Combine the birthday `LocalDate` and meeting `LocalTime` into a `LocalDateTime` using `LocalDateTime.of()`
   - Print the result
   - Print the result of adding 3 days and 2 hours using `.plusDays(3).plusHours(2)`

4. **DateTimeFormatter**:
   - Format birthday with `"dd/MM/yyyy"` → `"15/06/2000"`
   - Format birthday with `"MMMM d, yyyy"` → `"June 15, 2000"`
   - Parse the string `"20/12/2025"` with `"dd/MM/yyyy"` into a `LocalDate` and print it

5. **Period**:
   - Calculate the `Period.between(birthday, LocalDate.now())`
   - Print the years, months, and days components with `getYears()`, `getMonths()`, `getDays()`

6. **Duration**:
   - Calculate `Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 45))`
   - Print the total hours with `toHours()` and remaining minutes with `toMinutesPart()`

## Hints
- All `java.time` objects are **immutable** — methods like `plusDays()` return a new object
- `DateTimeFormatter.ofPattern(...)` creates the formatter; use `date.format(formatter)` or `LocalDate.parse(str, formatter)`
- `toMinutesPart()` (Java 9+) gives only the minutes component; alternatively use `toMinutes() % 60`
- `LocalDate.now()` results will vary — your output will differ from the sample below

## Expected Output

```
=== LocalDate ===
Today: 2025-01-28
Birthday: 2000-06-15
100 days after birthday: 2000-09-23
Birthday is before today: true
Day of week: THURSDAY
Month: JUNE

=== LocalTime ===
Now: 10:45:32.123456789
Meeting at: 14:30
90 minutes later: 16:00
Meeting before noon: false

=== LocalDateTime ===
Combined: 2000-06-15T14:30
Plus 3 days and 2 hours: 2000-06-18T16:30

=== DateTimeFormatter ===
Formatted dd/MM/yyyy: 15/06/2000
Formatted MMMM d, yyyy: June 15, 2000
Parsed date: 2025-12-20

=== Period ===
Years: 24
Months: 7
Days: 13

=== Duration ===
Hours: 8
Minutes: 45
```

> Note: `Today`, `Now`, and `Period` values will reflect the actual current date when you run the program.
