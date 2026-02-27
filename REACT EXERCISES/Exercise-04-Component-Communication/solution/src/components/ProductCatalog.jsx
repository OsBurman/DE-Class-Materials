import ProductCard from './ProductCard'

export default function ProductCatalog({ products, onAddToCart }) {
  return (
    <div className="product-catalog">
      <h2>Products</h2>
      <div className="product-grid">
        {products.map(product => (
          <ProductCard key={product.id} product={product} onAddToCart={onAddToCart} />
        ))}
      </div>
    </div>
  )
}
