import { Component, inject } from '@angular/core';
import { DatePipe } from '@angular/common';
import { NotesService } from '../services/notes.service';

@Component({
  selector: 'app-note-list',
  standalone: true,
  imports: [DatePipe],
  template: `
    <div class="note-list">
      <h2>📒 My Notes ({{ notes.length }})</h2>
      @if (notes.length === 0) {
        <p class="empty">No notes yet. Create one!</p>
      }
      <div class="grid">
        @for (note of notes; track note.id) {
          <div class="note-card" [style.background]="note.color">
            <button class="delete-btn" (click)="deleteNote(note.id)">✕</button>
            <h3>{{ note.title }}</h3>
            <p>{{ note.content }}</p>
            <small>{{ note.createdAt | date:'short' }}</small>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    h2 { margin-bottom: 1rem; font-size: 1.2rem; }
    .empty { color: #a0aec0; text-align: center; padding: 2rem; }
    .grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1rem; }
    .note-card { padding: 1rem; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); position: relative; min-height: 120px; }
    .note-card h3 { font-size: 1rem; margin-bottom: 0.5rem; }
    .note-card p { font-size: 0.85rem; color: #4a5568; line-height: 1.5; }
    .note-card small { display: block; margin-top: 0.75rem; font-size: 0.75rem; color: #718096; }
    .delete-btn { position: absolute; top: 0.5rem; right: 0.5rem; background: rgba(0,0,0,0.1); border: none; border-radius: 50%; width: 24px; height: 24px; cursor: pointer; font-size: 0.75rem; }
    .delete-btn:hover { background: #e53e3e; color: white; }
  `]
})
export class NoteListComponent {
  private notesService = inject(NotesService);

  get notes() {
    return this.notesService.getNotes();
  }

  deleteNote(id: number): void {
    this.notesService.deleteNote(id);
  }
}
