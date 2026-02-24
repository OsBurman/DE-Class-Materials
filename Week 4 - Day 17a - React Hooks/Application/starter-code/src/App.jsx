// TODO Task 8: Import useLocalStorage from './hooks/useLocalStorage'
// TODO Task 10: Import ThemeProvider and useTheme from './context/ThemeContext'
import ExpenseForm from './components/ExpenseForm';
import ExpenseList from './components/ExpenseList';
import ExpenseSummary from './components/ExpenseSummary';
import { useState, useEffect } from 'react';

// TODO Task 10: Create an inner AppContent component that uses useTheme()
// and wrap the default export in <ThemeProvider>

export default function App() {
  // TODO Task 8: Replace useState below with your useLocalStorage hook
  // so expenses persist across page refreshes.
  // Key: 'expenses', initial value: []
  const [expenses, setExpenses] = useState([]);

  // TODO Task 9: useEffect — update document.title to "Expense Tracker — $X.XX"
  // whenever the total changes. Calculate total from expenses array.

  // TODO Task 10: Read theme from useTheme(). Apply it as className on the wrapper div.

  function handleAddExpense(expense) {
    // TODO: Add the new expense to the expenses array.
    // Each expense should have a unique id (use Date.now()), description, amount, category.
  }

  function handleDeleteExpense(id) {
    // TODO: Remove the expense with the matching id from the expenses array.
  }

  return (
    <div className="app">
      {/* TODO Task 10: Add a theme toggle button that calls toggleTheme() */}
      <header>
        <h1>💸 Expense Tracker</h1>
      </header>
      <main>
        <ExpenseForm onAddExpense={handleAddExpense} />
        <ExpenseSummary expenses={expenses} />
        <ExpenseList expenses={expenses} onDelete={handleDeleteExpense} />
      </main>
    </div>
  );
}
