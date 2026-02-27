# Exercise 01 — JSX & Components

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Write valid **JSX** syntax and understand how it differs from HTML
- Create **functional components** with and without props
- **Destructure props** in function parameters
- Pass data from parent to child using **props**
- Compose components — use one component inside another
- Render **nested data** received via props

---

## 📋 What You're Building
A **Recipe Card App** that displays two recipe cards side by side, each showing the recipe name, description, metadata, an ingredients list, and step-by-step cooking instructions.

```
┌────────────────────────────────────────────────────────┐
│  🍳 Recipe Book                                        │
│                                                        │
│  ┌──────────────────────┐  ┌──────────────────────┐   │
│  │  🍝 Spaghetti        │  │  🥗 Caesar Salad     │   │
│  │  Carbonara           │  │                      │   │
│  │  ⏱ 30 min  👤 4      │  │  ⏱ 15 min  👤 2     │   │
│  │  ⭐ Featured         │  │                      │   │
│  │                      │  │  Ingredients:        │   │
│  │  Ingredients:        │  │  • Romaine lettuce   │   │
│  │  • Spaghetti         │  │  • Parmesan          │   │
│  │  • Eggs              │  │  • Croutons          │   │
│  │                      │  │                      │   │
│  │  Steps:              │  │  Steps:              │   │
│  │  1. Boil pasta       │  │  1. Wash lettuce     │   │
│  └──────────────────────┘  └──────────────────────┘   │
└────────────────────────────────────────────────────────┘
```

---

## 🏗️ Project Setup
```bash
cd "Exercise-01-JSX-and-Components/starter-code"
npm install
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

---

## 📁 File Structure
```
src/
├── main.jsx
├── index.css
├── App.jsx                        ← renders two <RecipeCard> components
├── App.css
└── components/
    ├── RecipeCard.jsx             ← displays one recipe (uses IngredientList + StepList)
    ├── RecipeCard.css
    ├── IngredientList.jsx         ← renders a <ul> of ingredients
    └── StepList.jsx               ← renders an <ol> of steps
```

---

## ✅ TODOs

### `App.jsx`
- [ ] **TODO 1**: Define the `pastaRecipe` data object (see the structure below)
- [ ] **TODO 2**: Define the `saladRecipe` data object
- [ ] **TODO 3**: Import the `RecipeCard` component
- [ ] **TODO 4**: Render `<RecipeCard recipe={pastaRecipe} />` and `<RecipeCard recipe={saladRecipe} />`

### `components/RecipeCard.jsx`
- [ ] **TODO 5**: Accept a `recipe` prop in the component function — destructure it: `function RecipeCard({ recipe })`
- [ ] **TODO 6**: Destructure the recipe fields inside the component: `const { name, description, prepTime, servings, featured, ingredients, steps } = recipe`
- [ ] **TODO 7**: Render the recipe `name` in an `<h2>` tag
- [ ] **TODO 8**: Render `description`, `prepTime`, and `servings` using JSX interpolation `{}`
- [ ] **TODO 9**: Conditionally render a "⭐ Featured" badge **only if** `featured` is `true` (use `&&`)
- [ ] **TODO 10**: Render `<IngredientList ingredients={ingredients} />`
- [ ] **TODO 11**: Render `<StepList steps={steps} />`

### `components/IngredientList.jsx`
- [ ] **TODO 12**: Accept an `ingredients` array as a prop
- [ ] **TODO 13**: Render a `<ul>` containing one `<li>` for each ingredient
- [ ] **TODO 14**: Add a `key` prop to each `<li>` — use the ingredient string itself as the key

### `components/StepList.jsx`
- [ ] **TODO 15**: Accept a `steps` array as a prop
- [ ] **TODO 16**: Render an `<ol>` containing one `<li>` for each step
- [ ] **TODO 17**: Add a `key` prop to each `<li>` — use the step index as the key

---

## 📐 Recipe Data Shape
```js
{
  name: string,
  description: string,
  prepTime: string,       // e.g. "30 min"
  servings: number,
  featured: boolean,
  ingredients: string[],
  steps: string[],
}
```

---

## 💡 Key Concepts to Remember

| Concept | Example |
|---------|---------|
| JSX expression | `<h1>{title}</h1>` |
| Props | `<Card name="Alice" age={30} />` |
| Destructuring props | `function Card({ name, age }) {...}` |
| Conditional render | `{featured && <Badge />}` |
| Rendering a list | `{items.map(item => <li key={item}>{item}</li>)}` |
| Component composition | Using `<IngredientList />` inside `<RecipeCard />` |
