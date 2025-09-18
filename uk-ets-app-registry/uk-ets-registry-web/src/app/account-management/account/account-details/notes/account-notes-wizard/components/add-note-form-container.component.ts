import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectAddNotesDescription } from '@registry-web/account-management/account/account-details/notes/store/account-notes.selector';
import {
  navigateCancelAddNote,
  navigateCheckAndConfirm,
  saveNoteDescription,
} from '@registry-web/account-management/account/account-details/notes/store/account-notes.actions';
import { canGoBack } from '@registry-web/shared/shared.action';
import { AddNoteFormComponent } from '@notes/components/add-note-form/add-note-form.component';
import { CommonModule } from '@angular/common';
import { NotesWizardPathsModel } from '../../../../../../notes/model/notes-wizard-paths.model';

@Component({
  standalone: true,
  selector: 'app-add-note-form-container',
  imports: [AddNoteFormComponent, CommonModule],
  template: `<app-add-note-form
    [options]="options"
    [storedNote]="storedNote$ | async"
    (handleSubmit)="onContinue($event)"
    (handleCancel)="onCancel($event)"
  ></app-add-note-form>`,
})
export class AddNoteFormContainerComponent implements OnInit {
  options = [
    { label: 'Account', value: 'ACCOUNT', enabled: true },
    { label: 'Account Holder', value: 'ACCOUNT_HOLDER', enabled: true },
  ];

  storedNote$: Observable<string>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.storedNote$ = this.store.select(selectAddNotesDescription);

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${
          NotesWizardPathsModel.SELECT_ENTITY
        }`,
        extras: { skipLocationChange: true },
      })
    );
  }

  private saveFormState(noteDescription: string) {
    this.store.dispatch(
      saveNoteDescription({
        noteDescription: noteDescription,
      })
    );
  }

  onContinue(noteDescription: string) {
    this.saveFormState(noteDescription);
    this.store.dispatch(navigateCheckAndConfirm());
  }

  onCancel(noteDescription: string) {
    this.saveFormState(noteDescription);
    this.store.dispatch(
      navigateCancelAddNote({
        currentRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${
          NotesWizardPathsModel.ADD_NOTE
        }`,
      })
    );
  }
}
