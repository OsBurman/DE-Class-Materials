// TODO 16: Accept `item`, `onRemove`, `onUpdateQuantity` props

export default function CartItem(props) {
  // const { item, onRemove, onUpdateQuantity } = props

  return (
    <div className="cart-item">
      <span className="cart-item-emoji">❓</span>
      <div className="cart-item-info">
        {/* TODO: render item.name */}
        <div className="cart-item-name">Item Name</div>
        {/* TODO 20: Show line total: item.price * item.quantity */}
        <div className="cart-item-price">$0.00 × 0 = $0.00</div>
      </div>

      <div className="qty-controls">
        {/* TODO 17: onClick calls onUpdateQuantity(item.id, item.quantity - 1) */}
        <button className="btn-qty" onClick={() => {}}>−</button>
        {/* TODO: render item.quantity */}
        <span className="qty-value">0</span>
        {/* TODO 18: onClick calls onUpdateQuantity(item.id, item.quantity + 1) */}
        <button className="btn-qty" onClick={() => {}}>+</button>
      </div>

      {/* TODO 19: onClick calls onRemove(item.id) */}
      <button className="btn-remove" onClick={() => {}}>🗑</button>
    </div>
  )
}
