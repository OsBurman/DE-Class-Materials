// src/WeatherCard.js

/**
 * TODO Task 1: Implement the WeatherCard class.
 *
 * The constructor should accept a destructured object: { city, temp, description, humidity, icon }
 * Use default parameters where sensible.
 *
 * Methods:
 *   render()              — returns an HTML string for the card
 *   static formatTemp(temp, unit = 'C') — formats temperature with unit symbol
 */
export class WeatherCard {
  constructor({ city, temp, description, humidity = 0, icon = '🌡️' }) {
    // TODO: assign all fields to this
  }

  render() {
    // TODO: return a template literal HTML string showing city, temp, description
    // Use WeatherCard.formatTemp(this.temp) for the temperature
    return `<div class="weather-card"><!-- TODO --></div>`;
  }

  static formatTemp(temp, unit = 'C') {
    // TODO: return formatted string like "22°C" or "72°F"
    return `${temp}°${unit}`;
  }
}


/**
 * TODO Task 2: ForecastCard extends WeatherCard
 * Add a forecastDays field (array of { date, maxTemp })
 * Override render() to include the 3-day forecast after calling super.render()
 */
export class ForecastCard extends WeatherCard {
  constructor(weatherData, forecastDays = []) {
    super(weatherData);
    // TODO: assign forecastDays
  }

  render() {
    // TODO: const baseHtml = super.render();
    // TODO: build forecast HTML and return baseHtml + forecastHtml
    return '';
  }
}
