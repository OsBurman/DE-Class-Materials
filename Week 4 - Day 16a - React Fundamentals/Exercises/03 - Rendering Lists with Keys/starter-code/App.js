// Exercise 03: Rendering Lists with Keys

// Sample data — do not modify
const students = [
  { id: 1, name: "Alice",   grade: "A", subject: "Mathematics" },
  { id: 2, name: "Bob",     grade: "F", subject: "Science"     },
  { id: 3, name: "Carol",   grade: "B", subject: "History"     },
  { id: 4, name: "David",   grade: "C", subject: "English"     },
  { id: 5, name: "Eve",     grade: "A", subject: "Art"         },
];

// TODO 1: Create a StudentCard component that accepts name, grade, and subject.
//         Render a <div className="student-card"> containing:
//           - <h3> with the student's name
//           - <p> with subject
//           - <p> with "Grade: " + grade
function StudentCard({ name, grade, subject }) {
  return (
    <div className="student-card">
      {/* TODO: render name, subject, and grade */}
    </div>
  );
}

// TODO 2: Create a StudentList component that accepts a students array.
//         Use .map() to render a <StudentCard /> for each student.
//         IMPORTANT: Place key={student.id} on the <StudentCard /> inside .map()
function StudentList({ students }) {
  return (
    <div className="card-row">
      {/* TODO: map over students and return a keyed <StudentCard /> for each */}
    </div>
  );
}

// TODO 3: Create a PassingStudents component that accepts a students array,
//         filters to only students whose grade is NOT "F",
//         then renders the filtered list using .map() with key={student.id}.
function PassingStudents({ students }) {
  // TODO: filter, then map
  return (
    <div className="card-row">
    </div>
  );
}

// TODO 4: Complete the App component to render both lists.
function App() {
  return (
    <div>
      <h1>Student Roster</h1>

      <h2>All Students</h2>
      {/* TODO: Render <StudentList students={students} /> */}

      <h2>Passing Students</h2>
      {/* TODO: Render <PassingStudents students={students} /> */}
    </div>
  );
}

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
