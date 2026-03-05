import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@registry-web/shared/shared.action';
import { AddNoteFormComponent } from '@notes/components/add-note-form/add-note-form.component';
import { CommonModule } from '@angular/common';
import { NotesWizardPathsModel } from '@registry-web/notes/model/notes-wizard-paths.model';
import { selectAddNotesDescription } from '../../store/task-notes.selector';
import { TaskNotesActions } from '../../store/task-notes.actions';
import { TASK_NOTES_PARENT_DETAILS_PATH_TOKEN } from '../../task-notes.provider';
import { NOTES_LIST_PATH } from '../../task-notes.const';

@Component({
  selector: 'app-add-note-form-container',
  template: `<app-add-note-form
    [storedNote]="storedNote$ | async"
    (handleSubmit)="onContinue($event)"
    (handleCancel)="onCancel($event)"
  />`,
  standalone: true,
  imports: [AddNoteFormComponent, CommonModule],
})
export class AddNoteFormContainerComponent {
  private readonly store = inject(Store);
  private readonly route = inject(ActivatedRoute);
  private readonly DETAILS_PATH = inject(TASK_NOTES_PARENT_DETAILS_PATH_TOKEN);
  readonly storedNote$ = this.store.select(selectAddNotesDescription);

  constructor() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/${this.DETAILS_PATH}/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/${NOTES_LIST_PATH}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  private saveFormState(noteDescription: string) {
    this.store.dispatch(
      TaskNotesActions.SAVE_NOTE_DESCRIPTION({
        noteDescription: noteDescription,
      })
    );
  }

  onContinue(noteDescription: string) {
    this.saveFormState(noteDescription);
    this.store.dispatch(TaskNotesActions.NAVIGATE_TO_CHECK_AND_CONFIRM());
  }

  onCancel(noteDescription: string) {
    this.saveFormState(noteDescription);

    this.store.dispatch(
      TaskNotesActions.NAVIGATE_TO_CANCEL_ADD_NOTE({
        currentRoute: `/${this.DETAILS_PATH}/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${NotesWizardPathsModel.ADD_NOTE}`,
      })
    );
  }
}
