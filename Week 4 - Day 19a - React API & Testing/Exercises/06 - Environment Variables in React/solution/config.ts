/**
 * Centralises every environment variable the app needs.
 *
 * Rules enforced here:
 *  - All keys must start with REACT_APP_ to be embedded by CRA.
 *  - Boolean flags are represented as the string "true" in .env files.
 *  - Missing values fall back to safe defaults so the app still loads.
 */
export const config = {
  /** Base URL for all API calls, e.g. https://jsonplaceholder.typicode.com */
  apiBaseUrl: process.env.REACT_APP_API_BASE_URL ?? '',

  /** Enable dark-mode styles when the flag is the string "true" */
  featureDarkMode: process.env.REACT_APP_FEATURE_DARK_MODE === 'true',
};
