// src/pages/ProductDetailPage.jsx  (solution)
import React from 'react';
import { Link, useParams } from 'react-router-dom';
import products from '../data/products';

function ProductDetailPage() {
  // useParams() returns an object matching the dynamic segments in the route path
  const { id } = useParams();

  // Convert the string id to a number before comparing
  const product = products.find(p => p.id === parseInt(id, 10));

  if (!product) {
    return (
      <main style={{ padding: '1rem' }}>
        <p>Product not found.</p>
        <Link to="/products">← Back to Products</Link>
      </main>
    );
  }

  return (
    <main style={{ padding: '1rem' }}>
      <Link to="/products">← Back to Products</Link>
      <h1>{product.name}</h1>
      <p><strong>Category:</strong> {product.category}</p>
      <p>{product.description}</p>
    </main>
  );
}

export default ProductDetailPage;
