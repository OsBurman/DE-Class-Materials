import CartItem from './CartItem'

export default function Cart({ cartItems, onRemove, onUpdateQuantity, cartTotal, itemCount }) {
  return (
    <div className="cart">
      <h2>🛒 Cart ({itemCount})</h2>
      {cartItems.length === 0 ? (
        <p className="cart-empty">Your cart is empty 🛒</p>
      ) : (
        <div className="cart-items">
          {cartItems.map(item => (
            <CartItem
              key={item.id}
              item={item}
              onRemove={onRemove}
              onUpdateQuantity={onUpdateQuantity}
            />
          ))}
        </div>
      )}
      <div className="cart-total">
        <span>Total:</span>
        <strong>${cartTotal.toFixed(2)}</strong>
      </div>
    </div>
  )
}
