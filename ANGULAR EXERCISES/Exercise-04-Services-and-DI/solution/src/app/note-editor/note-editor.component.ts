import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgStyle } from '@angular/common';
import { NotesService } from '../services/notes.service';

@Component({
  selector: 'app-note-editor',
  standalone: true,
  imports: [FormsModule, NgStyle],
  template: `
    <div class="editor" [style.background]="color">
      <h2>✏️ New Note</h2>
      <input [(ngModel)]="title" placeholder="Note title" />
      <textarea [(ngModel)]="content" rows="5" placeholder="Write your note..."></textarea>
      <div class="color-row">
        <span>Color:</span>
        @for (c of colorOptions; track c) {
          <button
            class="color-dot"
            [style.background]="c"
            [class.selected]="color === c"
            (click)="color = c">
          </button>
        }
      </div>
      <button class="btn-save" (click)="saveNote()">💾 Save Note</button>
    </div>
  `,
  styles: [`
    .editor { padding: 1.5rem; border-radius: 12px; box-shadow: 0 2px 12px rgba(0,0,0,0.1); transition: background 0.3s; }
    h2 { margin-bottom: 1rem; font-size: 1.2rem; }
    input, textarea { width: 100%; display: block; padding: 0.5rem 0.75rem; border: 1px solid rgba(0,0,0,0.15); border-radius: 8px; font-size: 0.95rem; background: rgba(255,255,255,0.7); margin-bottom: 0.75rem; }
    .color-row { display: flex; align-items: center; gap: 0.5rem; margin-bottom: 1rem; }
    .color-dot { width: 28px; height: 28px; border-radius: 50%; border: 2px solid transparent; cursor: pointer; }
    .color-dot.selected { border-color: #2d3748; transform: scale(1.2); }
    .btn-save { width: 100%; background: #2d3748; color: white; border: none; border-radius: 8px; padding: 0.6rem; cursor: pointer; font-weight: 600; }
    .btn-save:hover { background: #1a202c; }
  `]
})
export class NoteEditorComponent {
  private notesService = inject(NotesService);

  title = '';
  content = '';
  color = '#fff9c4';

  readonly colorOptions = ['#fff9c4', '#c8e6c9', '#bbdefb', '#f8bbd0', '#e1bee7', '#ffe0b2'];

  saveNote(): void {
    if (!this.title.trim()) return;
    this.notesService.addNote(this.title.trim(), this.content.trim(), this.color);
    this.title = '';
    this.content = '';
  }
}
