// store/cartSlice.js  (solution)
import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  items: [],
  totalQuantity: 0,
};

const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    addItem(state, action) {
      const { id, name, price } = action.payload;
      // Check if this item already exists in the cart
      const existingIndex = state.items.findIndex(item => item.id === id);
      if (existingIndex !== -1) {
        // Immer allows direct mutation — increment quantity in place
        state.items[existingIndex].quantity += 1;
      } else {
        // New item — add with quantity 1
        state.items.push({ id, name, price, quantity: 1 });
      }
      state.totalQuantity += 1;
    },

    removeItem(state, action) {
      const id = action.payload;
      const existing = state.items.find(item => item.id === id);
      if (existing) {
        // Subtract this item's full quantity from the running total
        state.totalQuantity -= existing.quantity;
        state.items = state.items.filter(item => item.id !== id);
      }
    },

    clearCart(state) {
      state.items = [];
      state.totalQuantity = 0;
    },
  },
});

export const { addItem, removeItem, clearCart } = cartSlice.actions;
export default cartSlice.reducer;
