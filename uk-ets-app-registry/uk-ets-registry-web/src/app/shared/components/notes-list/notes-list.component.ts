import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Note } from '@registry-web/shared/model/note';

@Component({
  selector: 'app-notes-list',
  templateUrl: './notes-list.component.html',
  styleUrls: ['./notes-list.component.scss'],
})
export class NotesListComponent {
  @Input()
  accountNotes: Note[];
  @Input()
  isSeniorAdmin: boolean;
  @Output()
  deleteNote = new EventEmitter<string>();

  deleteNoteClick(noteId: string) {
    this.deleteNote.emit(noteId);
  }
}
