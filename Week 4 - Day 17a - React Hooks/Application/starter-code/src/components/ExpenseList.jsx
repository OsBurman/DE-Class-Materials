export default function ExpenseList({ expenses, onDelete }) {
  // TODO Task 6: If expenses is empty, show an empty state message.

  return (
    <div className="expense-list">
      <h2>Expenses ({expenses.length})</h2>
      {/* TODO Task 6: Map over expenses. Each item shows:
           - description
           - category badge (className="category-badge")
           - amount formatted as "$X.XX" (className="amount")
           - a delete button that calls onDelete(expense.id)
      */}
      <p className="empty-msg">No expenses yet. Add one above!</p>
    </div>
  );
}
