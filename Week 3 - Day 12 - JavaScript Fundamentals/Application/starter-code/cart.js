'use strict';
// TODO Task 10: 'use strict' is already added above.
// Add a comment block below explaining what strict mode prevents.
// Example: trying to use an undeclared variable would throw a ReferenceError.

// ── Data ─────────────────────────────────────────────────────────────────

// TODO Task 1: The products array is provided — use const (never reassigned)
const products = [
  { id: 1, name: "Mechanical Keyboard",  price: 89.99,  emoji: "⌨️" },
  { id: 2, name: "Wireless Mouse",       price: 34.99,  emoji: "🖱️" },
  { id: 3, name: "USB-C Hub",            price: 49.99,  emoji: "🔌" },
  { id: 4, name: "Monitor Stand",        price: 29.99,  emoji: "🖥️" },
  { id: 5, name: "Desk Lamp",            price: 24.99,  emoji: "💡" },
];

// TODO Task 1: Declare the cart array with let
// let cart = [];


// TODO Task 2: Call initCart() HERE — before the function is defined below.
// Add a comment explaining why this works (hoisting of function declarations).
// initCart();


// ── Functions ─────────────────────────────────────────────────────────────

// TODO Task 2: initCart — function declaration (hoisted)
// Render product cards in #product-grid with "Add to Cart" buttons.
function initCart() {
  const grid = document.getElementById('product-grid');
  // Use products.map() and template literals to generate HTML for each product
  // grid.innerHTML = products.map(p => `...`).join('');
  renderCart();
}


// TODO Task 3: addToCart — function declaration
// Check if product already in cart with cart.find(), increment qty or push new entry
function addToCart(productId) {
}


// TODO Task 4: removeFromCart — arrow function
// Use cart.filter() — reassign to cart
const removeFromCart = (productId) => {
};


// TODO Task 5: updateQuantity — function expression
// If newQty <= 0, remove item; otherwise update quantity
const updateQuantity = function(productId, newQty) {
};


// TODO Task 6: getTotal — arrow function
// Use cart.reduce() — for each item look up price with products.find()
const getTotal = () => {
  return 0;
};


// TODO Task 7: renderCart — function declaration
// Use cart.map() + template literals to build HTML for each cart item
// Set document.getElementById('cart-items').innerHTML
function renderCart() {
  const cartItemsEl = document.getElementById('cart-items');
  if (!cartItemsEl) return;
  // cartItemsEl.innerHTML = cart.map(item => {
  //   const product = products.find(p => p.id === item.productId);
  //   return `...template literal with emoji, name, qty controls, price, remove button...`;
  // }).join('');
  renderTotal();
}


// TODO Task 8: renderTotal — arrow function
// Display total with toFixed(2); show "Cart is empty" if cart.length === 0
const renderTotal = () => {
  const totalEl = document.getElementById('cart-total');
  if (!totalEl) return;
  // const discount = createDiscountCalculator(10);
  // const total = getTotal();
  // const finalTotal = total > 50 ? discount(total) : total;
  // totalEl.innerHTML = `...`;
};


// TODO Task 9: createDiscountCalculator — closure factory
// Returns a function that applies a percentage discount to a price
function createDiscountCalculator(discountPercent) {
  // Return a function that takes a price and returns the discounted price.
  // The inner function "closes over" discountPercent.
  return function(price) {
    return price; // replace with discounted price calculation
  };
}
