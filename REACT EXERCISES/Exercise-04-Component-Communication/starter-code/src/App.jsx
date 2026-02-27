// TODO 1: Import useState from 'react'
import './App.css'
import ProductCatalog from './components/ProductCatalog'
import Cart from './components/Cart'

const PRODUCTS = [
  { id: 1, name: 'Wireless Headphones', price: 79.99, emoji: '🎧' },
  { id: 2, name: 'Mechanical Keyboard', price: 129.99, emoji: '⌨️' },
  { id: 3, name: 'USB-C Hub', price: 49.99, emoji: '🔌' },
  { id: 4, name: 'Webcam HD', price: 89.99, emoji: '📷' },
  { id: 5, name: 'Desk Lamp', price: 34.99, emoji: '💡' },
  { id: 6, name: 'Mouse Pad XL', price: 24.99, emoji: '🖱️' },
]

export default function App() {
  // TODO 1: Declare cartItems state — initialize as []
  // const [cartItems, setCartItems] = useState([])

  // TODO 2: Implement addToCart(product)
  //   - Check if product is already in cart: cartItems.some(item => item.id === product.id)
  //   - If yes: increment that item's quantity by 1
  //   - If no: add { ...product, quantity: 1 } to the array
  function addToCart(product) {
    // your code here
  }

  // TODO 3: Implement removeFromCart(id)
  //   Filter out the item with the matching id
  function removeFromCart(id) {
    // your code here
  }

  // TODO 4: Implement updateQuantity(id, newQty)
  //   If newQty < 1, remove the item entirely (call removeFromCart)
  //   Otherwise, map over cartItems and update the matching item's quantity
  function updateQuantity(id, newQty) {
    // your code here
  }

  // TODO 5: Compute cartTotal — multiply each item's price × quantity and sum them
  //   Use reduce: cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0)
  const cartTotal = 0 // replace this

  // TODO 6: Compute itemCount — sum of all item quantities
  const itemCount = 0 // replace this

  return (
    <div className="app">
      <header className="app-header">
        <h1>🛒 Tech Store</h1>
        {/* TODO 6: Show "Cart (X items)" badge in the header */}
        <span className="cart-badge">Cart (0 items)</span>
      </header>

      <div className="store-layout">
        {/* TODO 7: Pass onAddToCart={addToCart} and products={PRODUCTS} */}
        <ProductCatalog products={PRODUCTS} onAddToCart={() => {}} />

        {/* TODO 8: Pass cartItems, onRemove, onUpdateQuantity, cartTotal, itemCount */}
        <Cart
          cartItems={[]}
          onRemove={() => {}}
          onUpdateQuantity={() => {}}
          cartTotal={0}
          itemCount={0}
        />
      </div>
    </div>
  )
}
