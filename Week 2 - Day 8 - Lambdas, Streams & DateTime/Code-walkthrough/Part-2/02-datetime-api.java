import java.time.*;
import java.time.format.*;
import java.time.temporal.*;

/**
 * DAY 8 — PART 2 | The DateTime API (java.time)
 * ─────────────────────────────────────────────────────────────────────────────
 * Java 8 replaced the old java.util.Date / Calendar API with java.time.
 * The new API is:
 *   - Immutable & thread-safe
 *   - Clearly named (plus/minus/with instead of set/add)
 *   - Separated by concern (date-only, time-only, date+time, timezone-aware)
 *
 * KEY CLASSES:
 * ┌──────────────────────┬──────────────────────────────────────────────────┐
 * │ LocalDate            │ Date only — year, month, day (no time, no zone)  │
 * │ LocalTime            │ Time only — hour, minute, second, nanosecond     │
 * │ LocalDateTime        │ Date + Time (no timezone)                        │
 * │ ZonedDateTime        │ Date + Time + Timezone                           │
 * │ Instant              │ A point on the UTC timeline (epoch seconds)      │
 * │ Period               │ Date-based amount (years, months, days)          │
 * │ Duration             │ Time-based amount (hours, minutes, seconds, nanos│
 * │ DateTimeFormatter    │ Format and parse date/time values                │
 * │ ChronoUnit           │ Units for date/time arithmetic                   │
 * └──────────────────────┴──────────────────────────────────────────────────┘
 *
 * GOLDEN RULE: All java.time objects are IMMUTABLE.
 *   Methods like plusDays() return a NEW object — the original is unchanged.
 */
public class DateTimeApi {

    public static void main(String[] args) {
        demonstrateLocalDate();
        demonstrateLocalTime();
        demonstrateLocalDateTime();
        demonstrateZonedDateTimeAndInstant();
        demonstrateDateTimeFormatter();
        demonstratePeriodAndDuration();
        demonstrateChronoUnit();
        demonstratePracticalExamples();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 1 — LocalDate
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateLocalDate() {
        System.out.println("=== LocalDate ===");

        // ----- Creation -----
        LocalDate today     = LocalDate.now();                    // system clock
        LocalDate birthday  = LocalDate.of(1990, 6, 15);         // specific date
        LocalDate fromParse = LocalDate.parse("2024-03-20");      // ISO-8601 string

        System.out.println("today:      " + today);
        System.out.println("birthday:   " + birthday);
        System.out.println("fromParse:  " + fromParse);

        // ----- Getters -----
        System.out.println("Year:         " + today.getYear());
        System.out.println("Month:        " + today.getMonth());          // enum: MARCH
        System.out.println("Month value:  " + today.getMonthValue());     // int: 3
        System.out.println("Day of month: " + today.getDayOfMonth());
        System.out.println("Day of week:  " + today.getDayOfWeek());      // enum: MONDAY
        System.out.println("Day of year:  " + today.getDayOfYear());
        System.out.println("Is leap year: " + today.isLeapYear());
        System.out.println("Length of month: " + today.lengthOfMonth());  // e.g. 31

        // ----- Arithmetic (IMMUTABLE — always returns new object) -----
        LocalDate nextWeek    = today.plusDays(7);
        LocalDate lastMonth   = today.minusMonths(1);
        LocalDate nextYear    = today.plusYears(1);
        LocalDate firstOfYear = today.withDayOfYear(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        System.out.println("Next week:       " + nextWeek);
        System.out.println("Last month:      " + lastMonth);
        System.out.println("Next year:       " + nextYear);
        System.out.println("First of year:   " + firstOfYear);
        System.out.println("Last of month:   " + lastDayOfMonth);

        // ----- Comparison -----
        LocalDate date1 = LocalDate.of(2024, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 12, 31);

        System.out.println("date1.isBefore(date2): " + date1.isBefore(date2));  // true
        System.out.println("date1.isAfter(date2):  " + date1.isAfter(date2));   // false
        System.out.println("date1.isEqual(date1):  " + date1.isEqual(date1));   // true
        System.out.println("compareTo (< 0 means before): " + date1.compareTo(date2));

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 2 — LocalTime
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateLocalTime() {
        System.out.println("=== LocalTime ===");

        // ----- Creation -----
        LocalTime now       = LocalTime.now();
        LocalTime lunchTime = LocalTime.of(12, 30, 0);      // hour, minute, second
        LocalTime precise   = LocalTime.of(9, 15, 30, 500_000_000);  // with nanoseconds
        LocalTime fromParse = LocalTime.parse("14:45:00");

        System.out.println("now:        " + now);
        System.out.println("lunchTime:  " + lunchTime);
        System.out.println("precise:    " + precise);
        System.out.println("fromParse:  " + fromParse);

        // ----- Getters -----
        System.out.println("Hour:   " + lunchTime.getHour());    // 12
        System.out.println("Minute: " + lunchTime.getMinute());  // 30
        System.out.println("Second: " + lunchTime.getSecond());  // 0

        // ----- Arithmetic -----
        LocalTime plusOneHour    = lunchTime.plusHours(1);        // 13:30
        LocalTime minus30Minutes = lunchTime.minusMinutes(30);    // 12:00
        LocalTime nextHour       = lunchTime.withMinute(0);       // 12:00

        System.out.println("Plus one hour:    " + plusOneHour);
        System.out.println("Minus 30 minutes: " + minus30Minutes);
        System.out.println("On the hour:      " + nextHour);

        // ----- Comparison -----
        LocalTime morning   = LocalTime.of(8, 0);
        LocalTime afternoon = LocalTime.of(15, 0);
        System.out.println("Morning before afternoon: " + morning.isBefore(afternoon));  // true

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 3 — LocalDateTime (Date + Time, no timezone)
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateLocalDateTime() {
        System.out.println("=== LocalDateTime ===");

        // ----- Creation -----
        LocalDateTime now       = LocalDateTime.now();
        LocalDateTime specific  = LocalDateTime.of(2024, 6, 15, 14, 30, 0);
        LocalDateTime combined  = LocalDateTime.of(LocalDate.of(2024, 3, 20), LocalTime.of(9, 0));
        LocalDateTime fromParse = LocalDateTime.parse("2024-01-15T08:30:00");

        System.out.println("now:      " + now);
        System.out.println("specific: " + specific);
        System.out.println("combined: " + combined);
        System.out.println("parsed:   " + fromParse);

        // ----- Extract date or time component -----
        System.out.println("Date part: " + specific.toLocalDate());  // 2024-06-15
        System.out.println("Time part: " + specific.toLocalTime());  // 14:30

        // ----- Arithmetic -----
        LocalDateTime meeting    = LocalDateTime.of(2024, 6, 15, 10, 0);
        LocalDateTime twoHrsLater = meeting.plusHours(2);
        LocalDateTime nextMonth   = meeting.plusMonths(1);

        System.out.println("Meeting:      " + meeting);
        System.out.println("Two hrs later:" + twoHrsLater);
        System.out.println("Next month:   " + nextMonth);

        // ----- Comparison -----
        LocalDateTime deadline = LocalDateTime.of(2024, 12, 31, 23, 59);
        System.out.println("Meeting before deadline: " + meeting.isBefore(deadline));  // true

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 4 — ZonedDateTime & Instant
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateZonedDateTimeAndInstant() {
        System.out.println("=== ZonedDateTime & Instant ===");

        // ----- ZonedDateTime — includes timezone -----
        ZonedDateTime nyTime      = ZonedDateTime.now(ZoneId.of("America/New_York"));
        ZonedDateTime londonTime  = ZonedDateTime.now(ZoneId.of("Europe/London"));
        ZonedDateTime tokyoTime   = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));

        System.out.println("New York: " + nyTime);
        System.out.println("London:   " + londonTime);
        System.out.println("Tokyo:    " + tokyoTime);

        // Convert between zones
        ZonedDateTime noonNY = ZonedDateTime.of(2024, 6, 15, 12, 0, 0, 0, ZoneId.of("America/New_York"));
        ZonedDateTime noonLondon = noonNY.withZoneSameInstant(ZoneId.of("Europe/London"));
        System.out.println("Noon NY in London: " + noonLondon);  // 17:00

        // List available zone IDs
        System.out.println("Sample zone IDs: " +
                ZoneId.getAvailableZoneIds().stream().limit(5).toList());

        // ----- Instant — a point on the timeline -----
        Instant now  = Instant.now();
        Instant then = Instant.ofEpochSecond(0);  // 1970-01-01T00:00:00Z (Unix epoch)

        System.out.println("Now (Instant): " + now);
        System.out.println("Epoch:         " + then);
        System.out.println("Epoch millis:  " + now.toEpochMilli());

        // Convert Instant to ZonedDateTime
        ZonedDateTime fromInstant = now.atZone(ZoneId.of("America/Chicago"));
        System.out.println("Instant → ZonedDateTime: " + fromInstant);

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 5 — DateTimeFormatter
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateDateTimeFormatter() {
        System.out.println("=== DateTimeFormatter ===");

        LocalDate  date = LocalDate.of(2024, 3, 15);
        LocalTime  time = LocalTime.of(14, 30, 45);
        LocalDateTime dt = LocalDateTime.of(date, time);

        // ----- Built-in formatters -----
        System.out.println("ISO_LOCAL_DATE:     " + date.format(DateTimeFormatter.ISO_LOCAL_DATE));  // 2024-03-15
        System.out.println("ISO_LOCAL_TIME:     " + time.format(DateTimeFormatter.ISO_LOCAL_TIME));  // 14:30:45
        System.out.println("ISO_LOCAL_DATE_TIME:" + dt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)); // 2024-03-15T14:30:45

        // ----- Custom patterns with ofPattern -----
        // Pattern letters: y=year, M=month, d=day, H=hour(24h), h=hour(12h),
        //                  m=minute, s=second, a=AM/PM, E=day-of-week, z=timezone

        DateTimeFormatter usDate   = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        DateTimeFormatter euDate   = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter friendly = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter withTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter timeOnly = DateTimeFormatter.ofPattern("hh:mm a");

        System.out.println("US Date:   " + date.format(usDate));    // 03/15/2024
        System.out.println("EU Date:   " + date.format(euDate));    // 15-03-2024
        System.out.println("Friendly:  " + date.format(friendly));  // Friday, March 15, 2024
        System.out.println("With time: " + dt.format(withTime));    // 2024-03-15 14:30:45
        System.out.println("12h time:  " + time.format(timeOnly));  // 02:30 PM

        // ----- Parsing — string to date -----
        // Use the SAME formatter for both format and parse
        LocalDate  parsedDate = LocalDate.parse("15/03/2024",
                DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        LocalDateTime parsedDT = LocalDateTime.parse("15-03-2024 09:30",
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

        System.out.println("Parsed date: " + parsedDate);   // 2024-03-15
        System.out.println("Parsed dt:   " + parsedDT);     // 2024-03-15T09:30

        // ----- Locale-specific formatting -----
        DateTimeFormatter germanDate = DateTimeFormatter
                .ofLocalizedDate(FormatStyle.FULL)
                .withLocale(java.util.Locale.GERMAN);
        System.out.println("German format: " + date.format(germanDate));

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 6 — Period & Duration
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstratePeriodAndDuration() {
        System.out.println("=== Period & Duration ===");

        // ----- Period — date-based amounts (years, months, days) -----
        LocalDate start = LocalDate.of(2020, 1, 15);
        LocalDate end   = LocalDate.of(2024, 6, 20);

        Period period = Period.between(start, end);
        System.out.println("Period: " + period);                    // P4Y5M5D
        System.out.println("Years:  " + period.getYears());         // 4
        System.out.println("Months: " + period.getMonths());        // 5
        System.out.println("Days:   " + period.getDays());          // 5

        // Create periods manually
        Period threeMonths = Period.ofMonths(3);
        Period twoYears    = Period.ofYears(2);
        Period oneYearSixMonths = Period.of(1, 6, 0);

        LocalDate renewalDate = LocalDate.now().plus(threeMonths);
        System.out.println("Renewal (3 months from now): " + renewalDate);

        // ----- Duration — time-based amounts (hours, minutes, seconds, nanos) -----
        LocalTime startTime = LocalTime.of(9, 15, 0);
        LocalTime endTime   = LocalTime.of(17, 45, 30);

        Duration duration = Duration.between(startTime, endTime);
        System.out.println("Duration:       " + duration);                   // PT8H30M30S
        System.out.println("Total hours:    " + duration.toHours());         // 8
        System.out.println("Total minutes:  " + duration.toMinutes());       // 510
        System.out.println("Total seconds:  " + duration.getSeconds());      // 30630
        System.out.println("Minutes part:   " + duration.toMinutesPart());   // 30 (Java 9+)
        System.out.println("Seconds part:   " + duration.toSecondsPart());   // 30 (Java 9+)

        // Duration between two LocalDateTimes
        LocalDateTime meetingStart = LocalDateTime.of(2024, 6, 15, 9, 0);
        LocalDateTime meetingEnd   = LocalDateTime.of(2024, 6, 15, 10, 45);
        Duration meetingLength = Duration.between(meetingStart, meetingEnd);
        System.out.println("Meeting length: " + meetingLength.toMinutes() + " minutes");  // 105

        // Duration arithmetic
        Duration fourHours = Duration.ofHours(4);
        LocalDateTime sessionEnd = meetingStart.plus(fourHours);
        System.out.println("4-hour session ends: " + sessionEnd);

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 7 — ChronoUnit: Simple Date/Time Arithmetic
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstrateChronoUnit() {
        System.out.println("=== ChronoUnit ===");

        LocalDate today     = LocalDate.now();
        LocalDate christmas = LocalDate.of(today.getYear(), 12, 25);
        // If Christmas already passed this year, use next year's
        if (christmas.isBefore(today)) {
            christmas = christmas.plusYears(1);
        }

        long daysUntilChristmas   = ChronoUnit.DAYS.between(today, christmas);
        long weeksUntilChristmas  = ChronoUnit.WEEKS.between(today, christmas);
        long monthsUntilChristmas = ChronoUnit.MONTHS.between(today, christmas);

        System.out.println("Days until Christmas:   " + daysUntilChristmas);
        System.out.println("Weeks until Christmas:  " + weeksUntilChristmas);
        System.out.println("Months until Christmas: " + monthsUntilChristmas);

        // Time-based ChronoUnit
        LocalTime classStart = LocalTime.of(9, 0);
        LocalTime classEnd   = LocalTime.of(17, 0);

        long hoursInClass   = ChronoUnit.HOURS.between(classStart, classEnd);
        long minutesInClass = ChronoUnit.MINUTES.between(classStart, classEnd);
        System.out.println("Hours in class:   " + hoursInClass);    // 8
        System.out.println("Minutes in class: " + minutesInClass);  // 480

        // truncatedTo — zero out smaller units
        LocalTime timeWithNanos = LocalTime.of(14, 30, 45, 123_456_789);
        System.out.println("Original:          " + timeWithNanos);
        System.out.println("Truncated to mins: " + timeWithNanos.truncatedTo(ChronoUnit.MINUTES));  // 14:30
        System.out.println("Truncated to hours:" + timeWithNanos.truncatedTo(ChronoUnit.HOURS));    // 14:00

        System.out.println();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // SECTION 8 — Practical Examples
    // Real-world scenarios combining multiple DateTime concepts
    // ─────────────────────────────────────────────────────────────────────────
    static void demonstratePracticalExamples() {
        System.out.println("=== Practical Examples ===");

        // ----- Example 1: Age Calculator -----
        LocalDate dob   = LocalDate.of(1990, 6, 15);
        LocalDate today = LocalDate.now();
        Period age      = Period.between(dob, today);
        System.out.printf("Date of birth: %s → Age: %d years, %d months, %d days%n",
                dob, age.getYears(), age.getMonths(), age.getDays());

        long ageInDays = ChronoUnit.DAYS.between(dob, today);
        System.out.println("Age in total days: " + ageInDays);

        // ----- Example 2: Deadline Checker -----
        LocalDate projectDeadline = LocalDate.of(today.getYear(), 12, 31);
        long daysRemaining = ChronoUnit.DAYS.between(today, projectDeadline);

        if (today.isAfter(projectDeadline)) {
            System.out.println("⚠️  OVERDUE by " + ChronoUnit.DAYS.between(projectDeadline, today) + " days");
        } else if (daysRemaining <= 7) {
            System.out.println("⚠️  Due soon: " + daysRemaining + " days remaining");
        } else {
            System.out.println("✅ Deadline: " + projectDeadline + " (" + daysRemaining + " days away)");
        }

        // ----- Example 3: Subscription Expiry -----
        LocalDate subscriptionStart  = LocalDate.of(2024, 1, 1);
        LocalDate subscriptionExpiry = subscriptionStart.plusMonths(12).minusDays(1);
        boolean isActive = !today.isAfter(subscriptionExpiry);
        System.out.printf("Subscription: %s → %s | Active: %b%n",
                subscriptionStart, subscriptionExpiry, isActive);

        // ----- Example 4: Meeting Scheduler across timezones -----
        ZonedDateTime chicagoMeeting = ZonedDateTime.of(
                LocalDateTime.of(2024, 6, 15, 10, 0), ZoneId.of("America/Chicago"));
        ZonedDateTime nyMeeting     = chicagoMeeting.withZoneSameInstant(ZoneId.of("America/New_York"));
        ZonedDateTime londonMeeting = chicagoMeeting.withZoneSameInstant(ZoneId.of("Europe/London"));

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("h:mm a z");
        System.out.println("Team meeting:");
        System.out.println("  Chicago: " + chicagoMeeting.format(fmt));
        System.out.println("  NY:      " + nyMeeting.format(fmt));
        System.out.println("  London:  " + londonMeeting.format(fmt));

        // ----- Example 5: Business hours check -----
        LocalTime now = LocalTime.now();
        LocalTime openTime  = LocalTime.of(9, 0);
        LocalTime closeTime = LocalTime.of(17, 0);
        boolean isOpen = !now.isBefore(openTime) && now.isBefore(closeTime);
        System.out.println("Current time " + now.truncatedTo(ChronoUnit.MINUTES) +
                " — Office is " + (isOpen ? "OPEN" : "CLOSED"));

        // ----- Example 6: Working with timestamps (Instant) -----
        Instant requestReceived = Instant.now();
        // Simulate some processing
        Instant requestCompleted = requestReceived.plusMillis(250);
        Duration responseTime = Duration.between(requestReceived, requestCompleted);
        System.out.println("Response time: " + responseTime.toMillis() + "ms");

        // ----- Example 7: Generate a list of future dates -----
        System.out.println("Next 5 Mondays:");
        LocalDate nextMonday = today.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
        for (int i = 0; i < 5; i++) {
            System.out.println("  " + nextMonday.plusWeeks(i)
                    .format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")));
        }

        System.out.println();
    }
}
