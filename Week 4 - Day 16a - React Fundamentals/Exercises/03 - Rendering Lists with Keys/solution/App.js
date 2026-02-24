// Exercise 03 Solution: Rendering Lists with Keys

const students = [
  { id: 1, name: "Alice",   grade: "A", subject: "Mathematics" },
  { id: 2, name: "Bob",     grade: "F", subject: "Science"     },
  { id: 3, name: "Carol",   grade: "B", subject: "History"     },
  { id: 4, name: "David",   grade: "C", subject: "English"     },
  { id: 5, name: "Eve",     grade: "A", subject: "Art"         },
];

// StudentCard is a pure presentational component — it just renders its props.
function StudentCard({ name, grade, subject }) {
  return (
    <div className="student-card">
      <h3>{name}</h3>
      <p>{subject}</p>
      <p>Grade: {grade}</p>
    </div>
  );
}

// StudentList maps the full array.
// KEY RULE: key={student.id} goes on the element returned from .map(),
// which is <StudentCard> — NOT inside StudentCard itself.
// React uses the key internally; it is NOT passed as a prop to StudentCard.
function StudentList({ students }) {
  return (
    <div className="card-row">
      {students.map((student) => (
        <StudentCard
          key={student.id}
          name={student.name}
          grade={student.grade}
          subject={student.subject}
        />
      ))}
    </div>
  );
}

// PassingStudents filters first, then maps.
// We still use student.id — not the index — as the key so that if the
// underlying array changes, React can reconcile correctly.
function PassingStudents({ students }) {
  const passing = students.filter((s) => s.grade !== "F");
  return (
    <div className="card-row">
      {passing.map((student) => (
        <StudentCard
          key={student.id}
          name={student.name}
          grade={student.grade}
          subject={student.subject}
        />
      ))}
    </div>
  );
}

function App() {
  return (
    <div>
      <h1>Student Roster</h1>

      <h2>All Students</h2>
      <StudentList students={students} />

      <h2>Passing Students</h2>
      {/* Bob (grade "F") is excluded from this list */}
      <PassingStudents students={students} />
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
