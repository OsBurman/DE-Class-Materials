import { useState, useRef, useEffect } from 'react';

const CATEGORIES = ['Food', 'Transport', 'Entertainment', 'Other'];

export default function ExpenseForm({ onAddExpense }) {
  // TODO Task 3: Add useState for description, amount, and category
  // const [description, setDescription] = useState('');
  // const [amount, setAmount] = useState('');
  // const [category, setCategory] = useState('Food');
  // const [error, setError] = useState('');

  // TODO Task 4: Create a ref for the description input
  // const descriptionRef = useRef(null);

  // TODO Task 4: useEffect to auto-focus the description input on mount
  // Dependency array: []

  function handleSubmit(e) {
    e.preventDefault();
    // TODO Task 5: Validate — description not empty, amount > 0
    // If invalid, setError(...) and return.

    // TODO Task 5: Call onAddExpense with a new expense object:
    // { id: Date.now(), description, amount: parseFloat(amount), category }

    // TODO Task 5: Reset form fields to initial values
    // TODO Task 4: Re-focus the description input after submit
  }

  return (
    <div className="expense-form">
      <h2>Add Expense</h2>
      {/* TODO: Display error message if error is set */}
      <form onSubmit={handleSubmit}>
        <div className="form-grid">
          {/* TODO Task 3: Make this a controlled input */}
          <input
            className="full-width"
            type="text"
            placeholder="Description"
            {/* ref={descriptionRef} */}
          />
          <input
            type="number"
            placeholder="Amount ($)"
            min="0"
            step="0.01"
          />
          {/* TODO Task 3: Controlled select */}
          <select>
            {CATEGORIES.map(cat => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </select>
        </div>
        <button type="submit" className="submit-btn">Add Expense</button>
      </form>
    </div>
  );
}
