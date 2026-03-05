import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';
import { DeleteNoteSuccessComponent } from '@notes/components/delete-note-success/delete-note-success.component';
import { TaskNotesActions } from '../../store/task-notes.actions';

@Component({
  standalone: true,
  imports: [DeleteNoteSuccessComponent],
  selector: 'app-delete-note-success-container',
  template: `<app-delete-note-success (handleBackToNotes)="onBackToNotes()" />`,
})
export class DeleteNoteSuccessContainerComponent {
  constructor(private store: Store) {
    this.store.dispatch(clearGoBackRoute());
    this.clearState();
  }

  private clearState() {
    this.store.dispatch(TaskNotesActions.CLEAR_FORM_STATE());
  }

  onBackToNotes() {
    this.store.dispatch(TaskNotesActions.NAVIGATE_TO_TASK_NOTES());
  }
}
