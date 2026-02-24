// store/cartSlice.js  (starter — same as Ex 05 solution)
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
      const existingIndex = state.items.findIndex(item => item.id === id);
      if (existingIndex !== -1) {
        state.items[existingIndex].quantity += 1;
      } else {
        state.items.push({ id, name, price, quantity: 1 });
      }
      state.totalQuantity += 1;
    },
    removeItem(state, action) {
      const id = action.payload;
      const existing = state.items.find(item => item.id === id);
      if (existing) {
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
