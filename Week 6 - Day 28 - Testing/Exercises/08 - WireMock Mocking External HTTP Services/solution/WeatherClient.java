package com.testing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public WeatherClient(RestTemplate restTemplate,
                         @Value("${weather.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Weather getWeather(String city) {
        String url = baseUrl + "/weather?city=" + city;
        return restTemplate.getForObject(url, Weather.class);
    }
}
