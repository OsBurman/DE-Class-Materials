export default function ProductCard({ product, onAddToCart }) {
  return (
    <div className="product-card">
      <span className="product-emoji">{product.emoji}</span>
      <span className="product-name">{product.name}</span>
      <span className="product-price">${product.price.toFixed(2)}</span>
      <button className="btn-add" onClick={() => onAddToCart(product)}>
        Add to Cart
      </button>
    </div>
  )
}
