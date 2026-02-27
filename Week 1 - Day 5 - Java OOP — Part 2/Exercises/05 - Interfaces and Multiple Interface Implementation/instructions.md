# Exercise 05: Interfaces and Multiple Interface Implementation

## Objective
Practice defining Java interfaces, implementing them in classes, and using multiple interface implementation to add distinct capabilities to a single class.

## Background
A smart home system has devices that can be controlled in different ways — some can be powered on/off, some can connect to a network, and some can be controlled remotely via a schedule. By modeling these capabilities as separate interfaces, any device class can pick and choose exactly which capabilities it needs, regardless of its place in an inheritance hierarchy.

## Requirements

1. Define interface `Switchable` with:
   - `void turnOn()` — called to power on the device
   - `void turnOff()` — called to power off the device
   - `boolean isOn()` — returns whether the device is currently on

2. Define interface `Connectable` with:
   - `void connect(String networkName)` — connects to a network
   - `void disconnect()` — disconnects from the network
   - `boolean isConnected()` — returns whether currently connected

3. Define interface `Schedulable` with:
   - A **default method** `String getDefaultSchedule()` that returns `"No schedule set"` — this is a concrete method inside an interface (Java 8+)
   - `void setSchedule(String schedule)` — abstract; subclasses must implement
   - `String getSchedule()` — abstract; returns the current schedule

4. Create class `SmartLight` that implements `Switchable` only:
   - Fields to track on/off state
   - A `String name` field (set via constructor)
   - Implement all `Switchable` methods: `turnOn()` prints `"[name] light turned on"`, `turnOff()` prints `"[name] light turned off"`, `isOn()` returns the state

5. Create class `SmartThermostat` that implements `Switchable` and `Schedulable`:
   - Fields for on/off state and schedule string
   - Constructor takes a `String location`
   - Implement `Switchable` methods similarly to SmartLight
   - Implement `setSchedule(String schedule)` — stores the schedule
   - Implement `getSchedule()` — returns stored schedule or calls `getDefaultSchedule()` if none set

6. Create class `SmartRouter` that implements all three interfaces (`Switchable`, `Connectable`, `Schedulable`):
   - Fields for on/off, connected/disconnected state, network name, and schedule
   - Constructor takes a `String modelName`
   - Implement all methods across all three interfaces

7. In `main`:
   - Create one of each device
   - Call appropriate methods on each and print state
   - Demonstrate assigning `SmartRouter` to a `Connectable` variable — you can only call `Connectable` methods through that reference
   - Show the default method: call `getDefaultSchedule()` on a freshly created thermostat before setting a schedule

## Hints
- A class can implement multiple interfaces separated by commas: `class Foo implements A, B, C`
- Interfaces contain only abstract methods by default — unless marked `default` (Java 8+)
- When a class implements an interface, it must implement ALL non-default methods or be declared abstract itself
- You can hold any `SmartRouter` object in a variable of type `Connectable` — but you can only call the methods defined in `Connectable` through that reference

## Expected Output

```
=== Smart Home Device System ===

--- SmartLight ---
Living Room light turned on
Is on: true
Living Room light turned off
Is on: false

--- SmartThermostat ---
Bedroom thermostat turned on
Default schedule: No schedule set
Schedule set to: Weekdays 7am-10pm
Current schedule: Weekdays 7am-10pm

--- SmartRouter ---
Main Router turned on
Connected to: HomeNetwork
Is connected: true
Schedule set to: Daily restart at 3am
Disconnected from network.
Is connected: false

--- Connectable reference to SmartRouter ---
Connected to: OfficeNetwork
Is connected: true
```
