import { Component } from '@angular/core';
import { NoteEditorComponent } from './note-editor/note-editor.component';
import { NoteListComponent } from './note-list/note-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [NoteEditorComponent, NoteListComponent],
  template: `
    <div class="page">
      <h1>📓 Notes App</h1>
      <div class="layout">
        <aside><app-note-editor /></aside>
        <main><app-note-list /></main>
      </div>
    </div>
  `,
  styles: [`
    * { box-sizing: border-box; }
    body { font-family: 'Segoe UI', sans-serif; background: #f7fafc; }
    .page { max-width: 1100px; margin: 2rem auto; padding: 0 1rem; }
    h1 { text-align: center; font-size: 2rem; margin-bottom: 2rem; }
    .layout { display: grid; grid-template-columns: 320px 1fr; gap: 2rem; align-items: start; }
  `]
})
export class AppComponent {}
