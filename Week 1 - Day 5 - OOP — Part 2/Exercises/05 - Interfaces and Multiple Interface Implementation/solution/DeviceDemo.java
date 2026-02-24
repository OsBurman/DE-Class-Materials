// Interface for power state — any device that can be switched on or off
interface Switchable {
    void turnOn();
    void turnOff();
    boolean isOn();
}

// Interface for network connectivity
interface Connectable {
    void connect(String networkName);
    void disconnect();
    boolean isConnected();
}

// Interface for scheduled operation
// getDefaultSchedule() is a default method — concrete implementation inside an interface (Java 8+)
// Classes that implement Schedulable get this for free but can override it if needed
interface Schedulable {
    default String getDefaultSchedule() { return "No schedule set"; }
    void setSchedule(String schedule);
    String getSchedule();
}

// SmartLight — only needs to be switchable
class SmartLight implements Switchable {
    private boolean on;
    private String name;

    public SmartLight(String name) {
        this.name = name;
        this.on = false;
    }

    @Override
    public void turnOn()  { on = true;  System.out.println(name + " light turned on"); }
    @Override
    public void turnOff() { on = false; System.out.println(name + " light turned off"); }
    @Override
    public boolean isOn() { return on; }
}

// SmartThermostat — can be switched and scheduled
class SmartThermostat implements Switchable, Schedulable {
    private boolean on;
    private String schedule;
    private String location;

    public SmartThermostat(String location) {
        this.location = location;
        this.on = false;
        this.schedule = null;
    }

    @Override public void turnOn()  { on = true;  System.out.println(location + " thermostat turned on"); }
    @Override public void turnOff() { on = false; System.out.println(location + " thermostat turned off"); }
    @Override public boolean isOn() { return on; }

    @Override
    public void setSchedule(String schedule) { this.schedule = schedule; }

    @Override
    public String getSchedule() {
        // Fall back to the interface's default method if no schedule has been set
        return (schedule != null) ? schedule : getDefaultSchedule();
    }
}

// SmartRouter — switchable, connectable, and schedulable (implements all three)
class SmartRouter implements Switchable, Connectable, Schedulable {
    private boolean on;
    private boolean connected;
    private String currentNetwork;
    private String schedule;
    private String modelName;

    public SmartRouter(String modelName) {
        this.modelName = modelName;
        this.on = false;
        this.connected = false;
    }

    // Switchable
    @Override public void turnOn()  { on = true;  System.out.println(modelName + " turned on"); }
    @Override public void turnOff() { on = false; System.out.println(modelName + " turned off"); }
    @Override public boolean isOn() { return on; }

    // Connectable
    @Override
    public void connect(String networkName) {
        currentNetwork = networkName;
        connected = true;
        System.out.println("Connected to: " + networkName);
    }
    @Override
    public void disconnect() {
        connected = false;
        currentNetwork = null;
        System.out.println("Disconnected from network.");
    }
    @Override
    public boolean isConnected() { return connected; }

    // Schedulable
    @Override public void setSchedule(String schedule)  { this.schedule = schedule; }
    @Override public String getSchedule() {
        return (schedule != null) ? schedule : getDefaultSchedule();
    }
}

public class DeviceDemo {
    public static void main(String[] args) {
        System.out.println("=== Smart Home Device System ===\n");

        // --- SmartLight (Switchable only) ---
        System.out.println("--- SmartLight ---");
        SmartLight light = new SmartLight("Living Room");
        light.turnOn();
        System.out.println("Is on: " + light.isOn());
        light.turnOff();
        System.out.println("Is on: " + light.isOn());

        System.out.println();

        // --- SmartThermostat (Switchable + Schedulable) ---
        System.out.println("--- SmartThermostat ---");
        SmartThermostat thermostat = new SmartThermostat("Bedroom");
        thermostat.turnOn();
        // getDefaultSchedule() returns the interface's default implementation
        System.out.println("Default schedule: " + thermostat.getDefaultSchedule());
        thermostat.setSchedule("Weekdays 7am-10pm");
        System.out.println("Schedule set to: Weekdays 7am-10pm");
        System.out.println("Current schedule: " + thermostat.getSchedule());

        System.out.println();

        // --- SmartRouter (all three interfaces) ---
        System.out.println("--- SmartRouter ---");
        SmartRouter router = new SmartRouter("Main Router");
        router.turnOn();
        router.connect("HomeNetwork");
        System.out.println("Is connected: " + router.isConnected());
        router.setSchedule("Daily restart at 3am");
        System.out.println("Schedule set to: Daily restart at 3am");
        router.disconnect();
        System.out.println("Is connected: " + router.isConnected());

        System.out.println();

        // --- Connectable reference: only Connectable methods are visible through this reference ---
        System.out.println("--- Connectable reference to SmartRouter ---");
        Connectable conn = router;  // SmartRouter IS-A Connectable
        conn.connect("OfficeNetwork");
        System.out.println("Is connected: " + conn.isConnected());
        // conn.turnOn();  // would NOT compile — turnOn() is not part of Connectable
    }
}
