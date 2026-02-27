import CartItem from './CartItem'

// TODO 13: Accept cartItems, onRemove, onUpdateQuantity, cartTotal, itemCount props

export default function Cart(props) {
  // const { cartItems, onRemove, onUpdateQuantity, cartTotal, itemCount } = props

  return (
    <div className="cart">
      <h2>🛒 Your Cart</h2>

      {/* TODO 14: If cartItems.length === 0, show an empty state message */}
      {/* TODO 15: Otherwise, map over cartItems and render <CartItem> for each
                  Pass: key={item.id}, item={item}, onRemove={onRemove}, onUpdateQuantity={onUpdateQuantity} */}

      <p className="cart-empty">Your cart is empty 🛒</p>

      {/* TODO: Show cart total */}
      <div className="cart-total">
        <span>Total:</span>
        <strong>${(0).toFixed(2)}</strong>
      </div>
    </div>
  )
}
