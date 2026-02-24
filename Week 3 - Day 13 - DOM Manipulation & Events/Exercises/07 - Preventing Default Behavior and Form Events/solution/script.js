// Exercise 07 Solution: Preventing Default Behavior and Form Events

// Requirement 2: DOMContentLoaded fires when HTML parsing is complete
document.addEventListener('DOMContentLoaded', function() {
  console.log('DOM is ready');
});

const form       = document.getElementById('signup-form');
const nameField  = document.getElementById('name-field');
const emailField = document.getElementById('email-field');
const roleField  = document.getElementById('role-field');
const feedback   = document.getElementById('feedback');
const navLink    = document.getElementById('nav-link');

// Requirement 3: Intercept form submission — prevent reload, validate, display result
form.addEventListener('submit', function(event) {
  event.preventDefault(); // MUST call before any async work; stops HTTP request + reload

  const name  = nameField.value.trim();
  const email = emailField.value.trim();
  const role  = roleField.value;

  // Simple validation: name is required
  if (!name) {
    feedback.textContent = 'Error: Name is required.';
    return; // bail out early — don't show success message
  }

  // All fields filled — show success
  feedback.innerHTML = `<strong>Submitted!</strong> Name: ${name}, Email: ${email}, Role: ${role}`;
});

// Requirement 4: Live validation — toggle "valid" class as user types
nameField.addEventListener('input', function(event) {
  if (event.target.value.trim() !== '') {
    nameField.classList.add('valid');    // green border when non-empty
  } else {
    nameField.classList.remove('valid'); // remove when cleared
  }
});

// Requirement 5: "change" fires when <select> value changes and the field loses focus
roleField.addEventListener('change', function(event) {
  console.log('Role changed to: ' + event.target.value);
});

// Requirement 6: Intercept anchor click — prevent navigation
navLink.addEventListener('click', function(event) {
  event.preventDefault(); // stops browser from following the href
  console.log('Navigation prevented');
  feedback.textContent = 'Link click intercepted!';
});
