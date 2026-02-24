// store/store.js  (solution)
import { configureStore } from '@reduxjs/toolkit';
import cartReducer from './cartSlice';

// configureStore accepts a map of slice reducers keyed by feature name
const store = configureStore({
  reducer: {
    cart: cartReducer,
  },
});

export default store;
