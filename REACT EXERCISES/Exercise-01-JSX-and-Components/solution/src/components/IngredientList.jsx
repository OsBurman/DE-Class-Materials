// Accept `ingredients` by destructuring from props
export default function IngredientList({ ingredients }) {
  return (
    <div className="ingredient-list">
      <h3>🛒 Ingredients</h3>
      <ul>
        {/* Map over the ingredients array. Use the ingredient string itself as the key
            because ingredient names are unique within a single recipe */}
        {ingredients.map((ingredient) => (
          <li key={ingredient}>{ingredient}</li>
        ))}
      </ul>
    </div>
  )
}
