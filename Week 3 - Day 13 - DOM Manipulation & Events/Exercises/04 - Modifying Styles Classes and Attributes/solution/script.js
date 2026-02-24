// Exercise 04 Solution: Modifying Styles Classes and Attributes

const card = document.getElementById('card');
const toggleBtn = document.getElementById('toggle-btn');
const statusText = document.getElementById('status-text');

// Requirement 2: classList.add then classList.contains
card.classList.add('active');
console.log(`Card has "active": ${card.classList.contains('active')}`); // true

// Requirement 3: classList.remove then classList.contains
card.classList.remove('active');
console.log(`Card has "active": ${card.classList.contains('active')}`); // false

// Requirement 4: classList.toggle adds "highlight" (since it wasn't present)
card.classList.toggle('highlight');
console.log(`Card className after toggle: ${card.className}`); // "highlight"

// Requirement 5: Inline style via the style object — camelCase property names
card.style.backgroundColor = '#d0e8ff';
card.style.border = '2px solid #0077cc';
card.style.padding = '1rem'; // overrides the CSS rule padding

// Requirement 6: setAttribute + getAttribute
card.setAttribute('data-status', 'active');
console.log(`data-status value: ${card.getAttribute('data-status')}`); // "active"

// Requirement 7: removeAttribute — getAttribute returns null for missing attributes
card.removeAttribute('data-status');
console.log(`data-status after remove: ${card.getAttribute('data-status')}`); // null

// Requirement 8: dataset API — data-user-id maps to dataset.userId (camelCase)
console.log(`data-user-id via dataset: ${card.dataset.userId}`); // "42"

// Requirement 9: disable button with setAttribute; update status text
toggleBtn.setAttribute('disabled', '');
statusText.textContent = 'Button is disabled';
