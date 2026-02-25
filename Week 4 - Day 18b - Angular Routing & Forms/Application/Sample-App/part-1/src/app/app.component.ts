// Day 18b Part 1 — Angular Router: Routes, RouterLink, useParams, Nested Routes
// Run: npm install && npm start
// NOTE: Routes are configured in app.config.ts

import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, RouterLink, RouterLinkActive, RouterOutlet,
         ActivatedRoute, Router } from '@angular/router';

const SHARED_STYLES = `
  * { box-sizing: border-box; }
  .page { max-width: 900px; margin: 0 auto; padding: 2rem 1rem; font-family: -apple-system, sans-serif; }
  .card { background: white; border-radius: 8px; padding: 1.5rem; margin-bottom: 1.5rem; box-shadow: 0 2px 8px rgba(0,0,0,.08); }
  h2 { color: #dd0031; margin-bottom: 1rem; }
  .btn { background: #dd0031; color: white; border: none; padding: .4rem .9rem; border-radius: 4px; cursor: pointer; margin: .2rem; text-decoration: none; display: inline-block; }
`;

const COURSES = [
  { id: 1, name: 'Java Fundamentals', icon: '☕', weeks: 'Week 1-2', desc: 'Core Java, OOP, Collections, Streams.' },
  { id: 2, name: 'Frontend',          icon: '🌐', weeks: 'Week 3-4', desc: 'HTML, CSS, JavaScript, React, Angular.' },
  { id: 3, name: 'Spring Boot',       icon: '🍃', weeks: 'Week 5-6', desc: 'REST APIs, Spring Data JPA, Security.' },
  { id: 4, name: 'DevOps & Cloud',    icon: '☁️', weeks: 'Week 7-8', desc: 'Docker, Kubernetes, CI/CD, AWS.' },
];

// ── Page Components ───────────────────────────────────────────────────────────

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  styles: [SHARED_STYLES],
  template: `
    <div class="page">
      <div style="background:#dd0031;color:white;padding:1.5rem 2rem;border-radius:8px;margin-bottom:2rem">
        <h1>🏠 Academy Course Portal</h1>
        <p style="opacity:.85">Angular Router Demo — client-side navigation without page reloads</p>
      </div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:1rem">
        <div *ngFor="let c of courses" class="card" style="cursor:pointer"
             [routerLink]="['/courses', c.id]">
          <div style="font-size:1.5rem">{{ c.icon }} <strong>{{ c.name }}</strong></div>
          <small style="color:#888">{{ c.weeks }}</small>
          <p style="color:#555;font-size:.9rem;margin-top:.4rem">{{ c.desc }}</p>
        </div>
      </div>
    </div>`
})
export class HomeComponent {
  courses = COURSES;
}

@Component({
  selector: 'app-course-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  styles: [SHARED_STYLES],
  template: `
    <div class="page">
      <h1 style="color:#dd0031">📚 All Courses</h1>
      <ul style="list-style:none;padding:0;margin-top:1rem">
        <li *ngFor="let c of courses" class="card" style="display:flex;align-items:center;gap:1rem">
          <span style="font-size:2rem">{{ c.icon }}</span>
          <div style="flex:1"><strong>{{ c.name }}</strong> <small style="color:#888">({{ c.weeks }})</small></div>
          <a [routerLink]="['/courses', c.id]" class="btn">View →</a>
        </li>
      </ul>
    </div>`
})
export class CourseListComponent {
  courses = COURSES;
}

@Component({
  selector: 'app-course-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterOutlet],
  styles: [SHARED_STYLES],
  template: `
    <div class="page">
      <button (click)="router.navigate(['/courses'])" class="btn">← Back</button>
      <div class="card" style="margin-top:1rem" *ngIf="course; else notFound">
        <h2>{{ course.icon }} {{ course.name }}</h2>
        <p style="color:#888">{{ course.weeks }}</p>
        <p style="margin:.8rem 0">{{ course.desc }}</p>
        <p style="color:#555;font-size:.85rem">
          Route params: <code>id = {{ courseId }}</code> (from <code>ActivatedRoute.params</code>)
        </p>
        <div style="margin-top:1rem">
          <a [routerLink]="['syllabus']" class="btn">📋 Syllabus</a>
          <a [routerLink]="['students']" class="btn" style="background:#388e3c">👥 Students</a>
        </div>
        <router-outlet></router-outlet>
      </div>
      <ng-template #notFound>
        <div class="card"><p style="color:#e74c3c">Course {{ courseId }} not found.</p></div>
      </ng-template>
    </div>`
})
export class CourseDetailComponent implements OnInit {
  courseId = 0;
  course: typeof COURSES[0] | undefined;
  router = inject(Router);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    this.route.params.subscribe(p => {
      this.courseId = +p['id'];
      this.course = COURSES.find(c => c.id === this.courseId);
    });
  }
}

@Component({ selector: 'app-syllabus', standalone: true,
  template: `<div style="margin-top:1rem;background:#e3f2fd;padding:1rem;border-radius:6px">
    <strong>📋 Syllabus</strong><p style="color:#555;margin:.3rem 0">Fundamentals → Advanced → Projects → Assessment</p></div>` })
export class SyllabusComponent {}

@Component({ selector: 'app-students-tab', standalone: true, imports: [CommonModule],
  template: `<div style="margin-top:1rem;background:#e8f5e9;padding:1rem;border-radius:6px">
    <strong>👥 Students</strong>
    <div style="margin-top:.5rem;display:flex;gap:.5rem;flex-wrap:wrap">
      <span *ngFor="let n of names" style="background:#dd0031;color:white;padding:2px 10px;border-radius:12px;font-size:.85rem">{{ n }}</span>
    </div></div>` })
export class StudentsTabComponent {
  names = ['Alice', 'Bob', 'Carol', 'David', 'Eve'];
}

@Component({ selector: 'app-not-found', standalone: true, imports: [RouterLink],
  template: `<div style="text-align:center;padding:4rem;font-family:sans-serif">
    <div style="font-size:4rem">🔍</div>
    <h2 style="color:#dd0031">404 — Page Not Found</h2>
    <a routerLink="/" style="background:#dd0031;color:white;padding:.5rem 1rem;border-radius:4px;text-decoration:none">Home</a>
  </div>` })
export class NotFoundComponent {}

// ── Root (shell) component ─────────────────────────────────────────────────────
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  styles: [`
    nav { background: #dd0031; padding: .8rem 2rem; display: flex; gap: 1.5rem; align-items: center; }
    a.nav-link { color: white; text-decoration: none; padding: .3rem .6rem; border-radius: 4px; }
    a.nav-link.active { background: rgba(255,255,255,.2); font-weight: bold; }
  `],
  template: `
    <nav>
      <span style="color:white;font-weight:bold;margin-right:1rem">🅰️ Academy</span>
      <a routerLink="/"        class="nav-link" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Home</a>
      <a routerLink="/courses" class="nav-link" routerLinkActive="active">Courses</a>
      <a routerLink="/about"   class="nav-link" routerLinkActive="active">About</a>
    </nav>
    <router-outlet></router-outlet>
  `
})
export class AppComponent {}
