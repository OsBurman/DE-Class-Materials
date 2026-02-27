import { Injectable } from '@angular/core';

// TODO 2: Define a Note interface with:
//   id: number
//   title: string
//   content: string
//   createdAt: Date
//   color: string  (a hex color for the note card background)
export interface Note {
  // your fields here
}

// TODO 1: Add the @Injectable decorator with providedIn: 'root'
export class NotesService {

  // TODO 3: Create a private notes array with 3 sample notes.
  //         Use colors like '#fff9c4' (yellow), '#c8e6c9' (green), '#bbdefb' (blue)
  private notes: Note[] = [
    // sample notes here
  ];

  // TODO 4: Implement getNotes() — returns the full notes array.
  getNotes(): Note[] {
    // your code here
    return [];
  }

  // TODO 5: Implement addNote(title: string, content: string, color: string).
  //   - Create a new Note with a unique id (use Date.now()), today's date, and the provided values
  //   - Push it to the notes array
  addNote(title: string, content: string, color: string): void {
    // your code here
  }

  // TODO 6: Implement deleteNote(id: number).
  //   - Reassign this.notes filtering out the note with the matching id
  deleteNote(id: number): void {
    // your code here
  }

  // TODO 7: Implement updateNote(id: number, title: string, content: string).
  //   - Find the note with the matching id and update its title and content
  updateNote(id: number, title: string, content: string): void {
    // your code here
  }
}
