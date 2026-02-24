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
  const dispatch = useDispatch(); // gives us the store's dispatch function

  function handleAddToCart(product) {
    // Dispatch the addItem action; Redux Toolkit wraps the product as action.payload
    dispatch(addItem(product));
  }

  return (
    <section>
      <h2>Products</h2>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {PRODUCTS.map(product => (
          <li key={product.id} style={{ marginBottom: '0.5rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
            <span>{product.name} — ${product.price}</span>
            <button onClick={() => handleAddToCart(product)}>Add to Cart</button>
          </li>
        ))}
      </ul>
    </section>
  );
}

export default ProductList;
