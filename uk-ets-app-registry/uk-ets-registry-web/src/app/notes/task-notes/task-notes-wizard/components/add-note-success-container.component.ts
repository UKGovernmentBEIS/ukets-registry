import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';
import { AddNoteSuccessComponent } from '@notes/components/add-note-success/add-note-success.component';
import { TaskNotesActions } from '../../store/task-notes.actions';

@Component({
  selector: 'app-add-note-success-container',
  template: `<app-add-note-success (handleBackToNotes)="onBackToNotes()" />`,
  standalone: true,
  imports: [AddNoteSuccessComponent],
})
export class AddNoteSuccessContainerComponent {
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
