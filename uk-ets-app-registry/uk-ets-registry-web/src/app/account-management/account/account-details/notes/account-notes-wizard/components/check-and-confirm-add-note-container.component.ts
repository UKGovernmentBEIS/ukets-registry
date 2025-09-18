import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  createNote,
  navigateCancelAddNote,
  navigateSelectEntity,
} from '@registry-web/account-management/account/account-details/notes/store/account-notes.actions';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@registry-web/shared/shared.action';
import {
  selectAddNotesDescription,
  selectAddNotesTypeLabel,
} from '@registry-web/account-management/account/account-details/notes/store/account-notes.selector';
import { CheckAndConfirmAddNoteComponent } from '@notes/components/check-and-confirm-add-note/check-and-confirm-add-note.component';
import { CommonModule } from '@angular/common';
import { NotesWizardPathsModel } from '../../../../../../notes/model/notes-wizard-paths.model';

@Component({
  standalone: true,
  imports: [CheckAndConfirmAddNoteComponent, CommonModule],
  selector: 'app-check-and-confirm-add-note-container',
  template: `<app-check-and-confirm-add-note
    [storedTypeLabel]="storedTypeLabel$ | async"
    [storedNote]="storedNote$ | async"
    (handleCancel)="onCancel()"
    (handleSubmit)="onSubmit()"
    (handleChange)="onChange()"
  ></app-check-and-confirm-add-note>`,
})
export class CheckAndConfirmAddNoteContainerComponent {
  storedTypeLabel$: Observable<any>;
  storedNote$: Observable<any>;

  constructor(private store: Store, private route: ActivatedRoute) {
    this.storedTypeLabel$ = this.store.select(selectAddNotesTypeLabel);
    this.storedNote$ = this.store.select(selectAddNotesDescription);
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
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
    this.store.dispatch(navigateSelectEntity());
  }

  onCancel() {
    this.store.dispatch(
      navigateCancelAddNote({
        currentRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${
          NotesWizardPathsModel.CHECK_AND_CONFIRM_ADD_NOTE
        }`,
      })
    );
  }
}
