# Angular Exercises — Full Curriculum

This folder contains **12 progressive Angular exercises** designed to take you from zero to production-ready Angular developer. Each exercise is a **self-contained Angular application** with starter code and a complete solution.

---

## 🗂️ Exercise Index

| # | Exercise | Key Concepts |
|---|----------|-------------|
| 01 | [Components & Data Binding](#) | Components, Interpolation, Property/Event/Two-Way Binding |
| 02 | [Built-in Directives](#) | `@if`, `@for`, `@switch`, `ngClass`, `ngStyle` |
| 03 | [Component Communication](#) | `@Input`, `@Output`, `EventEmitter`, `ViewChild` |
| 04 | [Services & Dependency Injection](#) | Services, `inject()`, Singleton pattern |
| 05 | [Angular Router](#) | Routes, `RouterLink`, Route Params, Guards |
| 06 | [Template-Driven Forms](#) | `ngModel`, `ngForm`, Built-in validators |
| 07 | [Reactive Forms](#) | `FormBuilder`, `FormGroup`, Custom validators, `FormArray` |
| 08 | [HTTP Client](#) | `HttpClient`, GET/POST, Error handling, Interceptors |
| 09 | [Pipes](#) | Built-in pipes, Custom pipes, `AsyncPipe` |
| 10 | [Angular Signals](#) | `signal()`, `computed()`, `effect()`, `toSignal()` |
| 11 | [RxJS & Observables](#) | Observables, Operators, `Subject`, `BehaviorSubject` |
| 12 | [Full Application](#) | Capstone: Task Manager combining all concepts |

---

## 🚀 Getting Started

### Prerequisites
```bash
# Install Node.js (v18+) from https://nodejs.org
node --version

# Install the Angular CLI globally
npm install -g @angular/cli
ng version
```

### How to Work on Each Exercise

1. **Create a new Angular project** for each exercise:
   ```bash
   ng new exercise-01-data-binding --standalone --routing=false --style=css
   cd exercise-01-data-binding
   ```

2. **Copy the starter-code files** into your project's `src/app/` folder, replacing the generated files.

3. **Read the `README.md`** inside the exercise folder — it lists every TODO you need to complete.

4. **Run the dev server** and implement the TODOs:
   ```bash
   ng serve
   # Open http://localhost:4200
   ```

5. **Check your work** against the `solution/` folder when you're done.

---

## 📚 Angular Version

These exercises use **Angular 17+** and leverage modern features:
- ✅ Standalone components (no `NgModule`)
- ✅ New control flow syntax (`@if`, `@for`, `@switch`)
- ✅ `inject()` function for dependency injection
- ✅ Signals (`signal()`, `computed()`, `effect()`)
- ✅ Typed forms

---

## 🎯 Learning Path

```
Exercise 01 → 02 → 03   (Core Angular: Components & Templates)
Exercise 04 → 05         (Architecture: Services & Routing)
Exercise 06 → 07         (User Input: Forms)
Exercise 08 → 09         (Data: HTTP & Pipes)
Exercise 10 → 11         (Reactivity: Signals & RxJS)
Exercise 12              (Capstone: Full Application)
```
