import ProductCard from './ProductCard'

// TODO 9: Accept `products` and `onAddToCart` props

export default function ProductCatalog(props) {
  return (
    <div className="product-catalog">
      <h2>Products</h2>
      <div className="product-grid">
        {/* TODO 10: Map over products array and render <ProductCard> for each
                    Pass: key={product.id}, product={product}, onAddToCart={onAddToCart} */}
        <p style={{ color: '#999' }}>No products yet — complete the TODOs!</p>
      </div>
    </div>
  )
}
