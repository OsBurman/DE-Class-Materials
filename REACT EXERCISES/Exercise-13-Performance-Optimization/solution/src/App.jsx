import { useState, useMemo, useCallback } from 'react'
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

  // useMemo: only recompute when filters change
  const filteredProducts = useMemo(() => {
    return PRODUCTS.filter(p => {
      const matchesSearch = p.name.toLowerCase().includes(search.toLowerCase())
      const matchesCategory = category === 'All' || p.category === category
      const matchesPrice = p.price >= minPrice
      return matchesSearch && matchesCategory && matchesPrice
    })
  }, [search, category, minPrice])

  // useMemo: only recompute when cart changes
  const cartTotal = useMemo(() => {
    return cart.reduce((sum, id) => {
      const product = PRODUCTS.find(p => p.id === id)
      return sum + (product ? product.price : 0)
    }, 0)
  }, [cart])

  // useCallback: stable reference — memo'd ProductCard won't re-render just because App re-rendered
  const handleToggleCart = useCallback((id) => {
    setCart(prev =>
      prev.includes(id) ? prev.filter(x => x !== id) : [...prev, id]
    )
  }, [])

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
