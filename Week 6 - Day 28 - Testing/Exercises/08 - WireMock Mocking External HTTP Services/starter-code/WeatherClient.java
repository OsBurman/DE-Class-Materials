package com.testing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client that calls an external weather service.
 *
 * The base URL is injected from the property {@code weather.service.url}.
 * In production this points to the real API.
 * In tests, WireMock overrides the property with the stub server's URL.
 *
 * Do NOT modify this class.
 */
@Component
public class WeatherClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public WeatherClient(RestTemplate restTemplate,
                         @Value("${weather.service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * Fetches current weather for the given city.
     *
     * @throws RuntimeException if the external service returns a non-2xx status
     */
    public Weather getWeather(String city) {
        String url = baseUrl + "/weather?city=" + city;
        return restTemplate.getForObject(url, Weather.class);
    }
}
