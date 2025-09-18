import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { Note } from '@registry-web/shared/model';
import { errors } from '@registry-web/shared/shared.action';
import { SharedModule } from '@registry-web/shared/shared.module';
import * as NotesActions from '@registry-web/task-management/task-details/components/task-notes/store/task-notes.actions';

@Component({
  standalone: true,
  imports: [SharedModule],
  selector: 'app-task-notes',
  templateUrl: './task-notes.component.html',
})
export class TaskNotesComponent {
  @Input()
  notes: Note[];
  @Input()
  isAdmin: boolean;
  @Input()
  isSeniorAdmin: boolean;
  @Input()
  requestId: string;

  constructor(private store: Store) {}

  addNote() {
    this.clearState();
    this.store.dispatch(NotesActions.navigateAddNote());
  }

  private clearState() {
    this.store.dispatch(NotesActions.clearNoteState());
  }

  deleteNote(noteId: string) {
    this.store.dispatch(NotesActions.navigateDeleteNote({ noteId }));
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
