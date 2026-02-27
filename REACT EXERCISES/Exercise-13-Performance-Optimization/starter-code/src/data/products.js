const CATEGORIES = ['Electronics', 'Books', 'Clothing', 'Home & Garden', 'Sports', 'Toys']
const ADJECTIVES = ['Premium', 'Deluxe', 'Pro', 'Elite', 'Classic', 'Modern', 'Smart', 'Ultra']
const NOUNS = ['Widget', 'Gadget', 'Device', 'Tool', 'Kit', 'Set', 'Pack', 'Bundle']

export const PRODUCTS = Array.from({ length: 500 }, (_, i) => ({
  id: i + 1,
  name: `${ADJECTIVES[i % ADJECTIVES.length]} ${NOUNS[i % NOUNS.length]} ${i + 1}`,
  category: CATEGORIES[i % CATEGORIES.length],
  price: Math.round((9.99 + (i * 3.17) % 490) * 100) / 100,
  rating: Math.round((3 + (i * 0.31) % 2) * 10) / 10,
  stock: (i * 7) % 50 + 1,
}))

export const CATEGORIES_LIST = ['All', ...CATEGORIES]
