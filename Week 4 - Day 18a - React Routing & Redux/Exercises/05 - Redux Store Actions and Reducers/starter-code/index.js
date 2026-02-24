// index.js  (starter)
import store from './store/store';
import { addItem, removeItem, clearCart } from './store/cartSlice';

// TODO 10: Dispatch addItem twice for { id: 1, name: 'Laptop', price: 999 }
// TODO 11: Dispatch addItem once for { id: 2, name: 'Mouse', price: 29 }

console.log('After adding Laptop x2 and Mouse x1:');
// TODO 12: Log store.getState().cart

// TODO 13: Dispatch removeItem with the Laptop's id (1)

console.log('After removing Laptop:');
// TODO 14: Log store.getState().cart

// TODO 15: Dispatch clearCart

console.log('After clearing cart:');
// TODO 16: Log store.getState().cart
