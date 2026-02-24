// src/pages/ProductListPage.jsx  (solution)
import React from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import products from '../data/products';

const CATEGORIES = ['all', 'electronics', 'books', 'clothing'];

function ProductListPage() {
  // useSearchParams returns a tuple analogous to useState
  const [searchParams, setSearchParams] = useSearchParams();

  // Read 'category' from the query string; default to 'all' when absent
  const category = searchParams.get('category') ?? 'all';

  // Filter based on selected category
  const filtered = category === 'all'
    ? products
    : products.filter(p => p.category === category);

  function handleCategoryChange(e) {
    // Updates the URL to e.g. /products?category=electronics without navigation
    setSearchParams({ category: e.target.value });
  }

  return (
    <main style={{ padding: '1rem' }}>
      <h1>Products</h1>

      <label>
        Filter:{' '}
        <select value={category} onChange={handleCategoryChange}>
          {CATEGORIES.map(cat => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>
      </label>

      <ul style={{ marginTop: '1rem' }}>
        {filtered.map(product => (
          <li key={product.id}>
            {/* Link navigates to the detail page for this product */}
            <Link to={`/products/${product.id}`}>{product.name}</Link>
          </li>
        ))}
      </ul>
    </main>
  );
}

export default ProductListPage;
