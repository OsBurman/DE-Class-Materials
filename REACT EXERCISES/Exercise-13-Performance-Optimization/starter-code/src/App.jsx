import { useState } from 'react'
import './App.css'
import { PRODUCTS, CATEGORIES_LIST } from './data/products'
import Toolbar from './components/Toolbar'
import ProductCard from './components/ProductCard'
import CartSummary from './components/CartSummary'

function App() {
  const [search, setSearch] = useState('')
  const [category, setCategory] = useState('All')
  const [minPrice, setMinPrice] = useState(0)
  const [cart, setCart] = useState([])

  // TODO 2: Wrap this derivation in useMemo.
  // Dependencies: search, category, minPrice
  const filteredProducts = PRODUCTS.filter(p => {
    const matchesSearch = p.name.toLowerCase().includes(search.toLowerCase())
    const matchesCategory = category === 'All' || p.category === category
    const matchesPrice = p.price >= minPrice
    return matchesSearch && matchesCategory && matchesPrice
  })

  // TODO 3: Wrap this derivation in useMemo.
  // Dependencies: cart
  const cartTotal = cart.reduce((sum, id) => {
    const product = PRODUCTS.find(p => p.id === id)
    return sum + (product ? product.price : 0)
  }, 0)

  // TODO 4: Wrap this function in useCallback.
  // Dependencies: [] — it uses the functional updater form so needs no deps
  function handleToggleCart(id) {
    setCart(prev =>
      prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
    )
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>🛍️ Product Catalog</h1>
        <CartSummary count={cart.length} total={cartTotal} />
      </header>

      <Toolbar
        search={search}
        onSearchChange={setSearch}
        category={category}
        onCategoryChange={setCategory}
        minPrice={minPrice}
        onMinPriceChange={setMinPrice}
        categories={CATEGORIES_LIST}
        resultCount={filteredProducts.length}
      />

      <main className="products-grid">
        {filteredProducts.map(product => (
          <ProductCard
            key={product.id}
            product={product}
            inCart={cart.includes(product.id)}
            onToggleCart={handleToggleCart}
          />
        ))}
      </main>
    </div>
  )
}

export default App
