import { memo } from 'react'
import RenderCount from './RenderCount'

// React.memo: this component only re-renders when product, inCart, or onToggleCart changes.
// Because onToggleCart is wrapped in useCallback in App.jsx with no deps, its reference is
// stable — meaning memo'd cards only re-render when their own inCart prop changes.
const ProductCard = memo(function ProductCard({ product, inCart, onToggleCart }) {
  return (
    <div className={`product-card ${inCart ? 'in-cart' : ''}`}>
      <RenderCount />
      <div className="product-name">{product.name}</div>
      <div className="product-category">{product.category}</div>
      <div className="product-price">${product.price.toFixed(2)}</div>
      <div className="product-rating">{'⭐'.repeat(Math.round(product.rating))} {product.rating}</div>
      <div className="product-stock">Stock: {product.stock}</div>
      <button
        className={`btn-cart ${inCart ? 'in-cart' : ''}`}
        onClick={() => onToggleCart(product.id)}
      >
        {inCart ? '✓ Remove' : '+ Add to Cart'}
      </button>
    </div>
  )
})

export default ProductCard
