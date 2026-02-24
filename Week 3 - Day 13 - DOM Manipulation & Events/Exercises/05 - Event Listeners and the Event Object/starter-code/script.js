// Exercise 05: Event Listeners and the Event Object

const colorBtn  = document.getElementById('color-btn');
const removeBtn = document.getElementById('remove-btn');
const textInput = document.getElementById('text-input');
const output    = document.getElementById('output');

// TODO: Requirement 2 & 5 — define a NAMED function called handleColorClick.
//       Inside it, log event.type, event.target.id, and event.currentTarget.id,
//       then toggle the "active" class on the button.
//       Then attach it to colorBtn using addEventListener.

// TODO: Requirement 3 — attach a "keydown" listener to textInput.
//       In the handler, log event.key and event.type.
//       If event.key === "Enter", set output.textContent to "You pressed Enter!"

// TODO: Requirement 4 — attach an "input" listener to textInput.
//       In the handler, set output.textContent to "You typed: " + event.target.value

// TODO: Requirement 5 — attach a "click" listener to removeBtn.
//       In the handler, use removeEventListener to remove handleColorClick from colorBtn,
//       then log "Listener removed"
