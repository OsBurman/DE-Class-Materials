import './RecipeCard.css'
import IngredientList from './IngredientList'
import StepList from './StepList'

// Destructure `recipe` directly from props in the function signature
export default function RecipeCard({ recipe }) {
  // Destructure all fields from the recipe object
  const { name, description, prepTime, servings, featured, ingredients, steps } = recipe

  return (
    <article className="recipe-card">
      <div className="recipe-header">
        <h2>{name}</h2>
        {/* Conditionally render the Featured badge using && operator */}
        {featured && <span className="badge">⭐ Featured</span>}
      </div>

      <p className="recipe-description">{description}</p>

      <div className="recipe-meta">
        <span>⏱ {prepTime}</span>
        <span>👤 {servings} servings</span>
      </div>

      <div className="recipe-sections">
        {/* Pass ingredients array as a prop to the child component */}
        <IngredientList ingredients={ingredients} />

        {/* Pass steps array as a prop to the child component */}
        <StepList steps={steps} />
      </div>
    </article>
  )
}
