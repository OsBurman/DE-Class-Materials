// Exercise 07: Preventing Default Behavior and Form Events

// TODO: Requirement 2 — attach a "DOMContentLoaded" listener to document.
//       Inside the handler, log "DOM is ready" to the console.
//       (Even though the script is deferred, attaching this listener is good practice.)

const form       = document.getElementById('signup-form');
const nameField  = document.getElementById('name-field');
const emailField = document.getElementById('email-field');
const roleField  = document.getElementById('role-field');
const feedback   = document.getElementById('feedback');
const navLink    = document.getElementById('nav-link');

// TODO: Requirement 3 — attach a "submit" listener to form.
//       - Call event.preventDefault() to stop the page reload.
//       - Read the values of nameField, emailField, and roleField.
//       - If nameField.value is empty (after trim), set feedback.textContent
//         to "Error: Name is required." and return.
//       - Otherwise, set feedback.innerHTML to show the submitted data:
//         "<strong>Submitted!</strong> Name: [name], Email: [email], Role: [role]"

// TODO: Requirement 4 — attach an "input" listener to nameField.
//       If the value is non-empty, add the class "valid" to nameField.
//       If the value becomes empty, remove the class "valid".

// TODO: Requirement 5 — attach a "change" listener to roleField.
//       Log "Role changed to: " + event.target.value when the selection changes.

// TODO: Requirement 6 — attach a "click" listener to navLink.
//       Call event.preventDefault() to stop navigation.
//       Log "Navigation prevented" to the console.
//       Set feedback.textContent to "Link click intercepted!"
