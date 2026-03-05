import { Component, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@registry-web/shared/shared.action';
import { CheckAndConfirmAddNoteComponent } from '@notes/components/check-and-confirm-add-note/check-and-confirm-add-note.component';
import { CommonModule } from '@angular/common';
import { NotesWizardPathsModel } from '@registry-web/notes/model/notes-wizard-paths.model';
import { selectAddNotesDescription } from '../../store/task-notes.selector';
import { TaskNotesActions } from '../../store/task-notes.actions';
import { TASK_NOTES_PARENT_DETAILS_PATH_TOKEN } from '../../task-notes.provider';

@Component({
  selector: 'app-check-and-confirm-add-note-container',
  template: `<app-check-and-confirm-add-note
    [storedNote]="storedNote$ | async"
    (handleCancel)="onCancel()"
    (handleSubmit)="onSubmit()"
    (handleChange)="onChange()"
  />`,
  standalone: true,
  imports: [CheckAndConfirmAddNoteComponent, CommonModule],
})
export class CheckAndConfirmAddNoteContainerComponent {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);
  private readonly DETAILS_PATH = inject(TASK_NOTES_PARENT_DETAILS_PATH_TOKEN);
  readonly storedNote$ = this.store.select(selectAddNotesDescription);

  constructor() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/${this.DETAILS_PATH}/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.ADD_NOTE}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onSubmit() {
    this.store.dispatch(TaskNotesActions.CREATE_NOTE());
  }

  onChange() {
    this.store.dispatch(TaskNotesActions.NAVIGATE_TO_ADD_NOTE());
  }

  onCancel() {
    this.store.dispatch(
      TaskNotesActions.NAVIGATE_TO_CANCEL_ADD_NOTE({
        currentRoute: `/${this.DETAILS_PATH}/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.CHECK_AND_CONFIRM_ADD_NOTE}`,
      })
    );
  }
}
