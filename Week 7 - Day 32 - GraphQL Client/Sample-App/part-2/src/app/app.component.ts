import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Apollo, gql } from 'apollo-angular';
import { Subscription } from 'rxjs';

// ── GraphQL Operations ────────────────────────────────────────────────────────

const GET_STUDENTS = gql`
  query GetStudents {
    students {
      id name major gpa
    }
  }
`;

const GET_STUDENT_DETAIL = gql`
  query GetStudentDetail($id: ID!) {
    student(id: $id) {
      id name email major gpa
      courses { id title instructor }
    }
  }
`;

const CREATE_STUDENT = gql`
  mutation CreateStudent($input: CreateStudentInput!) {
    createStudent(input: $input) {
      id name email major gpa
    }
  }
`;

const DELETE_STUDENT = gql`
  mutation DeleteStudent($id: ID!) {
    deleteStudent(id: $id)
  }
`;

// ── Types ─────────────────────────────────────────────────────────────────────

interface Student {
  id: string;
  name: string;
  email?: string;
  major: string;
  gpa: number;
  courses?: Course[];
}

interface Course {
  id: string;
  title: string;
  instructor: string;
}

/**
 * Day 32 — Part 2: GraphQL Client — Angular + Apollo Angular
 * ============================================================
 *
 * apollo-angular uses RxJS Observables.
 * Key methods:
 *   this.apollo.watchQuery()  — subscribes + watches for cache updates
 *   this.apollo.query()       — one-time query (returns Observable)
 *   this.apollo.mutate()      — execute mutation
 *
 * Run: npm install && npm start
 */
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div style="max-width:900px; margin:0 auto; padding:20px; font-family:system-ui,sans-serif">

      <!-- Header -->
      <div style="background:#7b1fa2; color:white; padding:16px; border-radius:8px; margin-bottom:20px">
        <h1 style="margin:0; font-size:22px">Day 32 — GraphQL Client: Angular + Apollo Angular</h1>
        <p style="margin:4px 0 0; opacity:.9; font-size:14px">
          Requires Day 31 server: cd .../Day 31/part-1 &amp;&amp; mvn spring-boot:run
        </p>
      </div>

      <!-- Student List (watchQuery) -->
      <div style="background:white; border:1px solid #ddd; border-radius:8px; padding:20px; margin-bottom:16px">
        <h2 style="color:#7b1fa2; margin-top:0">
          apollo.watchQuery() — Auto-refreshing List
        </h2>
        <button (click)="refetchStudents()" style="background:#7b1fa2; color:white; border:none; padding:8px 16px; border-radius:4px; cursor:pointer; margin-bottom:12px">
          ↻ Refetch
        </button>
        <div *ngIf="studentsLoading" style="color:#888; font-style:italic">Loading students...</div>
        <div *ngIf="studentsError" style="color:#d32f2f">Error: {{ studentsError }}</div>
        <div style="display:grid; grid-template-columns:repeat(auto-fill,minmax(180px,1fr)); gap:12px">
          <div *ngFor="let s of students"
               style="border:1px solid #e0e0e0; border-radius:8px; padding:12px; background:#fafafa">
            <strong>{{ s.name }}</strong>
            <div style="margin:4px 0">
              <span style="background:#f3e5f5; color:#6a1b9a; padding:2px 8px; border-radius:12px; font-size:12px">
                {{ s.major }}
              </span>
            </div>
            <div>GPA: <strong [style.color]="s.gpa >= 3.5 ? '#2e7d32' : '#333'">{{ s.gpa }}</strong></div>
            <button (click)="deleteStudent(s.id)"
                    style="background:#d32f2f; color:white; border:none; padding:4px 8px; border-radius:4px; cursor:pointer; font-size:12px; margin-top:8px">
              Delete
            </button>
          </div>
        </div>
      </div>

      <!-- Student Detail (query with variable) -->
      <div style="background:white; border:1px solid #ddd; border-radius:8px; padding:20px; margin-bottom:16px">
        <h2 style="color:#7b1fa2; margin-top:0">apollo.query() — Detail with Variable</h2>
        <input [(ngModel)]="selectedId" placeholder="Enter Student ID (1-5)"
               style="padding:6px 10px; border:1px solid #ccc; border-radius:4px; margin-right:8px" />
        <button (click)="loadStudentDetail()"
                style="background:#7b1fa2; color:white; border:none; padding:8px 16px; border-radius:4px; cursor:pointer">
          Load
        </button>
        <div *ngIf="detailLoading" style="color:#888; font-style:italic; margin-top:8px">Loading...</div>
        <div *ngIf="selectedStudent" style="margin-top:12px; border:1px solid #e0e0e0; border-radius:8px; padding:12px">
          <h3 style="margin-top:0">{{ selectedStudent.name }}</h3>
          <div>Email: {{ selectedStudent.email }}</div>
          <div>Major: {{ selectedStudent.major }} | GPA: {{ selectedStudent.gpa }}</div>
          <div style="margin-top:8px"><strong>Courses:</strong></div>
          <div *ngFor="let c of selectedStudent.courses"
               style="border:1px solid #eee; border-radius:4px; padding:8px; margin:4px 0">
            {{ c.title }} — {{ c.instructor }}
          </div>
        </div>
      </div>

      <!-- Create Student (mutation) -->
      <div style="background:white; border:1px solid #ddd; border-radius:8px; padding:20px; margin-bottom:16px">
        <h2 style="color:#7b1fa2; margin-top:0">apollo.mutate() — Create Student</h2>
        <div style="display:flex; flex-direction:column; gap:10px; max-width:400px">
          <input [(ngModel)]="newStudent.name" placeholder="Name *"
                 style="padding:6px 10px; border:1px solid #ccc; border-radius:4px" />
          <input [(ngModel)]="newStudent.email" placeholder="Email *"
                 style="padding:6px 10px; border:1px solid #ccc; border-radius:4px" />
          <select [(ngModel)]="newStudent.major"
                  style="padding:6px 10px; border:1px solid #ccc; border-radius:4px">
            <option value="CS">Computer Science</option>
            <option value="Math">Mathematics</option>
          </select>
          <input [(ngModel)]="newStudent.gpa" placeholder="GPA (0-4)" type="number" step="0.1" min="0" max="4"
                 style="padding:6px 10px; border:1px solid #ccc; border-radius:4px" />
          <button (click)="createStudent()" [disabled]="creating"
                  style="background:#7b1fa2; color:white; border:none; padding:8px 16px; border-radius:4px; cursor:pointer">
            {{ creating ? 'Creating...' : 'Create Student' }}
          </button>
          <div *ngIf="createSuccess" style="color:#2e7d32">✓ {{ createSuccess }}</div>
          <div *ngIf="createError" style="color:#d32f2f">Error: {{ createError }}</div>
        </div>
      </div>

      <!-- Reference -->
      <div style="background:white; border:1px solid #ddd; border-radius:8px; padding:20px">
        <h2 style="color:#7b1fa2; margin-top:0">Apollo Angular Reference</h2>
        <button (click)="showRef = !showRef"
                style="background:#7b1fa2; color:white; border:none; padding:8px 16px; border-radius:4px; cursor:pointer; margin-bottom:12px">
          {{ showRef ? 'Hide' : 'Show' }} Reference
        </button>
        <div *ngIf="showRef">
          <table style="width:100%; border-collapse:collapse">
            <thead>
              <tr style="background:#f3e5f5">
                <th style="padding:8px; text-align:left; border:1px solid #ddd">Method</th>
                <th style="padding:8px; text-align:left; border:1px solid #ddd">Returns</th>
                <th style="padding:8px; text-align:left; border:1px solid #ddd">Use For</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let row of apolloMethods">
                <td style="padding:8px; border:1px solid #ddd; font-family:monospace; font-size:13px">{{ row[0] }}</td>
                <td style="padding:8px; border:1px solid #ddd; font-family:monospace; font-size:13px">{{ row[1] }}</td>
                <td style="padding:8px; border:1px solid #ddd">{{ row[2] }}</td>
              </tr>
            </tbody>
          </table>
          <p><strong>vs React Apollo:</strong> Apollo Angular uses RxJS Observables. Subscribe with .subscribe() or | async pipe. Apollo React uses hooks (useQuery, useMutation).</p>
        </div>
      </div>

    </div>
  `
})
export class AppComponent implements OnInit, OnDestroy {

  // Students list
  students: Student[] = [];
  studentsLoading = false;
  studentsError = '';
  private studentsQuery: any;
  private studentsSub?: Subscription;

  // Student detail
  selectedId = '';
  selectedStudent: Student | null = null;
  detailLoading = false;

  // Create form
  newStudent = { name: '', email: '', major: 'CS', gpa: 3.5 };
  creating = false;
  createSuccess = '';
  createError = '';

  showRef = false;

  apolloMethods = [
    ['apollo.watchQuery()', 'QueryRef', 'Queries + subscribes to cache updates. Use .valueChanges Observable.'],
    ['apollo.query()', 'Observable<ApolloQueryResult>', 'One-time query. Does not subscribe to updates.'],
    ['apollo.mutate()', 'Observable<MutationResult>', 'Execute a mutation.'],
    ['queryRef.refetch()', 'Promise', 'Re-execute the query with same or new variables.'],
    ['queryRef.fetchMore()', 'Observable', 'Fetch next page and merge with existing cache.'],
  ];

  constructor(private apollo: Apollo) {}

  ngOnInit() {
    this.loadStudents();
  }

  ngOnDestroy() {
    this.studentsSub?.unsubscribe();
  }

  loadStudents() {
    this.studentsLoading = true;
    /**
     * watchQuery creates a QueryRef that:
     *   1. Executes the query
     *   2. Subscribes to InMemoryCache — updates when cache changes (e.g., after mutations)
     */
    this.studentsQuery = this.apollo.watchQuery<{ students: Student[] }>({
      query: GET_STUDENTS
    });

    this.studentsSub = this.studentsQuery.valueChanges.subscribe({
      next: ({ data, loading }: any) => {
        this.studentsLoading = loading;
        this.students = data?.students ?? [];
      },
      error: (err: any) => {
        this.studentsLoading = false;
        this.studentsError = err.message;
      }
    });
  }

  refetchStudents() {
    this.studentsQuery?.refetch();
  }

  loadStudentDetail() {
    if (!this.selectedId) return;
    this.detailLoading = true;
    this.selectedStudent = null;

    /**
     * apollo.query() for one-time data fetch.
     * Returns Observable<ApolloQueryResult<T>>.
     */
    this.apollo.query<{ student: Student }>({
      query: GET_STUDENT_DETAIL,
      variables: { id: this.selectedId },
      fetchPolicy: 'network-only' // bypass cache for fresh data
    }).subscribe({
      next: ({ data }) => {
        this.detailLoading = false;
        this.selectedStudent = data.student;
      },
      error: (err) => {
        this.detailLoading = false;
      }
    });
  }

  createStudent() {
    if (!this.newStudent.name || !this.newStudent.email) return;
    this.creating = true;
    this.createSuccess = '';
    this.createError = '';

    this.apollo.mutate<{ createStudent: Student }>({
      mutation: CREATE_STUDENT,
      variables: { input: this.newStudent },
      refetchQueries: [{ query: GET_STUDENTS }] // refresh list after mutation
    }).subscribe({
      next: ({ data }) => {
        this.creating = false;
        this.createSuccess = `Created: ${data?.createStudent.name}`;
        this.newStudent = { name: '', email: '', major: 'CS', gpa: 3.5 };
      },
      error: (err) => {
        this.creating = false;
        this.createError = err.message;
      }
    });
  }

  deleteStudent(id: string) {
    this.apollo.mutate({
      mutation: DELETE_STUDENT,
      variables: { id },
      refetchQueries: [{ query: GET_STUDENTS }]
    }).subscribe();
  }
}
