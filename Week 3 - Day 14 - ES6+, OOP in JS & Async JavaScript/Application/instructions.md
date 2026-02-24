# Day 14 Application — ES6+, OOP in JS & Async JavaScript: Weather Dashboard

## Overview

You'll build a **Weather Dashboard** — a browser app that fetches live weather data from a public API using the Fetch API and `async/await`. It uses ES6 class syntax, modules, destructuring, and Promises — all the async and ES6+ patterns from today.

---

## Learning Goals

- Use ES6 class syntax with inheritance and prototypes
- Apply destructuring, spread/rest, default parameters
- Import/export with ES Modules
- Use `Map` and `Set` data structures
- Understand the event loop and async execution model
- Implement Promises and async/await
- Handle errors in async code
- Make HTTP requests with Fetch API
- Parse and display JSON data

---

## Project Structure

```
starter-code/
├── index.html        ← provided (uses type="module")
├── styles.css        ← provided
└── src/
    ├── main.js           ← TODO: entry point
    ├── WeatherService.js ← TODO: API class using fetch + async/await
    ├── WeatherCard.js    ← TODO: ES6 class for rendering a weather card
    └── utils.js          ← TODO: utility functions + Map/Set demo
```

---

## Part 1 — ES6 Class: `WeatherCard.js`

**Task 1 — Class with constructor and methods**  
```js
export class WeatherCard {
  constructor({ city, temp, description, humidity, icon }) { ... }
  render() { /* return an HTML string */ }
  static formatTemp(temp, unit = 'C') { /* default param + static method */ }
}
```
Use destructuring in the constructor. Use a default parameter in `formatTemp`.

**Task 2 — Inheritance**  
Create `class ForecastCard extends WeatherCard`:
- Add a `forecastDays` array field
- Override `render()` to include a 3-day forecast section
- Call `super.render()` inside

---

## Part 2 — Async API: `WeatherService.js`

**Task 3 — Async/await with Fetch**  
```js
export class WeatherService {
  async getCurrentWeather(city) { ... }
  async getForecast(city) { ... }
}
```
Use the **Open-Meteo API** (free, no key needed):  
`https://geocoding-api.open-meteo.com/v1/search?name={city}`  
then  
`https://api.open-meteo.com/v1/forecast?latitude={lat}&longitude={lon}&current_weather=true`

**Task 4 — Error handling with async/await**  
Wrap fetch calls in `try-catch`. Create a custom `WeatherApiError` class (extends `Error`). Throw it when the city isn't found or when the network fails.

**Task 5 — `Promise.all` for parallel requests**  
`getMultipleCities(cities)` — accept an array of city names, use `Promise.all()` to fetch all in parallel, return an array of results.

---

## Part 3 — Utils: `utils.js`

**Task 6 — Spread and rest**  
```js
export function mergeWeatherData(...dataSources) { return {...dataSources[0], ...dataSources[1]}; }
```

**Task 7 — Destructuring**  
```js
export function formatWeatherSummary({ city, temp, description } = {}) { ... }
```

**Task 8 — Map and Set**  
Maintain a `Map<string, WeatherCard>` cache of recently fetched cities.  
Use a `Set<string>` to track searched cities (deduplicates automatically).  
Export both as `weatherCache` and `searchHistory`.

---

## Part 4 — `main.js`

**Task 9 — Wire it all up**  
- Import `WeatherService`, `WeatherCard`, and utils using ES module syntax
- On form submit, call `service.getCurrentWeather(city)` and render the result
- Show a loading spinner during the fetch, hide it after
- Display errors in a visible error div
- Use the `searchHistory` Set to show "Recent searches"

---

## Stretch Goals

1. Use `Promise.race()` to add a timeout — if the fetch takes over 5 seconds, reject with a timeout error.
2. Add `async/await` with `for...of` to fetch cities one at a time and display them as they arrive.
3. Convert `WeatherService` to use the `Proxy` object to intercept property access.

---

## Submission Checklist

- [ ] ES6 class with constructor, methods, static method used
- [ ] Class inheritance with `extends` and `super()`
- [ ] Default parameters used
- [ ] Destructuring used in constructor and function params
- [ ] Spread/rest operator used
- [ ] ES module `import`/`export` used
- [ ] `Map` and `Set` used
- [ ] `async/await` used for API call
- [ ] `try-catch` error handling around async code
- [ ] `Promise.all` used for parallel fetches
- [ ] Fetch API used (no Axios needed)
