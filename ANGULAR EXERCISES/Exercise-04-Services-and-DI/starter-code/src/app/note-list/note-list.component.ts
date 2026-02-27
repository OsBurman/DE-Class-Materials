import { Component } from '@angular/core';
import { NotesService } from '../services/notes.service';

// TODO 8: Import the `inject` function from '@angular/core'

@Component({
  selector: 'app-note-list',
  standalone: true,
  imports: [],
  templateUrl: './note-list.component.html',
  styleUrls: ['./note-list.component.css'],
})
export class NoteListComponent {

  // TODO 8 (continued): Use inject(NotesService) to assign the service to a private property.
  //         Example:  private notesService = inject(NotesService);
  //         Do NOT use a constructor — this is the modern Angular pattern!

  // TODO 9: Create a `notes` getter that returns notesService.getNotes()
  get notes() {
    // your code here
    return [];
  }

  // TODO 10: Implement deleteNote(id: number) that calls notesService.deleteNote(id)
  deleteNote(id: number): void {
    // your code here
  }
}
