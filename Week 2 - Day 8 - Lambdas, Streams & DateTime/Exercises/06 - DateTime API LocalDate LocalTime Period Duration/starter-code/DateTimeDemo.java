import java.time.*;
import java.time.format.DateTimeFormatter;

public class DateTimeDemo {

    public static void main(String[] args) {

        // ── Part 1: LocalDate ───────────────────────────────────────────────
        System.out.println("=== LocalDate ===");

        // TODO: Print LocalDate.now() with label "Today: "


        // TODO: Create LocalDate birthday = LocalDate.of(2000, 6, 15) and print it


        // TODO: Print 100 days after birthday using plusDays(100)


        // TODO: Print whether birthday isBefore today


        // TODO: Print birthday.getDayOfWeek() and birthday.getMonth()


        // ── Part 2: LocalTime ───────────────────────────────────────────────
        System.out.println("\n=== LocalTime ===");

        // TODO: Print LocalTime.now() with label "Now: "


        // TODO: Create LocalTime meeting = LocalTime.of(14, 30) and print "Meeting at: " + meeting


        // TODO: Print the time 90 minutes later using plusMinutes(90)


        // TODO: Print whether the meeting is before noon using meeting.isBefore(LocalTime.NOON)


        // ── Part 3: LocalDateTime ───────────────────────────────────────────
        System.out.println("\n=== LocalDateTime ===");

        // TODO: Combine birthday and meeting into LocalDateTime using LocalDateTime.of() and print it


        // TODO: Print the result of adding 3 days and 2 hours with .plusDays(3).plusHours(2)


        // ── Part 4: DateTimeFormatter ──────────────────────────────────────
        System.out.println("\n=== DateTimeFormatter ===");

        // TODO: Create DateTimeFormatter with pattern "dd/MM/yyyy"
        //       Format birthday with it and print "Formatted dd/MM/yyyy: <result>"


        // TODO: Create DateTimeFormatter with pattern "MMMM d, yyyy"
        //       Format birthday and print "Formatted MMMM d, yyyy: <result>"


        // TODO: Parse "20/12/2025" with the dd/MM/yyyy formatter into a LocalDate
        //       Print "Parsed date: <result>"


        // ── Part 5: Period ──────────────────────────────────────────────────
        System.out.println("\n=== Period ===");

        // TODO: Calculate Period.between(birthday, LocalDate.now())
        //       Print getYears(), getMonths(), getDays() on separate lines


        // ── Part 6: Duration ───────────────────────────────────────────────
        System.out.println("\n=== Duration ===");

        // TODO: Calculate Duration.between(LocalTime.of(9, 0), LocalTime.of(17, 45))
        //       Print toHours() and toMinutesPart() (or toMinutes() % 60)
    }
}
