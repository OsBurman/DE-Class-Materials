// src/components/CartSummary.jsx  (starter — add selectCartTotal)
import React from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { removeItem, clearCart } from '../store/cartSlice';
// TODO 14: Import selectCartTotal from '../store/cartSlice'.

function CartSummary() {
  const items = useSelector(state => state.cart.items);
  const totalQuantity = useSelector(state => state.cart.totalQuantity);

  // TODO 15: Use useSelector with selectCartTotal to get the computed cart total.
  const cartTotal = 0; // replace with useSelector(selectCartTotal)

  const dispatch = useDispatch();

  return (
    <section style={{ borderLeft: '1px solid #ccc', paddingLeft: '1rem' }}>
      <h2>Cart: {totalQuantity} items | Total: ${cartTotal.toFixed(2)}</h2>
      {items.length === 0 && <p>Your cart is empty.</p>}
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {items.map(item => (
          <li key={item.id} style={{ display: 'flex', gap: '0.5rem', marginBottom: '0.4rem', alignItems: 'center' }}>
            <span>{item.name} x{item.quantity}</span>
            <button onClick={() => dispatch(removeItem(item.id))}>Remove</button>
          </li>
        ))}
      </ul>
      {items.length > 0 && (
        <button onClick={() => dispatch(clearCart())} style={{ marginTop: '0.5rem' }}>Clear Cart</button>
      )}
    </section>
  );
}

export default CartSummary;
