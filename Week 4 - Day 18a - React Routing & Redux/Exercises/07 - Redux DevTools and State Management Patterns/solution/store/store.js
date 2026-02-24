// store/store.js  (solution)
import { configureStore } from '@reduxjs/toolkit';
import cartReducer from './cartSlice';
import notificationsReducer from './notificationsSlice';

// Multiple slices — each manages its own independent state tree branch
// Redux DevTools is automatically enabled in development by configureStore
const store = configureStore({
  reducer: {
    cart: cartReducer,
    notifications: notificationsReducer,
  },
});

export default store;
