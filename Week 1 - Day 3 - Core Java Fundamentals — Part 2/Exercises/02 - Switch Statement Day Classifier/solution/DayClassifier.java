public class DayClassifier {

    public static void main(String[] args) {

        int dayNumber = 3;

        // Switch 1: assign exact day name — every case has its own break to prevent fall-through
        String dayName;
        switch (dayNumber) {
            case 1:  dayName = "Monday";    break;
            case 2:  dayName = "Tuesday";   break;
            case 3:  dayName = "Wednesday"; break;
            case 4:  dayName = "Thursday";  break;
            case 5:  dayName = "Friday";    break;
            case 6:  dayName = "Saturday";  break;
            case 7:  dayName = "Sunday";    break;
            default: dayName = "Unknown Day";
        }

        // Switch 2: intentional fall-through groups cases 1-5 and 6-7 together
        // Cases without a break "fall through" to the next case — here that's the desired behavior
        String dayType;
        switch (dayNumber) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                dayType = "Weekday";
                break;
            case 6:
            case 7:
                dayType = "Weekend";
                break;
            default:
                dayType = "Invalid";
        }

        System.out.println("Day Number : " + dayNumber);
        System.out.println("Day Name   : " + dayName);
        System.out.println("Day Type   : " + dayType);
    }
}
