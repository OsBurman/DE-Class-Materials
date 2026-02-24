// src/App.jsx  (solution)
import React from 'react';
import ProductList from './components/ProductList';
import CartSummary from './components/CartSummary';

function App() {
  return (
    <div style={{ display: 'flex', gap: '2rem', padding: '1rem' }}>
      <ProductList />
      <CartSummary />
    </div>
  );
}

export default App;
