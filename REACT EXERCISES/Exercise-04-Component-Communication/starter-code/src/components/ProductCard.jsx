// TODO 11: Accept `product` and `onAddToCart` props

export default function ProductCard(props) {
  // const { product, onAddToCart } = props

  return (
    <div className="product-card">
      {/* TODO: render product.emoji */}
      <span className="product-emoji">❓</span>
      {/* TODO: render product.name and product.price */}
      <span className="product-name">Product Name</span>
      <span className="product-price">$0.00</span>

      {/* TODO 12: onClick should call onAddToCart(product) */}
      <button className="btn-add" onClick={() => {}}>
        Add to Cart
      </button>
    </div>
  )
}
