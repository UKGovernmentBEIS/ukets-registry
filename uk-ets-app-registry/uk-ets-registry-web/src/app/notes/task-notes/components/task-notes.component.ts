import { Component, inject, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { ErrorDetail, ErrorSummary } from '@registry-web/shared/error-summary';
import { Note } from '@registry-web/shared/model';
import { canGoBackToList, errors } from '@registry-web/shared/shared.action';
import { SharedModule } from '@registry-web/shared/shared.module';
import { TaskNotesActions } from '../store';
import { Router } from '@angular/router';
import { NOTES_LIST_PATH } from '../task-notes.const';
import { TASK_NOTES_PARENT_DETAILS_PATH_TOKEN } from '../task-notes.provider';

@Component({
  selector: 'app-task-notes',
  templateUrl: './task-notes.component.html',
  standalone: true,
  imports: [SharedModule],
})
export class TaskNotesComponent {
  private readonly store = inject(Store);
  private readonly router = inject(Router);
  private readonly DETAILS_PATH = inject(TASK_NOTES_PARENT_DETAILS_PATH_TOKEN);

  @Input() notes: Note[];
  @Input() isAdmin: boolean;
  @Input() isSeniorAdmin: boolean;
  @Input() requestId: string;

  addNote() {
    this.clearState();
    this.store.dispatch(TaskNotesActions.NAVIGATE_TO_ADD_NOTE());
  }

  private clearState() {
    this.store.dispatch(TaskNotesActions.CLEAR_FORM_STATE());
  }

  deleteNote(noteId: string) {
    this.store.dispatch(TaskNotesActions.NAVIGATE_TO_DELETE_NOTE({ noteId }));
  }

  goToUserDetails(userId: string) {
    this.store.dispatch(
      canGoBackToList({
        goBackToListRoute: `/${this.DETAILS_PATH}/${this.requestId}/${NOTES_LIST_PATH}`,
        extras: { skipLocationChange: false },
      })
    );

    this.router.navigate(['/user-details', userId]);
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = { errors: details };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
