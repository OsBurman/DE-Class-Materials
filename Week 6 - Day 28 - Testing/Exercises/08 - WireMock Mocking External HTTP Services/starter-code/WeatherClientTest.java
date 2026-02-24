package com.testing;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercise 08 – WireMock: Mocking External HTTP Services
 *
 * TODO: Complete each test method.
 *       Do NOT modify WeatherClient or Weather.
 */
// TODO: Add @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// TODO: Add @AutoConfigureWireMock(port = 0)
//       This starts a WireMock server and sets ${wiremock.server.url} automatically.
// TODO: Add @TestPropertySource(properties = "weather.service.url=${wiremock.server.url}")
//       This points WeatherClient at WireMock instead of the real service.
@DisplayName("WeatherClient WireMock Tests")
class WeatherClientTest {

    // TODO: @Autowired WeatherClient weatherClient;

    // ── Task 1 — Happy Path ───────────────────────────────────────────────

    @Test
    @DisplayName("getWeather() returns weather data from stubbed response")
    void testGetWeatherSuccess() {
        // TODO: stubFor(get(urlEqualTo("/weather?city=London"))
        //           .willReturn(aResponse()
        //               .withStatus(200)
        //               .withHeader("Content-Type", "application/json")
        //               .withBody("""{"temp":18,"condition":"Cloudy"}""")));

        // TODO: Weather result = weatherClient.getWeather("London");
        // TODO: assertEquals(18, result.getTemp());
        // TODO: assertEquals("Cloudy", result.getCondition());
    }

    // ── Task 2 — Verify the Request ───────────────────────────────────────

    @Test
    @DisplayName("WeatherClient sends exactly one GET to /weather?city=Tokyo")
    void testVerifyRequest() {
        // TODO: Stub /weather?city=Tokyo

        // TODO: Call weatherClient.getWeather("Tokyo")

        // TODO: verify(1, getRequestedFor(urlEqualTo("/weather?city=Tokyo")));
    }

    // ── Task 3 — 503 Service Unavailable ──────────────────────────────────

    @Test
    @DisplayName("getWeather() throws when external service returns 503")
    void testGetWeatherServiceDown() {
        // TODO: Stub /weather?city=Paris to return status 503

        // TODO: assertThrows(Exception.class, () -> weatherClient.getWeather("Paris"));
    }

    // ── Task 4 — Simulate Timeout ─────────────────────────────────────────

    @Test
    @DisplayName("getWeather() throws on timeout (delayed response)")
    void testGetWeatherTimeout() {
        // TODO: Stub /weather?city=Berlin with .withFixedDelay(3000)
        //       (3000 ms delay — longer than the client's read timeout)

        // TODO: assertThrows(Exception.class, () -> weatherClient.getWeather("Berlin"));
        //       The RestTemplate will throw a ResourceAccessException on timeout
    }
}
