// src/pages/ProductDetailPage.jsx  (starter)
import React from 'react';
import { Link } from 'react-router-dom';
// TODO 10: Import useParams from 'react-router-dom'.
import products from '../data/products';

function ProductDetailPage() {
  // TODO 11: Call useParams() and destructure the `id` parameter.

  // TODO 12: Find the product in the products array whose id matches parseInt(id, 10).
  const product = null; // replace this line

  if (!product) {
    // TODO 13: Return a <p> that says "Product not found." when no match is found.
    return null;
  }

  return (
    <main style={{ padding: '1rem' }}>
      <Link to="/products">← Back to Products</Link>

      {/* TODO 14: Render the product's name in an <h1>, category in a <p>, and description in a <p> */}
    </main>
  );
}

export default ProductDetailPage;
