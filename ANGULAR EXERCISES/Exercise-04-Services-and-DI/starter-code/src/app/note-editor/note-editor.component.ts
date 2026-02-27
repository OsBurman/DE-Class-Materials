import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NotesService } from '../services/notes.service';

// TODO 11: Import and inject NotesService using inject()

@Component({
  selector: 'app-note-editor',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './note-editor.component.html',
  styleUrls: ['./note-editor.component.css'],
})
export class NoteEditorComponent {

  // TODO 11: Inject the NotesService here

  // TODO 12: Declare form fields:
  //   title: string = ''
  //   content: string = ''
  //   color: string = '#fff9c4'

  // Available colors for the color picker
  readonly colorOptions = ['#fff9c4', '#c8e6c9', '#bbdefb', '#f8bbd0', '#e1bee7', '#ffe0b2'];

  // TODO 13: Implement saveNote():
  //   - Only proceed if title is not empty
  //   - Call notesService.addNote(this.title, this.content, this.color)
  //   - Reset title and content to '' (keep the color)
  saveNote(): void {
    // your code here
  }
}
