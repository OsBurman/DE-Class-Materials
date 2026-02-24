package com.testing;

/**
 * Simple DTO representing weather data returned by the external API.
 * Do NOT modify.
 */
public class Weather {

    private int temp;
    private String condition;

    public Weather() {}

    public Weather(int temp, String condition) {
        this.temp = temp;
        this.condition = condition;
    }

    public int getTemp() { return temp; }
    public void setTemp(int temp) { this.temp = temp; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }
}
