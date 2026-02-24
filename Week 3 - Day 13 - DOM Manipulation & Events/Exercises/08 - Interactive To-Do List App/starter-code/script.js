// Exercise 08: Interactive To-Do List App

const todoForm  = document.getElementById('todo-form');
const todoInput = document.getElementById('todo-input');
const todoList  = document.getElementById('todo-list');
const itemCount = document.getElementById('item-count');

// TODO: Requirement 3 — write a helper function called updateCount() that sets
//       itemCount.textContent to "Tasks: N" where N is the current number of
//       <li> elements in todoList (use querySelectorAll('li').length)

// TODO: Requirement 2 — attach a "submit" listener to todoForm.
//       - Call event.preventDefault() to prevent page reload.
//       - Read todoInput.value and trim it. If empty, return.
//       - Create a new <li> element.
//       - Inside the <li>, create a <span class="todo-text"> with the task text.
//       - Inside the <li>, create a <button class="delete-btn"> with text "Delete".
//       - Append the span and button to the <li>, then append the <li> to todoList.
//       - Clear todoInput.value and call todoInput.focus().
//       - Call updateCount().

// TODO: Requirement 4 — attach a SINGLE "click" listener to todoList (event delegation).
//       Inside the handler:
//       - Find the closest <li> ancestor of event.target using event.target.closest('li').
//       - If event.target has class "delete-btn": remove the <li>, then call updateCount().
//       - If event.target has class "todo-text": toggle class "done" on the <li>.
