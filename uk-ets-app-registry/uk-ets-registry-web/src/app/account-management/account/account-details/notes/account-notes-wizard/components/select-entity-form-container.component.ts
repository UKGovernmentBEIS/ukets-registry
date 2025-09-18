import { Component, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectAddNotesType } from '@registry-web/account-management/account/account-details/notes/store/account-notes.selector';
import {
  navigateAddNote,
  navigateCancelAddNote,
  saveNoteType,
} from '@registry-web/account-management/account/account-details/notes/store/account-notes.actions';
import { canGoBack } from '@registry-web/shared/shared.action';
import { NoteType, NoteTypeLabel } from '@registry-web/shared/model/note';
import { SelectEntityFormComponent } from '@notes/components/select-entity-form/select-entity-form.component';
import { CommonModule } from '@angular/common';
import { NotesWizardPathsModel } from '../../../../../../notes/model/notes-wizard-paths.model';

@Component({
  standalone: true,
  imports: [SelectEntityFormComponent, CommonModule],
  selector: 'app-select-entity-form-container',
  template: ` <app-select-entity-form
    [options]="options"
    [storedType]="storedType$ | async"
    (handleSubmit)="onContinue($event)"
    (handleCancel)="onCancel($event)"
  >
  </app-select-entity-form>`,
})
export class SelectEntityFormContainerComponent implements OnInit {
  options = [
    {
      label: NoteTypeLabel[NoteType.ACCOUNT],
      value: NoteType.ACCOUNT,
      enabled: true,
    },
    {
      label: NoteTypeLabel[NoteType.ACCOUNT_HOLDER],
      value: NoteType.ACCOUNT_HOLDER,
      enabled: true,
    },
  ];

  storedType$: Observable<NoteType>;
  constructor(private store: Store, private route: ActivatedRoute) {}
  ngOnInit(): void {
    this.storedType$ = this.store.select(selectAddNotesType);

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  private saveFormState(noteType: NoteType) {
    this.store.dispatch(
      saveNoteType({
        noteType: noteType,
      })
    );
  }

  onContinue(noteType: NoteType) {
    this.saveFormState(noteType);
    this.store.dispatch(navigateAddNote());
  }

  onCancel(noteType: NoteType) {
    this.saveFormState(noteType);
    this.store.dispatch(
      navigateCancelAddNote({
        currentRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/${NotesWizardPathsModel.BASE_PATH}/${
          NotesWizardPathsModel.SELECT_ENTITY
        }`,
      })
    );
  }
}
