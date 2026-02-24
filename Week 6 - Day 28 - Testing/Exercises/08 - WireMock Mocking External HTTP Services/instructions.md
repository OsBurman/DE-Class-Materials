# Exercise 08 - WireMock: Mocking External HTTP Services

## Learning Objectives

By the end of this exercise you will be able to:

- Explain when WireMock is preferable to Mockito for testing HTTP integrations
- Configure a WireMock server in a Spring Boot test
- Stub `GET` and `POST` endpoints with `stubFor()` and `WireMock.*` DSL methods
- Assert request details (URL, headers, body) using `verify()` and `RequestPatternBuilder`
- Simulate failure modes: timeouts, 5xx errors, and delayed responses

---

## Background

When your service calls an **external HTTP API** (payment gateway, weather
service, another microservice), you cannot mock it with Mockito because the
HTTP call goes out over the network. WireMock starts a lightweight HTTP server
that accepts stubbed routes and records all received requests.

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)    // random port; URL injected as ${wiremock.server.url}
class WeatherClientTest {

    @Autowired WeatherClient weatherClient;

    @Test void testGetWeather() {
        stubFor(get("/weather?city=London")
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("""{"temp": 18, "condition": "Cloudy"}""")));

        Weather w = weatherClient.getWeather("London");
        assertEquals(18, w.getTemp());
    }
}
```

---

## Domain

`WeatherClient` is a service that calls `GET /weather?city={city}` on an
external URL (configured via `weather.service.url` property).  
It returns a `Weather` object with `temp` (int) and `condition` (String).

---

## Task 1 — Happy Path

1. Add `wiremock-spring-boot` starter to pom.xml.
2. Stub `GET /weather?city=London` to return `{"temp":18,"condition":"Cloudy"}`.
3. Call `weatherClient.getWeather("London")` and assert `temp == 18`.

## Task 2 — Verify the Request

4. After calling the client, use WireMock `verify()` to confirm that exactly
   one `GET` request was made to `/weather?city=London`.

## Task 3 — Simulate 503 Service Unavailable

5. In `testGetWeatherServiceDown()`:
   - Stub `/weather?city=Paris` to return status `503`.
   - Assert that `weatherClient.getWeather("Paris")` throws a `RuntimeException`
     (or whatever your client throws on 5xx).

## Task 4 — Simulate Timeout / Slow Response

6. In `testGetWeatherTimeout()`:
   - Stub `/weather?city=Berlin` to return status `200` but with a fixed delay
     of 3000 ms using `.withFixedDelay(3000)`.
   - Set the client's read timeout to 1000 ms.
   - Assert that the call throws a timeout-related exception.

---

## Running the Tests

```bash
cd starter-code
mvn test
```
