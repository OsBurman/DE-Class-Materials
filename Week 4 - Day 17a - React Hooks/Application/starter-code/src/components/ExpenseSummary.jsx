export default function ExpenseSummary({ expenses }) {
  // TODO Task 7: Calculate total — sum of all expense amounts
  // const total = expenses.reduce(...)

  // TODO Task 7: Calculate breakdown by category using reduce
  // Result shape: { Food: 45.50, Transport: 12.00, ... }
  // Hint: expenses.reduce((acc, expense) => { ... }, {})

  return (
    <div className="expense-summary">
      <h2>Summary</h2>
      {/* TODO: Display total amount */}
      <div className="total">$0.00</div>
      {/* TODO: Display per-category breakdown */}
      {/* TODO: Display total count */}
    </div>
  );
}
