package com.springai;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WeatherFunctionConfig {

    // Registers a FunctionCallback bean that the AI model can call when it needs weather data.
    // The description tells the model WHEN to use this function — be precise.
    @Bean
    public FunctionCallback currentWeather() {
        return FunctionCallback.builder()
                .function("currentWeather",
                        (WeatherRequest req) -> new WeatherResponse(req.city() + ": 22°C, sunny"))
                .inputType(WeatherRequest.class)
                .description("Get the current weather for a given city")
                .build();
    }
}
