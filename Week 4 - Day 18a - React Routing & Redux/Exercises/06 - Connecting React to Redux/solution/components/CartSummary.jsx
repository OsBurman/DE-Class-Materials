// src/components/CartSummary.jsx  (solution)
import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { removeItem, clearCart } from '../store/cartSlice';

function CartSummary() {
  // useSelector subscribes this component to the cart slice;
  // it re-renders automatically whenever cart state changes
  const items = useSelector(state => state.cart.items);
  const totalQuantity = useSelector(state => state.cart.totalQuantity);
  const dispatch = useDispatch();

  function handleRemove(id) {
    dispatch(removeItem(id)); // id becomes action.payload in the reducer
  }

  function handleClear() {
    dispatch(clearCart());
  }

  return (
    <section style={{ borderLeft: '1px solid #ccc', paddingLeft: '1rem' }}>
      <h2>Cart: {totalQuantity} items</h2>
      {items.length === 0 && <p>Your cart is empty.</p>}
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {items.map(item => (
          <li key={item.id} style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.4rem', alignItems: 'center' }}>
            <span>{item.name} x{item.quantity}</span>
            <button onClick={() => handleRemove(item.id)}>Remove</button>
          </li>
        ))}
      </ul>
      {items.length > 0 && (
        <button onClick={handleClear} style={{ marginTop: '0.5rem' }}>Clear Cart</button>
      )}
    </section>
  );
}

export default CartSummary;
