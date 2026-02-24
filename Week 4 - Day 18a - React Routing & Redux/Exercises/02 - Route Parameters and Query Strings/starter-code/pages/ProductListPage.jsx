// src/pages/ProductListPage.jsx  (starter)
import React from 'react';
import { Link } from 'react-router-dom';
// TODO 3: Import useSearchParams from 'react-router-dom'.
import products from '../data/products';

const CATEGORIES = ['all', 'electronics', 'books', 'clothing'];

function ProductListPage() {
  // TODO 4: Call useSearchParams() to get [searchParams, setSearchParams].

  // TODO 5: Read the 'category' value from searchParams. Default to 'all' when it's null.
  const category = 'all'; // replace this line

  // TODO 6: Filter the products array.
  //         If category === 'all', show all products.
  //         Otherwise, only show products whose .category matches the selected value.
  const filtered = products; // replace this line

  function handleCategoryChange(e) {
    // TODO 7: Call setSearchParams({ category: e.target.value }) to update the URL query string.
  }

  return (
    <main style={{ padding: '1rem' }}>
      <h1>Products</h1>

      <label>
        Filter:{' '}
        {/* TODO 8: Set value={category} and onChange={handleCategoryChange} on this <select> */}
        <select>
          {CATEGORIES.map(cat => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>
      </label>

      <ul style={{ marginTop: '1rem' }}>
        {/* TODO 9: Map over `filtered` and render a <li> for each product.
              Each item should be a <Link to={`/products/${product.id}`}>{product.name}</Link> */}
      </ul>
    </main>
  );
}

export default ProductListPage;
