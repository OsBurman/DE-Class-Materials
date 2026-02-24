// store/cartSlice.js  (starter)
import { createSlice } from '@reduxjs/toolkit';

// TODO 1: Define initialState with shape: { items: [], totalQuantity: 0 }
const initialState = {
  // TODO: add items and totalQuantity fields
};

// TODO 2: Create a slice using createSlice with:
//         - name: 'cart'
//         - initialState
//         - reducers: addItem, removeItem, clearCart
const cartSlice = createSlice({
  name: 'cart',
  initialState,
  reducers: {
    // TODO 3: addItem(state, action)
    //   action.payload = { id, name, price }
    //   - If an item with action.payload.id already exists, increment its quantity by 1.
    //   - Otherwise, push { ...action.payload, quantity: 1 } to state.items.
    //   - Always increment state.totalQuantity by 1.
    addItem(state, action) {
      // TODO: implement
    },

    // TODO 4: removeItem(state, action)
    //   action.payload = id (number)
    //   - Find the item with that id.
    //   - Subtract that item's quantity from state.totalQuantity.
    //   - Filter state.items to remove the item.
    removeItem(state, action) {
      // TODO: implement
    },

    // TODO 5: clearCart(state)
    //   - Reset state.items to [].
    //   - Reset state.totalQuantity to 0.
    clearCart(state) {
      // TODO: implement
    },
  },
});

// TODO 6: Export action creators from cartSlice.actions
export const { addItem, removeItem, clearCart } = cartSlice.actions;

// TODO 7: Export the reducer as the default export
export default cartSlice.reducer;
