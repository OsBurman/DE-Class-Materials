// TODO 12: Accept an `ingredients` prop — destructure it in the function signature:
//          function IngredientList({ ingredients }) { ... }

export default function IngredientList(props) {
  return (
    <div className="ingredient-list">
      <h3>🛒 Ingredients</h3>

      {/* TODO 13: Render a <ul> with one <li> for each ingredient
                  Use the .map() method: ingredients.map(ingredient => ...)

          TODO 14: Each <li> needs a `key` prop — use the ingredient string itself:
                   <li key={ingredient}>{ingredient}</li>  */}
    </div>
  )
}
