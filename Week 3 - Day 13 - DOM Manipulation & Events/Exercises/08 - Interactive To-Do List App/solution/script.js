// Exercise 08 Solution: Interactive To-Do List App

const todoForm  = document.getElementById('todo-form');
const todoInput = document.getElementById('todo-input');
const todoList  = document.getElementById('todo-list');
const itemCount = document.getElementById('item-count');

// Helper: recalculate and display the current number of list items
function updateCount() {
  const count = todoList.querySelectorAll('li').length;
  itemCount.textContent = `Tasks: ${count}`;
}

// Requirement 2: Form submission — build and insert a new <li>
todoForm.addEventListener('submit', function(event) {
  event.preventDefault(); // stop the browser from sending an HTTP request

  const text = todoInput.value.trim();
  if (!text) return; // Requirement 8: ignore whitespace-only input

  // Build the <li> structure: <li><span class="todo-text">…</span><button class="delete-btn">Delete</button></li>
  const li = document.createElement('li');

  const span = document.createElement('span');
  span.classList.add('todo-text');
  span.textContent = text;

  const deleteBtn = document.createElement('button');
  deleteBtn.classList.add('delete-btn');
  deleteBtn.textContent = 'Delete';

  li.appendChild(span);
  li.appendChild(deleteBtn);
  todoList.appendChild(li);

  todoInput.value = ''; // clear input
  todoInput.focus();    // return focus so user can type immediately

  updateCount(); // Requirement 3 & 7
});

// Requirement 4: Single delegation listener — handles both delete and toggle
todoList.addEventListener('click', function(event) {
  // closest('li') walks up from event.target to find the containing <li>
  const li = event.target.closest('li');
  if (!li) return; // click landed outside any <li> (e.g., on the <ul> padding)

  if (event.target.classList.contains('delete-btn')) {
    // Requirement 6: remove only this <li>
    li.remove();
    updateCount(); // Requirement 3 & 7
  } else if (event.target.classList.contains('todo-text')) {
    // Requirement 5: toggle strikethrough via "done" class
    li.classList.toggle('done');
  }
});
