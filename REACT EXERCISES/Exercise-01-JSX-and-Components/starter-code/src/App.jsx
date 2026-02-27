// TODO 3: Import RecipeCard from './components/RecipeCard'

// ─────────────────────────────────────────────────────────────────────────────
// TODO 1: Define the pastaRecipe object with these fields:
//   name, description, prepTime, servings, featured (boolean), ingredients (array), steps (array)
//
// Example:
//   const pastaRecipe = {
//     name: 'Spaghetti Carbonara',
//     description: 'A classic Roman pasta dish ...',
//     prepTime: '30 min',
//     servings: 4,
//     featured: true,
//     ingredients: ['400g spaghetti', '200g pancetta', '4 eggs', '100g Pecorino Romano', 'Black pepper'],
//     steps: ['Boil salted water ...', 'Fry pancetta ...', ...],
//   }
// ─────────────────────────────────────────────────────────────────────────────

// TODO 2: Define the saladRecipe object similarly (featured: false)

export default function App() {
  return (
    <div className="app">
      <header className="app-header">
        <h1>🍳 Recipe Book</h1>
        <p>Delicious recipes for every occasion</p>
      </header>

      <main className="recipes-grid">
        {/* TODO 4: Render <RecipeCard recipe={pastaRecipe} /> here */}
        {/* TODO 4: Render <RecipeCard recipe={saladRecipe} /> here */}
      </main>
    </div>
  )
}
