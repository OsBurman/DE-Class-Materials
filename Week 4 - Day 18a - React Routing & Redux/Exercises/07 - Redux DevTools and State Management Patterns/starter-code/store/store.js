// store/store.js  (starter)
import { configureStore } from '@reduxjs/toolkit';
import cartReducer from './cartSlice';
// TODO 6: Import notificationsReducer from './notificationsSlice'.

// TODO 7: Add the notifications reducer under the key 'notifications'.
const store = configureStore({
  reducer: {
    cart: cartReducer,
    // TODO: notifications: notificationsReducer
  },
});

export default store;
