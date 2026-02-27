import './App.css'
import RecipeCard from './components/RecipeCard'

// Recipe data — in a real app this would come from an API
const pastaRecipe = {
  name: 'Spaghetti Carbonara',
  description: 'A rich and creamy classic Roman pasta dish made with eggs, Pecorino Romano, pancetta, and black pepper. No cream needed!',
  prepTime: '30 min',
  servings: 4,
  featured: true,
  ingredients: [
    '400g spaghetti',
    '200g pancetta or guanciale',
    '4 large eggs',
    '100g Pecorino Romano, grated',
    '50g Parmesan, grated',
    'Freshly ground black pepper',
    'Salt for pasta water',
  ],
  steps: [
    'Bring a large pot of salted water to a boil.',
    'Fry the pancetta in a large skillet over medium heat until crispy. Remove from heat.',
    'Cook spaghetti until al dente. Reserve 1 cup of pasta water before draining.',
    'Whisk eggs with grated Pecorino Romano and Parmesan in a bowl. Season with black pepper.',
    'Add hot drained pasta to the pancetta skillet (off heat). Toss to coat in fat.',
    'Pour egg mixture over pasta, tossing quickly. Add pasta water a little at a time until creamy.',
    'Serve immediately with extra cheese and black pepper.',
  ],
}

const saladRecipe = {
  name: 'Classic Caesar Salad',
  description: 'A timeless salad with crisp romaine lettuce, homemade croutons, and a tangy Caesar dressing. Quick, fresh, and satisfying.',
  prepTime: '15 min',
  servings: 2,
  featured: false,
  ingredients: [
    '1 large head romaine lettuce',
    '1 cup croutons',
    '60g Parmesan, shaved',
    '3 tbsp Caesar dressing',
    '1 lemon (for juice)',
    'Freshly ground black pepper',
  ],
  steps: [
    'Wash and dry romaine leaves thoroughly, then tear into bite-sized pieces.',
    'Toss romaine with Caesar dressing until evenly coated.',
    'Add croutons and toss gently.',
    'Top with shaved Parmesan and freshly ground black pepper.',
    'Squeeze lemon juice over the salad and serve immediately.',
  ],
}

export default function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>🍳 Recipe Book</h1>
        <p>Delicious recipes for every occasion</p>
      </header>

      <main className="recipes-grid">
        <RecipeCard recipe={pastaRecipe} />
        <RecipeCard recipe={saladRecipe} />
      </main>
    </div>
  )
}
