function CartSummary({ count, total }) {
  return (
    <div className="cart-summary">
      🛒 <strong>{count}</strong> items · <strong>${total.toFixed(2)}</strong>
    </div>
  )
}

export default CartSummary
