// index.js  (solution)
import store from './store/store';
import { addItem, removeItem, clearCart } from './store/cartSlice';

// Dispatch actions to add items — duplicate id increments quantity
store.dispatch(addItem({ id: 1, name: 'Laptop', price: 999 }));
store.dispatch(addItem({ id: 1, name: 'Laptop', price: 999 })); // quantity becomes 2
store.dispatch(addItem({ id: 2, name: 'Mouse',  price: 29  }));

console.log('After adding Laptop x2 and Mouse x1:');
console.log(store.getState().cart);
// → { items: [{ id:1, name:'Laptop', price:999, quantity:2 }, { id:2, ... quantity:1 }], totalQuantity: 3 }

store.dispatch(removeItem(1)); // Remove Laptop by id

console.log('After removing Laptop:');
console.log(store.getState().cart);
// → { items: [{ id:2, name:'Mouse', price:29, quantity:1 }], totalQuantity: 1 }

store.dispatch(clearCart());

console.log('After clearing cart:');
console.log(store.getState().cart);
// → { items: [], totalQuantity: 0 }
