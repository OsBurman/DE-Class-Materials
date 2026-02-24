// Exercise 04: Modifying Styles Classes and Attributes

const card = document.getElementById('card');
const toggleBtn = document.getElementById('toggle-btn');
const statusText = document.getElementById('status-text');

// TODO: Requirement 2 — use classList.add to add the class "active" to card,
//       then log whether card has "active" using classList.contains
//       Format: 'Card has "active": true'

// TODO: Requirement 3 — use classList.remove to remove "active" from card,
//       then log classList.contains('active') again
//       Format: 'Card has "active": false'

// TODO: Requirement 4 — use classList.toggle to toggle "highlight" on card,
//       then log card.className
//       Format: 'Card className after toggle: highlight'

// TODO: Requirement 5 — use the style property to set card's backgroundColor to
//       "#d0e8ff", border to "2px solid #0077cc", and padding to "1rem"
//       (Remember: CSS property names become camelCase on the style object)

// TODO: Requirement 6 — use setAttribute to set data-status to "active" on card,
//       then use getAttribute to read it back and log it
//       Format: 'data-status value: active'

// TODO: Requirement 7 — use removeAttribute to remove data-status from card,
//       then log getAttribute('data-status')
//       Format: 'data-status after remove: null'

// TODO: Requirement 8 — access the existing data-user-id attribute via the dataset API
//       (card.dataset.userId) and log it
//       Format: 'data-user-id via dataset: 42'

// TODO: Requirement 9 — disable the toggleBtn using setAttribute,
//       then set statusText.textContent to "Button is disabled"
