package com.springai;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherFunctionConfig {

    /**
     * TODO 3: Define a @Bean of type FunctionCallback named "currentWeather".
     * Use the builder:
     *   FunctionCallback.builder()
     *       .function("currentWeather", (WeatherRequest req) ->
     *           new WeatherResponse(req.city() + ": 22°C, sunny"))
     *       .inputType(WeatherRequest.class)
     *       .description("Get the current weather for a given city")
     *       .build()
     */
    @Bean
    public FunctionCallback currentWeather() {
        // TODO 3: Implement and return the FunctionCallback
        return null;
    }
}
