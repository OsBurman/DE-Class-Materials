import React, { useState } from 'react';
import { gql, useQuery, useMutation, useLazyQuery } from '@apollo/client';

// ── GraphQL Operations ────────────────────────────────────────────────────────

/**
 * gql template literal parses GraphQL query string into an AST.
 * Apollo uses the AST for caching and type checking.
 */
const GET_STUDENTS = gql`
  query GetStudents {
    students {
      id
      name
      major
      gpa
    }
  }
`;

const GET_STUDENT_WITH_COURSES = gql`
  query GetStudentWithCourses($id: ID!) {
    student(id: $id) {
      id
      name
      email
      major
      gpa
      courses {
        id
        title
        instructor
      }
    }
  }
`;

const GET_STUDENTS_BY_MAJOR = gql`
  query GetByMajor($major: String!) {
    studentsByMajor(major: $major) {
      id
      name
      gpa
    }
  }
`;

const CREATE_STUDENT = gql`
  mutation CreateStudent($input: CreateStudentInput!) {
    createStudent(input: $input) {
      id
      name
      email
      major
      gpa
    }
  }
`;

const DELETE_STUDENT = gql`
  mutation DeleteStudent($id: ID!) {
    deleteStudent(id: $id)
  }
`;

// ── Styles ────────────────────────────────────────────────────────────────────

const styles = {
  app: { maxWidth: 1000, margin: '0 auto', padding: 20 },
  header: { background: '#1976d2', color: 'white', padding: '16px 20px', borderRadius: 8, marginBottom: 20 },
  section: { background: 'white', border: '1px solid #ddd', borderRadius: 8, padding: 20, marginBottom: 20 },
  sectionTitle: { color: '#1976d2', marginTop: 0 },
  grid: { display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 12 },
  card: { border: '1px solid #e0e0e0', borderRadius: 8, padding: 12, background: '#fafafa' },
  button: { background: '#1976d2', color: 'white', border: 'none', padding: '8px 16px', borderRadius: 4, cursor: 'pointer', marginRight: 8 },
  deleteBtn: { background: '#d32f2f', color: 'white', border: 'none', padding: '4px 8px', borderRadius: 4, cursor: 'pointer', fontSize: 12 },
  input: { padding: '6px 10px', border: '1px solid #ccc', borderRadius: 4, marginRight: 8 },
  select: { padding: '6px 10px', border: '1px solid #ccc', borderRadius: 4, marginRight: 8 },
  form: { display: 'flex', flexDirection: 'column', gap: 10, maxWidth: 400 },
  error: { color: '#d32f2f', background: '#ffebee', padding: 10, borderRadius: 4 },
  loading: { color: '#888', fontStyle: 'italic' },
  badge: { background: '#e3f2fd', color: '#1565c0', padding: '2px 8px', borderRadius: 12, fontSize: 12 },
  code: { background: '#f5f5f5', padding: '8px 12px', borderRadius: 4, fontFamily: 'monospace', fontSize: 13, overflowX: 'auto' },
};

// ── Components ────────────────────────────────────────────────────────────────

/**
 * StudentList — demonstrates useQuery hook
 * useQuery runs automatically when component mounts.
 * Returns: { data, loading, error, refetch }
 */
function StudentList() {
  const { data, loading, error, refetch } = useQuery(GET_STUDENTS);
  const [deleteStudent] = useMutation(DELETE_STUDENT, {
    refetchQueries: [{ query: GET_STUDENTS }], // auto-refetch after mutation
  });

  if (loading) return <p style={styles.loading}>Loading students...</p>;
  if (error)   return <p style={styles.error}>Error: {error.message}</p>;

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h3 style={{ margin: 0, color: '#333' }}>Students ({data.students.length})</h3>
        <button style={styles.button} onClick={() => refetch()}>↻ Refetch</button>
      </div>
      <div style={{ ...styles.grid, marginTop: 12 }}>
        {data.students.map(s => (
          <div key={s.id} style={styles.card}>
            <strong>{s.name}</strong>
            <div style={{ margin: '4px 0' }}>
              <span style={styles.badge}>{s.major}</span>
            </div>
            <div>GPA: <strong style={{ color: s.gpa >= 3.5 ? '#2e7d32' : '#333' }}>{s.gpa}</strong></div>
            <button
              style={{ ...styles.deleteBtn, marginTop: 8 }}
              onClick={() => deleteStudent({ variables: { id: s.id } })}
            >
              Delete
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

/**
 * StudentDetail — demonstrates useQuery with variables
 * Passing variables to parameterized queries.
 */
function StudentDetail() {
  const [selectedId, setSelectedId] = useState('');
  const { data, loading, error } = useQuery(GET_STUDENT_WITH_COURSES, {
    variables: { id: selectedId },
    skip: !selectedId, // skip = don't run if no ID selected
  });

  return (
    <div>
      <div style={{ marginBottom: 12 }}>
        <label>Student ID: </label>
        <input
          style={styles.input}
          placeholder="Enter ID (1-5)"
          value={selectedId}
          onChange={e => setSelectedId(e.target.value)}
        />
      </div>
      {loading && <p style={styles.loading}>Loading...</p>}
      {error   && <p style={styles.error}>{error.message}</p>}
      {data?.student && (
        <div style={styles.card}>
          <h4 style={{ margin: '0 0 8px' }}>{data.student.name}</h4>
          <div>Email: {data.student.email}</div>
          <div>Major: {data.student.major} | GPA: {data.student.gpa}</div>
          <div style={{ marginTop: 8 }}>
            <strong>Enrolled Courses:</strong>
            {data.student.courses.length === 0
              ? <span style={{ color: '#888' }}> None</span>
              : data.student.courses.map(c => (
                  <div key={c.id} style={{ ...styles.card, margin: '4px 0' }}>
                    {c.title} — {c.instructor}
                  </div>
                ))
            }
          </div>
        </div>
      )}
    </div>
  );
}

/**
 * LazyQuery — demonstrates useLazyQuery
 * useLazyQuery doesn't run automatically — you call it manually.
 */
function MajorFilter() {
  const [major, setMajor] = useState('CS');
  const [loadStudents, { data, loading, error }] = useLazyQuery(GET_STUDENTS_BY_MAJOR);

  return (
    <div>
      <div style={{ marginBottom: 12 }}>
        <select style={styles.select} value={major} onChange={e => setMajor(e.target.value)}>
          <option value="CS">Computer Science</option>
          <option value="Math">Mathematics</option>
        </select>
        <button style={styles.button} onClick={() => loadStudents({ variables: { major } })}>
          Search
        </button>
      </div>
      {loading && <p style={styles.loading}>Searching...</p>}
      {error   && <p style={styles.error}>{error.message}</p>}
      {data && (
        <div>
          <strong>{data.studentsByMajor.length} students in {major}:</strong>
          {data.studentsByMajor.map(s => (
            <div key={s.id} style={{ ...styles.card, margin: '4px 0' }}>
              {s.name} — GPA: {s.gpa}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

/**
 * CreateStudent — demonstrates useMutation
 * useMutation returns [mutateFunction, { data, loading, error }]
 */
function CreateStudentForm() {
  const [form, setForm] = useState({ name: '', email: '', major: 'CS', gpa: '' });
  const [createStudent, { data, loading, error, reset }] = useMutation(CREATE_STUDENT, {
    refetchQueries: [{ query: GET_STUDENTS }],
    onCompleted: () => setForm({ name: '', email: '', major: 'CS', gpa: '' }),
  });

  const handleSubmit = (e) => {
    e.preventDefault();
    createStudent({
      variables: { input: { ...form, gpa: parseFloat(form.gpa) } }
    });
  };

  return (
    <form style={styles.form} onSubmit={handleSubmit}>
      <input style={styles.input} placeholder="Name *" value={form.name}
        onChange={e => setForm({...form, name: e.target.value})} required />
      <input style={styles.input} placeholder="Email *" value={form.email}
        onChange={e => setForm({...form, email: e.target.value})} required />
      <select style={styles.select} value={form.major}
        onChange={e => setForm({...form, major: e.target.value})}>
        <option value="CS">Computer Science</option>
        <option value="Math">Mathematics</option>
      </select>
      <input style={styles.input} placeholder="GPA (0-4)" value={form.gpa} type="number" step="0.1" min="0" max="4"
        onChange={e => setForm({...form, gpa: e.target.value})} required />
      <button style={styles.button} type="submit" disabled={loading}>
        {loading ? 'Creating...' : 'Create Student'}
      </button>
      {error && <p style={styles.error}>{error.message}</p>}
      {data  && <p style={{ color: '#2e7d32' }}>✓ Created: {data.createStudent.name}</p>}
    </form>
  );
}

// ── Reference Panel ───────────────────────────────────────────────────────────

function ApolloReference() {
  const [show, setShow] = useState(false);
  return (
    <div>
      <button style={styles.button} onClick={() => setShow(!show)}>
        {show ? 'Hide' : 'Show'} Apollo Reference
      </button>
      {show && (
        <div style={{ marginTop: 12 }}>
          <h4>Apollo Client Hooks</h4>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ background: '#e3f2fd' }}>
                <th style={{ padding: '8px', textAlign: 'left', border: '1px solid #ddd' }}>Hook</th>
                <th style={{ padding: '8px', textAlign: 'left', border: '1px solid #ddd' }}>When to Use</th>
                <th style={{ padding: '8px', textAlign: 'left', border: '1px solid #ddd' }}>Returns</th>
              </tr>
            </thead>
            <tbody>
              {[
                ['useQuery(QUERY)', 'Auto-runs on mount', '{ data, loading, error, refetch }'],
                ['useLazyQuery(QUERY)', 'Manual trigger', '[loadFn, { data, loading, error }]'],
                ['useMutation(MUTATION)', 'Write operations', '[mutateFn, { data, loading, error }]'],
                ['useSubscription(SUB)', 'Real-time updates', '{ data, loading, error }'],
              ].map(([hook, when, ret]) => (
                <tr key={hook}>
                  <td style={{ padding: '8px', border: '1px solid #ddd', fontFamily: 'monospace' }}>{hook}</td>
                  <td style={{ padding: '8px', border: '1px solid #ddd' }}>{when}</td>
                  <td style={{ padding: '8px', border: '1px solid #ddd', fontFamily: 'monospace', fontSize: 12 }}>{ret}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <h4>Key Concepts</h4>
          <ul>
            <li><strong>InMemoryCache:</strong> Apollo normalizes & caches all query results. No duplicate network requests for same data.</li>
            <li><strong>refetchQueries:</strong> After a mutation, specify which queries to re-run to keep UI in sync.</li>
            <li><strong>skip option:</strong> <code>useQuery(Q, {'{'} skip: !id {'}'}))</code> — don't run until condition met.</li>
            <li><strong>variables:</strong> Pass typed parameters to parameterized queries/mutations.</li>
            <li><strong>gql tag:</strong> Parses GraphQL string to AST — enables Apollo's caching by operation name.</li>
            <li><strong>ApolloProvider:</strong> Context provider — makes client available to all child components.</li>
          </ul>
        </div>
      )}
    </div>
  );
}

// ── Main App ──────────────────────────────────────────────────────────────────

export default function App() {
  return (
    <div style={styles.app}>
      <div style={styles.header}>
        <h1 style={{ margin: 0, fontSize: 24 }}>Day 32 — GraphQL Client: React + Apollo</h1>
        <p style={{ margin: '4px 0 0', opacity: 0.9 }}>
          Requires Day 31 server: cd ../../../Day 31.../part-1 &amp;&amp; mvn spring-boot:run
        </p>
      </div>

      <div style={styles.section}>
        <h2 style={styles.sectionTitle}>useQuery — Fetch All Students</h2>
        <StudentList />
      </div>

      <div style={styles.section}>
        <h2 style={styles.sectionTitle}>useQuery with Variables — Student Detail + Courses</h2>
        <StudentDetail />
      </div>

      <div style={styles.section}>
        <h2 style={styles.sectionTitle}>useLazyQuery — Filter by Major</h2>
        <MajorFilter />
      </div>

      <div style={styles.section}>
        <h2 style={styles.sectionTitle}>useMutation — Create Student</h2>
        <CreateStudentForm />
      </div>

      <div style={styles.section}>
        <h2 style={styles.sectionTitle}>Apollo Client Reference</h2>
        <ApolloReference />
      </div>
    </div>
  );
}
