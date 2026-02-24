// Exercise 05 Solution: Event Listeners and the Event Object

const colorBtn  = document.getElementById('color-btn');
const removeBtn = document.getElementById('remove-btn');
const textInput = document.getElementById('text-input');
const output    = document.getElementById('output');

// Requirement 2 & 5: Named function so we can remove it later.
// event.target    = element user interacted with (the button clicked)
// event.currentTarget = element the listener is attached to (same here, differs on bubbling)
function handleColorClick(event) {
  console.log(`Event type: ${event.type}`);          // "click"
  console.log(`Target id: ${event.target.id}`);      // "color-btn"
  console.log(`currentTarget id: ${event.currentTarget.id}`); // "color-btn"
  colorBtn.classList.toggle('active');               // visually toggles blue/default
}

colorBtn.addEventListener('click', handleColorClick);

// Requirement 3: keydown — fires on every key press
textInput.addEventListener('keydown', function(event) {
  console.log(`Key pressed: ${event.key}  (type: ${event.type})`);
  if (event.key === 'Enter') {
    output.textContent = 'You pressed Enter!';
  }
});

// Requirement 4: input — fires on every character change (live)
textInput.addEventListener('input', function(event) {
  output.textContent = 'You typed: ' + event.target.value;
});

// Requirement 5: removeEventListener — must pass the exact same function reference
removeBtn.addEventListener('click', function() {
  colorBtn.removeEventListener('click', handleColorClick);
  console.log('Listener removed');
});
