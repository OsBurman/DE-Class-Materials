// Day 17b Part 2 — Custom Pipes, Services, Dependency Injection
// Run: npm install && npm start

import { Component, Injectable, Pipe, PipeTransform, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

// ─────────────────────────────────────────────────────────────────────────────
// Custom Pipes
// ─────────────────────────────────────────────────────────────────────────────

// 1. Pure pipe: truncates a string
@Pipe({ name: 'truncate', standalone: true, pure: true })
export class TruncatePipe implements PipeTransform {
  transform(value: string, limit = 30, suffix = '…'): string {
    return value.length <= limit ? value : value.slice(0, limit) + suffix;
  }
}

// 2. Pure pipe: converts grade number to letter
@Pipe({ name: 'gradeLabel', standalone: true, pure: true })
export class GradeLabelPipe implements PipeTransform {
  transform(grade: number): string {
    if (grade >= 90) return 'A';
    if (grade >= 80) return 'B';
    if (grade >= 70) return 'C';
    if (grade >= 60) return 'D';
    return 'F';
  }
}

// 3. Impure pipe: filters an array (impure because array reference may not change)
@Pipe({ name: 'filterBy', standalone: true, pure: false })
export class FilterByPipe implements PipeTransform {
  transform(items: any[], field: string, query: string): any[] {
    if (!query) return items;
    return items.filter(i => String(i[field]).toLowerCase().includes(query.toLowerCase()));
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Services
// ─────────────────────────────────────────────────────────────────────────────

// providedIn: 'root' → singleton — same instance shared across entire app
@Injectable({ providedIn: 'root' })
export class LoggerService {
  private logs: string[] = [];

  log(msg: string) {
    const entry = `[${new Date().toLocaleTimeString()}] ${msg}`;
    this.logs.push(entry);
    console.log(entry);
  }

  getLogs() { return this.logs; }
  clear()    { this.logs = []; }
}

@Injectable({ providedIn: 'root' })
export class StudentService {
  private students = [
    { id: 1, name: 'Alice',  email: 'alice@academy.com',  grade: 92, bio: 'Loves Java and Spring Boot development' },
    { id: 2, name: 'Bob',    email: 'bob@academy.com',    grade: 74, bio: 'Frontend specialist, expert in React and Angular' },
    { id: 3, name: 'Carol',  email: 'carol@academy.com',  grade: 88, bio: 'Full stack developer focused on microservices' },
    { id: 4, name: 'David',  email: 'david@academy.com',  grade: 61, bio: 'DevOps enthusiast learning Docker and Kubernetes' },
    { id: 5, name: 'Eve',    email: 'eve@academy.com',    grade: 95, bio: 'Cloud architect with AWS certification interest' },
  ];

  constructor(private logger: LoggerService) {}  // Service injecting another service

  getAll() {
    this.logger.log('StudentService.getAll() called');
    return [...this.students];
  }

  getById(id: number) {
    this.logger.log(`StudentService.getById(${id}) called`);
    return this.students.find(s => s.id === id) || null;
  }

  add(name: string) {
    const student = { id: Date.now(), name, email: `${name.toLowerCase()}@academy.com`, grade: 70, bio: 'New student' };
    this.students.push(student);
    this.logger.log(`StudentService.add("${name}") — new student created`);
    return student;
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Root Component — injects services via constructor
// ─────────────────────────────────────────────────────────────────────────────
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, TruncatePipe, GradeLabelPipe, FilterByPipe],
  styles: [`
    * { box-sizing: border-box; }
    .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; font-family: -apple-system, sans-serif; }
    .header { background: #dd0031; color: white; padding: 1.5rem 2rem; border-radius: 8px; margin-bottom: 2rem; }
    .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
    h2 { color: #dd0031; margin-bottom: 1rem; padding-bottom: .4rem; border-bottom: 2px solid #ff6d00; }
    .code { background: #1e1e1e; color: #d4e157; padding: 1rem; border-radius: 6px; font-size: .82rem; white-space: pre; overflow: auto; margin: .5rem 0; }
    .btn { background: #dd0031; color: white; border: none; padding: .4rem .9rem; border-radius: 4px; cursor: pointer; margin: .2rem; }
    input { padding: .35rem .6rem; border: 1px solid #ccc; border-radius: 4px; margin: .2rem; }
    table { width: 100%; border-collapse: collapse; font-size: .9rem; }
    th { background: #dd0031; color: white; padding: .5rem; text-align: left; }
    td { padding: .45rem; border-bottom: 1px solid #f0f0f0; }
  `],
  template: `
<div class="page">
  <div class="header">
    <h1>🅰️ Day 17b Part 2 — Custom Pipes, Services &amp; DI</h1>
  </div>

  <!-- Custom Pipes -->
  <div class="card">
    <h2>1. Custom Pipes</h2>
    <div class="code">@Pipe({ name: 'truncate', standalone: true, pure: true })
export class TruncatePipe implements PipeTransform {
  transform(value: string, limit = 30, suffix = '…'): string {
    return value.length <= limit ? value : value.slice(0, limit) + suffix;
  }
}</div>
    <table style="margin-top:.8rem">
      <tr><th>Pipe</th><th>Input</th><th>Output</th></tr>
      <tr *ngFor="let s of students | filterBy:'name':filterQuery">
        <td><code>truncate</code></td>
        <td style="color:#888">{{ s.bio }}</td>
        <td>{{ s.bio | truncate:35 }}</td>
      </tr>
    </table>
    <div style="margin-top:.8rem">
      <strong>gradeLabel pipe:</strong>
      <span *ngFor="let s of students | filterBy:'name':filterQuery"
            style="margin:.2rem;display:inline-block;background:#f0f0f0;padding:2px 8px;border-radius:4px;font-size:.85rem">
        {{ s.name }}: {{ s.grade }} → <strong>{{ s.grade | gradeLabel }}</strong>
      </span>
    </div>
    <div style="margin-top:.8rem">
      <strong>filterBy pipe (impure):</strong>
      <input [value]="filterQuery" (input)="filterQuery = $any($event.target).value" placeholder="Filter students by name…" />
    </div>
  </div>

  <!-- Services -->
  <div class="card">
    <h2>2. Services &amp; Dependency Injection</h2>
    <div class="code">@Injectable({ providedIn: 'root' })   // Singleton — one instance for whole app
export class StudentService {
  constructor(private logger: LoggerService) {}  // DI: inject another service

  getAll() {
    this.logger.log('StudentService.getAll() called');
    return [...this.students];
  }
}

// In a component:
constructor(private studentService: StudentService) {}  // Angular injects automatically</div>

    <div style="margin-top:1rem;display:flex;gap:.5rem;flex-wrap:wrap">
      <button class="btn" (click)="loadStudents()">Load Students</button>
      <div style="display:flex;gap:.3rem;align-items:center">
        <input #nameInput placeholder="New name…" style="width:140px" />
        <button class="btn" (click)="addStudent(nameInput.value); nameInput.value=''">Add</button>
      </div>
    </div>
    <div style="margin-top:.8rem">
      <div *ngFor="let s of displayedStudents"
           style="padding:.35rem .6rem;background:#f9f9f9;border-radius:4px;margin:.2rem;display:flex;justify-content:space-between">
        <span><strong>{{ s.name }}</strong> <span style="color:#888;font-size:.85rem">{{ s.email }}</span></span>
        <span>{{ s.grade | gradeLabel }} ({{ s.grade }})</span>
      </div>
    </div>
  </div>

  <!-- Logger Service -->
  <div class="card">
    <h2>3. LoggerService — Shared Singleton State</h2>
    <p style="color:#555;font-size:.85rem;margin-bottom:.8rem">
      Both StudentService and AppComponent share the <em>same</em> LoggerService instance (singleton).
      All calls to logger.log() appear here:
    </p>
    <div style="background:#1e1e1e;color:#98c379;padding:.8rem;border-radius:6px;font-family:monospace;font-size:.82rem;max-height:150px;overflow-y:auto">
      <div *ngFor="let entry of loggerService.getLogs()">{{ entry }}</div>
      <div *ngIf="loggerService.getLogs().length === 0" style="color:#888">No logs yet — click Load Students above</div>
    </div>
    <button class="btn" style="background:#888;margin-top:.5rem" (click)="loggerService.clear()">Clear Logs</button>
  </div>
</div>
  `
})
export class AppComponent implements OnInit {
  displayedStudents: ReturnType<StudentService['getAll']> = [];
  filterQuery = '';

  // Angular's DI injects these — no need to call new StudentService()
  constructor(
    public studentService: StudentService,
    private logger: LoggerService
  ) {}

  get students() { return this.studentService.getAll(); }

  loadStudents() {
    this.displayedStudents = this.studentService.getAll();
  }

  addStudent(name: string) {
    if (!name.trim()) return;
    this.studentService.add(name.trim());
    this.displayedStudents = this.studentService.getAll();
  }

  ngOnInit() {
    this.logger.log('AppComponent initialized');
    this.loadStudents();
  }
}
