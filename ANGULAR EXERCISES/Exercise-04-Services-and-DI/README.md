# Exercise 04 — Services & Dependency Injection

## 🎯 Learning Objectives
- Create an **Angular service** with `@Injectable`
- Use `providedIn: 'root'` for a singleton service
- Use the **`inject()` function** (modern pattern) to inject services
- Share **state** and **logic** across multiple components via a service
- Understand the **separation of concerns** between components and services

---

## 📋 What You're Building
A **Notes App** — a two-component app with a single shared `NotesService`:
- `NoteListComponent` — displays all notes, lets you delete them
- `NoteEditorComponent` — form to create / edit a note
- Both components share the same `NotesService` — if you add a note in the editor, it instantly appears in the list

---

## 🏗️ Project Setup
```bash
ng new exercise-04-services --standalone --routing=false --style=css
cd exercise-04-services
# Copy starter-code/src/app files into your src/app/
ng serve
```

---

## 📁 File Structure
```
src/app/
├── app.component.ts / .html / .css
├── services/
│   └── notes.service.ts
├── note-list/
│   ├── note-list.component.ts
│   └── note-list.component.html
└── note-editor/
    ├── note-editor.component.ts
    └── note-editor.component.html
```

---

## ✅ TODOs

### `services/notes.service.ts`
- [ ] **TODO 1**: Add `@Injectable({ providedIn: 'root' })` decorator
- [ ] **TODO 2**: Define `Note` interface: `id`, `title`, `content`, `createdAt: Date`, `color: string`
- [ ] **TODO 3**: Create a private `notes: Note[]` array with 3 sample notes
- [ ] **TODO 4**: Implement `getNotes()` — returns the notes array
- [ ] **TODO 5**: Implement `addNote(title, content, color)` — creates and pushes a new Note
- [ ] **TODO 6**: Implement `deleteNote(id)` — removes the note with that id
- [ ] **TODO 7**: Implement `updateNote(id, title, content)` — finds and updates the note

### `note-list.component.ts`
- [ ] **TODO 8**: Use `inject(NotesService)` to get the service (no constructor needed!)
- [ ] **TODO 9**: Create a `get notes()` getter that calls `notesService.getNotes()`
- [ ] **TODO 10**: Implement `deleteNote(id)` that delegates to the service

### `note-editor.component.ts`
- [ ] **TODO 11**: Inject `NotesService`
- [ ] **TODO 12**: Declare form fields: `title`, `content`, `color` (default `'#fff9c4'`)
- [ ] **TODO 13**: Implement `saveNote()` — calls `notesService.addNote(...)` then resets the form

### `app.component.html`
- [ ] **TODO 14**: Render both `<app-note-editor>` and `<app-note-list>` side by side

---

## 💡 Key Concepts Reminder

```typescript
// Creating a service
@Injectable({ providedIn: 'root' })
export class NotesService {
  private notes: Note[] = [];
  getNotes() { return this.notes; }
}

// Modern injection with inject() function (Angular 14+)
export class MyComponent {
  private notesService = inject(NotesService);
}

// Traditional constructor injection (also valid)
export class MyComponent {
  constructor(private notesService: NotesService) {}
}
```

> 💡 A service decorated with `providedIn: 'root'` is a **singleton** — every component that injects it gets the **same instance**, so state is shared automatically!
