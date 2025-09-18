import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@registry-web/shared/shared.action';
import { CheckAndConfirmAddNoteComponent } from '@notes/components/check-and-confirm-add-note/check-and-confirm-add-note.component';
import { CommonModule } from '@angular/common';
import { NotesWizardPathsModel } from '@registry-web/notes/model/notes-wizard-paths.model';
import {
  selectAddNotesDescription,
  selectAddNotesTypeLabel,
} from '../../store/task-notes.selector';
import {
  createNote,
  navigateAddNote,
  navigateCancelAddNote,
} from '../../store/task-notes.actions';

@Component({
  standalone: true,
  imports: [CheckAndConfirmAddNoteComponent, CommonModule],
  selector: 'app-check-and-confirm-add-note-container',
  template: `<app-check-and-confirm-add-note
    [storedNote]="storedNote$ | async"
    (handleCancel)="onCancel()"
    (handleSubmit)="onSubmit()"
    (handleChange)="onChange()"
  ></app-check-and-confirm-add-note>`,
})
export class CheckAndConfirmAddNoteContainerComponent {
  storedNote$: Observable<any>;

  constructor(private store: Store, private route: ActivatedRoute) {
    this.storedNote$ = this.store.select(selectAddNotesDescription);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${
          NotesWizardPathsModel.ADD_NOTE
        }`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onSubmit() {
    this.store.dispatch(createNote());
  }

  onChange() {
    this.store.dispatch(navigateAddNote());
  }

  onCancel() {
    this.store.dispatch(
      navigateCancelAddNote({
        currentRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${
          NotesWizardPathsModel.CHECK_AND_CONFIRM_ADD_NOTE
        }`,
      })
    );
  }
}
