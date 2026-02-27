# Exercise 01 — Components & Data Binding

## 🎯 Learning Objectives
By the end of this exercise you will be able to:
- Create and use Angular **components**
- Use **string interpolation** `{{ value }}`
- Use **property binding** `[property]="value"`
- Use **event binding** `(event)="handler()"`
- Use **two-way data binding** `[(ngModel)]="value"`
- Understand the difference between **class properties** and **template expressions**

---

## 📋 What You're Building
A **Profile Card Builder** — a live-preview form where users fill out their profile details and see the card update in real time.

![Preview]
```
┌─────────────────────────────────────────────────┐
│  [FORM]                    [LIVE PREVIEW]        │
│  Name: [____________]     ┌──────────────────┐   │
│  Title: [___________]     │  🧑 Jane Smith   │   │
│  Bio: [_____________]     │  Senior Dev      │   │
│  Skills: [__] [Add]       │  Skills: JS, TS  │   │
│  Available: [✓]           │  ✅ Available    │   │
└─────────────────────────────────────────────────┘
```

---

## 🏗️ Project Setup
```bash
ng new exercise-01-profile-card --standalone --routing=false --style=css
cd exercise-01-profile-card
# Copy all files from starter-code/src/app into your project's src/app/
ng serve
```

---

## 📁 File Structure
```
src/app/
├── app.component.ts          ← Root component (host the form + card side by side)
├── app.component.html
├── app.component.css
└── profile-card/
    ├── profile-card.component.ts    ← Display-only card component
    ├── profile-card.component.html
    └── profile-card.component.css
```

---

## ✅ TODOs

### `app.component.ts`
- [ ] **TODO 1**: Declare a `profile` object with: `name`, `title`, `bio`, `avatarUrl`, `skills: string[]`, `isAvailableForWork: boolean`
- [ ] **TODO 2**: Create an `addSkill(skill: string)` method that pushes to the skills array
- [ ] **TODO 3**: Create a `removeSkill(index: number)` method that splices from the array
- [ ] **TODO 4**: Declare a `newSkill = ''` property for the skill input field
- [ ] **TODO 5**: Create a `toggleAvailability()` method

### `app.component.html`
- [ ] **TODO 6**: Use two-way binding `[(ngModel)]` on each input (name, title, bio, newSkill)
- [ ] **TODO 7**: Bind `(click)` on the "Add Skill" button to call `addSkill(newSkill)`
- [ ] **TODO 8**: Use `@for` to loop skills and bind `(click)` on each delete button
- [ ] **TODO 9**: Pass the `profile` object to `<app-profile-card>` using property binding
- [ ] **TODO 10**: Use `(click)` to call `toggleAvailability()`

### `profile-card.component.ts`
- [ ] **TODO 11**: Declare an `@Input()` named `profile` with a default/initial value

### `profile-card.component.html`
- [ ] **TODO 12**: Use `{{ profile.name }}` interpolation to display all profile fields
- [ ] **TODO 13**: Bind the `[src]` attribute of the `<img>` to `profile.avatarUrl`
- [ ] **TODO 14**: Use `[class.available]` to conditionally apply a CSS class

---

## 💡 Key Concepts Reminder

| Syntax | Purpose | Example |
|--------|---------|---------|
| `{{ expr }}` | Interpolation | `{{ user.name }}` |
| `[attr]="expr"` | Property binding | `[src]="imageUrl"` |
| `(event)="fn()"` | Event binding | `(click)="save()"` |
| `[(ngModel)]="x"` | Two-way binding | `[(ngModel)]="name"` |

> ⚠️ **Remember**: To use `[(ngModel)]`, you must import `FormsModule` in your component's `imports` array!
