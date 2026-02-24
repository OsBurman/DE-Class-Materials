// src/components/ProductList.jsx  (solution)
import React from 'react';
import { useDispatch } from 'react-redux';
import { addItem } from '../store/cartSlice';

const PRODUCTS = [
  { id: 1, name: 'Laptop',   price: 999 },
  { id: 2, name: 'Mouse',    price: 29  },
  { id: 3, name: 'Keyboard', price: 79  },
];

function ProductList() {
  const dispatch = useDispatch();
  return (
    <section>
      <h2>Products</h2>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {PRODUCTS.map(p => (
          <li key={p.id} style={{ marginBottom: '0.5rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
            <span>{p.name} — ${p.price}</span>
            {/* Each dispatch is logged as a separate action in Redux DevTools */}
            <button onClick={() => dispatch(addItem(p))}>Add to Cart</button>
          </li>
        ))}
      </ul>
    </section>
  );
}

export default ProductList;
