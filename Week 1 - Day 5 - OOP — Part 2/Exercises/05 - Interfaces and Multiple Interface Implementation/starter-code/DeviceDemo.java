// TODO: Define interface Switchable with three methods:
//       void turnOn()
//       void turnOff()
//       boolean isOn()


// TODO: Define interface Connectable with three methods:
//       void connect(String networkName)
//       void disconnect()
//       boolean isConnected()


// TODO: Define interface Schedulable with:
//       - A DEFAULT method: default String getDefaultSchedule() { return "No schedule set"; }
//       - An abstract method: void setSchedule(String schedule)
//       - An abstract method: String getSchedule()


// TODO: Create class SmartLight that implements Switchable
//       Fields: boolean on, String name
//       Constructor takes String name
//       turnOn() prints "[name] light turned on" and sets on = true
//       turnOff() prints "[name] light turned off" and sets on = false
//       isOn() returns on


// TODO: Create class SmartThermostat that implements Switchable AND Schedulable
//       Fields: boolean on, String schedule, String location
//       Constructor takes String location
//       Implement Switchable: turnOn() prints "[location] thermostat turned on", etc.
//       setSchedule(String schedule): stores the schedule
//       getSchedule(): return schedule if it's set, otherwise call getDefaultSchedule()


// TODO: Create class SmartRouter that implements Switchable, Connectable, AND Schedulable
//       Fields: boolean on, boolean connected, String currentNetwork, String schedule, String modelName
//       Constructor takes String modelName
//       Implement all methods:
//         turnOn() prints "[modelName] turned on" and sets on = true
//         turnOff() prints "[modelName] turned off" and sets on = false
//         isOn() returns on
//         connect(networkName) prints "Connected to: [networkName]", sets connected = true, stores name
//         disconnect() prints "Disconnected from network.", sets connected = false
//         isConnected() returns connected
//         setSchedule/getSchedule — same pattern as SmartThermostat


public class DeviceDemo {
    public static void main(String[] args) {
        System.out.println("=== Smart Home Device System ===\n");

        // --- SmartLight ---
        System.out.println("--- SmartLight ---");
        // TODO: Create a SmartLight named "Living Room"
        // TODO: Call turnOn(), print "Is on: " + light.isOn()
        // TODO: Call turnOff(), print "Is on: " + light.isOn()

        System.out.println();

        // --- SmartThermostat ---
        System.out.println("--- SmartThermostat ---");
        // TODO: Create a SmartThermostat with location "Bedroom"
        // TODO: Call turnOn()
        // TODO: Print "Default schedule: " + thermostat.getDefaultSchedule()
        // TODO: Call setSchedule("Weekdays 7am-10pm")
        // TODO: Print "Schedule set to: Weekdays 7am-10pm"
        // TODO: Print "Current schedule: " + thermostat.getSchedule()

        System.out.println();

        // --- SmartRouter ---
        System.out.println("--- SmartRouter ---");
        // TODO: Create a SmartRouter with modelName "Main Router"
        // TODO: Call turnOn()
        // TODO: Call connect("HomeNetwork") — the method itself prints "Connected to: HomeNetwork"
        // TODO: Print "Is connected: " + router.isConnected()
        // TODO: Call setSchedule("Daily restart at 3am"), print "Schedule set to: Daily restart at 3am"
        // TODO: Call disconnect()
        // TODO: Print "Is connected: " + router.isConnected()

        System.out.println();

        // --- Connectable reference ---
        System.out.println("--- Connectable reference to SmartRouter ---");
        // TODO: Assign your router to a Connectable variable: Connectable conn = router;
        //       (You can only call Connectable methods through this reference)
        // TODO: Call conn.connect("OfficeNetwork")
        // TODO: Print "Is connected: " + conn.isConnected()
    }
}
