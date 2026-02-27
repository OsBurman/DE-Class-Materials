import './RecipeCard.css'
// TODO 10: Import IngredientList from './IngredientList'
// TODO 11: Import StepList from './StepList'

// ─────────────────────────────────────────────────────────────────────────────
// TODO 5: Accept a `recipe` prop by destructuring it in the function signature:
//         function RecipeCard({ recipe }) { ... }
//
// TODO 6: Inside the component, destructure all fields from recipe:
//         const { name, description, prepTime, servings, featured, ingredients, steps } = recipe
// ─────────────────────────────────────────────────────────────────────────────

export default function RecipeCard(props) {
  // TODO 6: Destructure from props.recipe here

  return (
    <article className="recipe-card">
      <div className="recipe-header">
        {/* TODO 7: Render the recipe name in an <h2> */}

        {/* TODO 9: Conditionally render a featured badge — only show if featured is true
                    Use the && operator: {featured && <span className="badge">⭐ Featured</span>} */}
      </div>

      <p className="recipe-description">
        {/* TODO 8: Render the description */}
      </p>

      <div className="recipe-meta">
        {/* TODO 8: Render prepTime and servings */}
        <span>⏱ {/* prepTime */}</span>
        <span>👤 {/* servings */} servings</span>
      </div>

      <div className="recipe-sections">
        {/* TODO 10: Render <IngredientList ingredients={ingredients} /> */}

        {/* TODO 11: Render <StepList steps={steps} /> */}
      </div>
    </article>
  )
}
