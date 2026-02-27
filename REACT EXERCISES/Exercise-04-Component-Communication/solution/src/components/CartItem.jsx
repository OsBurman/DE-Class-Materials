export default function CartItem({ item, onRemove, onUpdateQuantity }) {
  const lineTotal = (item.price * item.quantity).toFixed(2)
  return (
    <div className="cart-item">
      <span className="cart-item-emoji">{item.emoji}</span>
      <div className="cart-item-info">
        <div className="cart-item-name">{item.name}</div>
        <div className="cart-item-price">
          ${item.price.toFixed(2)} × {item.quantity} = ${lineTotal}
        </div>
      </div>
      <div className="qty-controls">
        <button className="btn-qty" onClick={() => onUpdateQuantity(item.id, item.quantity - 1)}>−</button>
        <span className="qty-value">{item.quantity}</span>
        <button className="btn-qty" onClick={() => onUpdateQuantity(item.id, item.quantity + 1)}>+</button>
      </div>
      <button className="btn-remove" onClick={() => onRemove(item.id)}>🗑</button>
    </div>
  )
}
