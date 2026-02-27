// TODO 1: Wrap this component with React.memo so it only re-renders
// when its own props change (product, inCart, or onToggleCart).
// Without React.memo, all 500 cards re-render on every App state change.

import RenderCount from './RenderCount'

function ProductCard({ product, inCart, onToggleCart }) {
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
}

export default ProductCard
