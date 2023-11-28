import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { Note } from '@registry-web/shared/model';
import { errors } from '@registry-web/shared/shared.action';
import * as NotesActions from 'src/app/notes/store/notes.actions';

@Component({
  selector: 'app-account-notes',
  templateUrl: './account-notes.component.html',
})
export class AccountNotesComponent {
  @Input()
  accountNotes: Note[];
  @Input()
  accountHolderNotes: Note[];
  @Input()
  isAdmin: boolean;
  @Input()
  isSeniorAdmin: boolean;
  @Input()
  accountFullId: string;

  constructor(private store: Store) {}

  addNote() {
    this.clearState();
    this.store.dispatch(NotesActions.navigateSelectEntity());
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
