// store/store.js  (starter)
import { configureStore } from '@reduxjs/toolkit';
// TODO 8: Import cartReducer from './cartSlice'

// TODO 9: Create and export the Redux store using configureStore.
//         Register the cart reducer under the key 'cart'.
const store = configureStore({
  reducer: {
    // TODO: cart: cartReducer
  },
});

export default store;
