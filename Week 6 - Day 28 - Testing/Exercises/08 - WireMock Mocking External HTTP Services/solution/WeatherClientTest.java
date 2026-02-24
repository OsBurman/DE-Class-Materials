package com.testing;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.HttpServerErrorException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)    // starts WireMock on a random port; sets ${wiremock.server.url}
@TestPropertySource(properties = "weather.service.url=${wiremock.server.url}")
@DisplayName("WeatherClient WireMock Tests")
class WeatherClientTest {

    @Autowired
    WeatherClient weatherClient;

    // ── Task 1 — Happy Path ───────────────────────────────────────────────

    @Test
    @DisplayName("getWeather() returns weather data from stubbed response")
    void testGetWeatherSuccess() {
        stubFor(get(urlEqualTo("/weather?city=London"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {"temp":18,"condition":"Cloudy"}
                                """)));

        Weather result = weatherClient.getWeather("London");

        assertEquals(18, result.getTemp());
        assertEquals("Cloudy", result.getCondition());
    }

    // ── Task 2 — Verify the Request ───────────────────────────────────────

    @Test
    @DisplayName("WeatherClient sends exactly one GET to /weather?city=Tokyo")
    void testVerifyRequest() {
        stubFor(get(urlEqualTo("/weather?city=Tokyo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"temp":22,"condition":"Sunny"}""")));

        weatherClient.getWeather("Tokyo");

        // Assert WireMock received exactly one matching request
        verify(1, getRequestedFor(urlEqualTo("/weather?city=Tokyo")));
    }

    // ── Task 3 — 503 Service Unavailable ──────────────────────────────────

    @Test
    @DisplayName("getWeather() throws when external service returns 503")
    void testGetWeatherServiceDown() {
        stubFor(get(urlEqualTo("/weather?city=Paris"))
                .willReturn(aResponse().withStatus(503)));

        // RestTemplate throws HttpServerErrorException for 5xx responses
        assertThrows(HttpServerErrorException.class,
                () -> weatherClient.getWeather("Paris"));
    }

    // ── Task 4 — Simulate Timeout ─────────────────────────────────────────

    @Test
    @DisplayName("getWeather() throws ResourceAccessException on delayed response")
    void testGetWeatherTimeout() {
        stubFor(get(urlEqualTo("/weather?city=Berlin"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(3000)   // 3 s delay > 1 s client read timeout
                        .withHeader("Content-Type", "application/json")
                        .withBody("""{"temp":5,"condition":"Cold"}""")));

        // RestTemplate read timeout = 1 s (configured in AppConfig)
        assertThrows(ResourceAccessException.class,
                () -> weatherClient.getWeather("Berlin"));
    }
}
