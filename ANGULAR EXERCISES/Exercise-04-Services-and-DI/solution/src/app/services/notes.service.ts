import { Injectable } from '@angular/core';

export interface Note {
  id: number;
  title: string;
  content: string;
  createdAt: Date;
  color: string;
}

@Injectable({ providedIn: 'root' })
export class NotesService {

  private notes: Note[] = [
    { id: 1, title: 'Angular Services', content: 'Services provide shared logic and state across components via dependency injection.', createdAt: new Date(), color: '#fff9c4' },
    { id: 2, title: 'RxJS Basics', content: 'Observables are streams of data over time. Use subscribe() to react to emitted values.', createdAt: new Date(), color: '#c8e6c9' },
    { id: 3, title: 'TypeScript Tips', content: 'Always type your interfaces! It prevents bugs and makes code self-documenting.', createdAt: new Date(), color: '#bbdefb' },
  ];

  getNotes(): Note[] {
    return this.notes;
  }

  addNote(title: string, content: string, color: string): void {
    this.notes.unshift({
      id: Date.now(),
      title,
      content,
      createdAt: new Date(),
      color,
    });
  }

  deleteNote(id: number): void {
    this.notes = this.notes.filter(n => n.id !== id);
  }

  updateNote(id: number, title: string, content: string): void {
    const note = this.notes.find(n => n.id === id);
    if (note) {
      note.title = title;
      note.content = content;
    }
  }
}
