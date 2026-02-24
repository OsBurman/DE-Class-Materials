// src/App.jsx  (solution)
import React from 'react';
import ProductList from './components/ProductList';
import CartSummary from './components/CartSummary';
import NotificationBell from './components/NotificationBell';

function App() {
  return (
    <div style={{ padding: '1rem' }}>
      <NotificationBell />
      <hr />
      <div style={{ display: 'flex', gap: '2rem', marginTop: '1rem' }}>
        <ProductList />
        <CartSummary />
      </div>
    </div>
  );
}

export default App;
