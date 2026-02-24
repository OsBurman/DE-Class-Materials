// src/App.jsx  (solution)
import React from 'react';
import { BrowserRouter, Routes, Route, Link } from 'react-router-dom';
import ProductListPage from './pages/ProductListPage';
import ProductDetailPage from './pages/ProductDetailPage';

function App() {
  return (
    <BrowserRouter>
      <nav style={{ padding: '0.5rem', background: '#eee' }}>
        <Link to="/products">Products</Link>
      </nav>

      <Routes>
        <Route path="/products" element={<ProductListPage />} />
        {/* :id is a dynamic segment — accessible via useParams() inside ProductDetailPage */}
        <Route path="/products/:id" element={<ProductDetailPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
