// src/components/ProductList.jsx  (starter)
import React from 'react';
// TODO 5: Import useDispatch from 'react-redux'.
// TODO 6: Import the addItem action creator from '../store/cartSlice'.

const PRODUCTS = [
  { id: 1, name: 'Laptop',   price: 999 },
  { id: 2, name: 'Mouse',    price: 29  },
  { id: 3, name: 'Keyboard', price: 79  },
];

function ProductList() {
  // TODO 7: Call useDispatch() to get the dispatch function.

  function handleAddToCart(product) {
    // TODO 8: Dispatch the addItem action with the product as payload.
  }

  return (
    <section>
      <h2>Products</h2>
      <ul style={{ listStyle: 'none', padding: 0 }}>
        {PRODUCTS.map(product => (
          <li key={product.id} style={{ marginBottom: '0.5rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
            <span>{product.name} — ${product.price}</span>
            {/* TODO 9: Attach onClick={() => handleAddToCart(product)} to this button */}
            <button>Add to Cart</button>
          </li>
        ))}
      </ul>
    </section>
  );
}

export default ProductList;
