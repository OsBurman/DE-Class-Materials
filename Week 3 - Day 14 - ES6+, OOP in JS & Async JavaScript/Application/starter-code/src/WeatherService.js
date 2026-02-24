// src/WeatherService.js

// TODO Task 4: Create a custom error class
// export class WeatherApiError extends Error {
//   constructor(message) { super(message); this.name = 'WeatherApiError'; }
// }

/**
 * Fetches weather data from the Open-Meteo API (free, no API key needed).
 *
 * Step 1 — Get coordinates:
 *   GET https://geocoding-api.open-meteo.com/v1/search?name={city}&count=1
 *
 * Step 2 — Get weather using coordinates:
 *   GET https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true
 */
export class WeatherService {

  // TODO Task 3: async getCurrentWeather(city)
  // 1. Fetch the geocoding API to get lat/lon for the city
  // 2. If no results, throw WeatherApiError("City not found: " + city)
  // 3. Fetch the weather API with the coordinates
  // 4. Return an object: { city, temp, description, lat, lon }
  async getCurrentWeather(city) {
    // your implementation here
  }

  // TODO Task 3: async getForecast(city) — same geocoding step, then fetch
  // https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&daily=temperature_2m_max&forecast_days=3
  // Return array of 3 day forecasts
  async getForecast(city) {
  }

  // TODO Task 5: getMultipleCities(cities) — use Promise.all to fetch in parallel
  // Accept string[] of city names, return Promise<result[]>
  async getMultipleCities(cities) {
  }
}
