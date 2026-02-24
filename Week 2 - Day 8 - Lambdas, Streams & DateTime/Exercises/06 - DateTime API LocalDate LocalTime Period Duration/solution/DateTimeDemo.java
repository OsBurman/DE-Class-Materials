import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeDemo {

    public static void main(String[] args) {

        // ── Part 1: LocalDate ───────────────────────────────────────────────
        System.out.println("=== LocalDate ===");

        LocalDate today = LocalDate.now();
        System.out.println("Today: " + today);

        LocalDate birthday = LocalDate.of(2000, 6, 15);
        System.out.println("Birthday: " + birthday);

        LocalDate hundredDaysLater = birthday.plusDays(100);
        System.out.println("100 days after birthday: " + hundredDaysLater);

        System.out.println("Birthday is before today: " + birthday.isBefore(today));

        System.out.println("Day of week: " + birthday.getDayOfWeek());
        System.out.println("Month: " + birthday.getMonth());

        // ── Part 2: LocalTime ───────────────────────────────────────────────
        System.out.println("\n=== LocalTime ===");

        System.out.println("Now: " + LocalTime.now());

        LocalTime meeting = LocalTime.of(14, 30);
        System.out.println("Meeting at: " + meeting);

        LocalTime ninetyLater = meeting.plusMinutes(90);
        System.out.println("90 minutes later: " + ninetyLater);

        System.out.println("Meeting before noon: " + meeting.isBefore(LocalTime.NOON));

        // ── Part 3: LocalDateTime ───────────────────────────────────────────
        System.out.println("\n=== LocalDateTime ===");

        LocalDateTime combined = LocalDateTime.of(birthday, meeting);
        System.out.println("Combined: " + combined);

        LocalDateTime shifted = combined.plusDays(3).plusHours(2);
        System.out.println("Plus 3 days and 2 hours: " + shifted);

        // ── Part 4: DateTimeFormatter ──────────────────────────────────────
        System.out.println("\n=== DateTimeFormatter ===");

        DateTimeFormatter slashFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println("Formatted dd/MM/yyyy: " + birthday.format(slashFormat));

        DateTimeFormatter longFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        System.out.println("Formatted MMMM d, yyyy: " + birthday.format(longFormat));

        LocalDate parsed = LocalDate.parse("20/12/2025", slashFormat);
        System.out.println("Parsed date: " + parsed);

        // ── Part 5: Period ──────────────────────────────────────────────────
        System.out.println("\n=== Period ===");

        Period age = Period.between(birthday, today);
        System.out.println("Years: " + age.getYears());
        System.out.println("Months: " + age.getMonths());
        System.out.println("Days: " + age.getDays());

        // ── Part 6: Duration ───────────────────────────────────────────────
        System.out.println("\n=== Duration ===");

        Duration workday = Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 45));
        System.out.println("Hours: " + workday.toHours());
        System.out.println("Minutes: " + workday.toMinutesPart());
    }
}
