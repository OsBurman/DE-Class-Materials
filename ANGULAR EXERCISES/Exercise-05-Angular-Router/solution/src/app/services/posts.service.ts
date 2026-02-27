import { Injectable } from '@angular/core';

export interface Post {
  id: number;
  title: string;
  excerpt: string;
  content: string;
  author: string;
  date: string;
  tags: string[];
}

@Injectable({ providedIn: 'root' })
export class PostsService {
  private posts: Post[] = [
    { id: 1, title: 'Getting Started with Angular', excerpt: 'A beginner-friendly guide to Angular fundamentals.', content: 'Angular is a powerful framework built with TypeScript. It provides a full solution for building web applications, including a component system, dependency injection, and a powerful router. In this post we cover the basics: setting up your first project, understanding components, and binding data in templates.', author: 'Alice Nguyen', date: '2025-01-10', tags: ['angular', 'beginners', 'typescript'] },
    { id: 2, title: 'Mastering RxJS Observables', excerpt: 'Deep dive into reactive programming with RxJS operators.', content: 'RxJS is the backbone of Angular\'s async story. Observables represent streams of data over time. Key operators you need to know: map, filter, switchMap, mergeMap, combineLatest, and takeUntilDestroyed. Understanding marble diagrams will transform how you think about async code.', author: 'Bob Martinez', date: '2025-02-01', tags: ['rxjs', 'observables', 'advanced'] },
    { id: 3, title: 'Angular Signals Explained', excerpt: 'The new reactivity primitive in Angular 16+.', content: 'Signals are Angular\'s answer to fine-grained reactivity. Unlike observables, signals are synchronous and don\'t require subscriptions. Use signal() to create reactive state, computed() for derived values, and effect() to run side effects when signals change.', author: 'Carol Smith', date: '2025-02-15', tags: ['signals', 'angular', 'reactivity'] },
    { id: 4, title: 'Reactive Forms Deep Dive', excerpt: 'Build complex, validated forms with Angular\'s reactive approach.', content: 'Reactive forms give you fine-grained control over form state. Use FormBuilder to create FormGroups and FormControls. Apply built-in validators like Validators.required and Validators.email, or write custom validator functions. FormArrays let you handle dynamic lists of form fields.', author: 'David Lee', date: '2025-03-01', tags: ['forms', 'angular', 'validation'] },
    { id: 5, title: 'Angular Router: Advanced Patterns', excerpt: 'Lazy loading, guards, and nested routes for large apps.', content: 'Once you go beyond basic routing, you need lazy loading to keep your bundle size small. Use loadComponent() and loadChildren() for code splitting. Route guards (CanActivate, CanDeactivate) protect routes from unauthorized access. Resolvers pre-fetch data before navigation.', author: 'Emma Wilson', date: '2025-03-15', tags: ['router', 'angular', 'performance'] },
    { id: 6, title: 'Testing Angular Applications', excerpt: 'Unit and integration testing with Jasmine, Karma, and Jest.', content: 'Testing is non-negotiable for production Angular apps. Use TestBed to set up component tests. Spy on services with spyOn(). For async operations, use fakeAsync and tick(). Testing HTTP calls is easy with HttpClientTestingModule. Always aim for meaningful tests over 100% coverage.', author: 'Frank Chen', date: '2025-04-01', tags: ['testing', 'angular', 'jasmine'] },
  ];

  getPosts(): Post[] { return this.posts; }

  getPostById(id: number): Post | undefined {
    return this.posts.find(p => p.id === id);
  }
}
