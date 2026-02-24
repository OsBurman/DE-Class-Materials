// src/components/CartSummary.jsx  (starter)
import React from 'react';
// TODO 10: Import useSelector and useDispatch from 'react-redux'.
// TODO 11: Import removeItem and clearCart from '../store/cartSlice'.

function CartSummary() {
  // TODO 12: Use useSelector to read state.cart.items into a variable `items`.
  // TODO 13: Use useSelector to read state.cart.totalQuantity into a variable `totalQuantity`.
  // TODO 14: Call useDispatch() to get the dispatch function.

  const items = [];         // replace with useSelector
  const totalQuantity = 0;  // replace with useSelector

  function handleRemove(id) {
    // TODO 15: Dispatch removeItem with the given id.
  }

  function handleClear() {
    // TODO 16: Dispatch clearCart.
  }

  return (
    <section style={{ borderLeft: '1px solid #ccc', paddingLeft: '1rem' }}>
      <h2>Cart: {totalQuantity} items</h2>
      {items.length === 0 && <p>Your cart is empty.</p>}
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {/* TODO 17: Map over `items` and render:
              <li key={item.id}>
                {item.name} x{item.quantity}
                <button onClick={() => handleRemove(item.id)}>Remove</button>
              </li>
        */}
      </ul>
      {/* TODO 18: Render a "Clear Cart" button that calls handleClear onClick */}
    </section>
  );
}

export default CartSummary;
