// ── State ────────────────────────────────────────────────────────────────

let todos = [];
let nextId = 1;
let currentFilter = 'all';

// ── DOM References (Task 1) ───────────────────────────────────────────────

// TODO Task 1: Select each element using a different method:
// const form = document.getElementById('todo-form');            // getElementById
// const input = document.querySelector('#todo-input');          // querySelector
// const todoList = document.getElementById('todo-list');
// const filterBtns = document.getElementsByClassName('filter-btn'); // getElementsByClassName
// const taskCount = document.querySelector('#task-count');

const form = null;       // replace with correct selector
const input = null;
const todoList = null;
const filterBtns = null;
const taskCount = null;


// ── Task 3: createTodoElement(todo) ──────────────────────────────────────
// Build the <li> node using ONLY createElement, textContent, classList, setAttribute, appendChild.
// Do NOT use innerHTML here.
function createTodoElement(todo) {
  const li = document.createElement('li');

  // TODO: Set data-id attribute on li: li.setAttribute('data-id', todo.id);

  // TODO: Create a checkbox input
  //   const checkbox = document.createElement('input');
  //   checkbox.type = 'checkbox';
  //   checkbox.checked = todo.completed;

  // TODO: Create a <span> with todo.title as textContent

  // TODO: Create a delete <button> with textContent "✕"

  // TODO: If todo.completed, add class 'completed' to li and set data-completed="true"

  // TODO: Append checkbox, span, and button to li

  return li;
}


// ── Task 4: renderTodos(filter) ──────────────────────────────────────────
function renderTodos(filter = 'all') {
  if (!todoList) return;
  todoList.innerHTML = '';

  // TODO: Filter the todos array based on filter value: 'all', 'active', 'completed'
  // const filtered = todos.filter(todo => { ... });

  // TODO: filtered.forEach(todo => todoList.appendChild(createTodoElement(todo)));

  // TODO Task 2: After appending, log todoList.children.length and
  //              todoList.firstElementChild?.nextElementSibling to demo traversal

  // TODO Task 10: Update task count
  // const remaining = todos.filter(t => !t.completed).length;
  // if (taskCount) taskCount.textContent = `${remaining} task(s) remaining`;
}


// ── Task 5: Form submit event ─────────────────────────────────────────────
// TODO: Add event listener to form for 'submit'
// - Call event.preventDefault()
// - Validate input is not empty/whitespace
// - Push { id: nextId++, title: input.value.trim(), completed: false } to todos
// - Clear the input
// - Call renderTodos(currentFilter)


// ── Task 6: Event delegation on todoList ─────────────────────────────────
// TODO: Add a single 'click' event listener to todoList
// Use event.target to check:
//   - If input[type=checkbox]: find todo by closest li's data-id, toggle completed, re-render
//   - If delete button: find todo by closest li's data-id, remove from todos, re-render
//
// Add a comment explaining WHY delegation is preferred over per-item listeners.


// ── Task 7: Filter buttons ────────────────────────────────────────────────
// TODO: Add 'click' listeners to each filter button
// On click:
//   1. Set currentFilter to event.target.dataset.filter
//   2. Remove 'active' class from ALL filter buttons (loop over filterBtns)
//   3. Add 'active' class to the clicked button
//   4. Call renderTodos(currentFilter)


// ── Task 8: Keyboard events ───────────────────────────────────────────────
// TODO: Add a 'keydown' listener to document
// - If key === 'Escape': clear the input value
// - If key === 'Enter' AND document.activeElement === input: form.requestSubmit()


// ── Init ──────────────────────────────────────────────────────────────────
renderTodos();
