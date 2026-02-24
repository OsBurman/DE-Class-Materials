// src/main.js — Entry point (ES Module)
// TODO Task 9: Import WeatherService, WeatherCard, ForecastCard, and utils
// import { WeatherService } from './WeatherService.js';
// import { WeatherCard, ForecastCard } from './WeatherCard.js';
// import { weatherCache, searchHistory, formatWeatherSummary } from './utils.js';

const service = null; // TODO: new WeatherService()

const form = document.getElementById('search-form');
const input = document.getElementById('city-input');
const display = document.getElementById('weather-display');
const errorMsg = document.getElementById('error-msg');
const loading = document.getElementById('loading');
const historyList = document.getElementById('history-list');
const multiBtn = document.getElementById('multi-btn');

// TODO Task 9: Form submit handler
// - Show loading, hide error
// - await service.getCurrentWeather(city)
// - Render a WeatherCard
// - Add city to searchHistory Set, update recent searches list
// - Catch errors and show in errorMsg div
// - Finally: hide loading

// TODO Task 9: Multi-city button handler
// - Call service.getMultipleCities(['London', 'Tokyo', 'New York'])
// - Render all cards side by side
