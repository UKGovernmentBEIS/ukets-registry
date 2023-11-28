import { Component, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectAddNotesType } from 'src/app/notes/store/notes.selector';
import {
  navigateAddNote,
  navigateCancelAddNote,
  saveNoteType,
} from '@registry-web/notes/store/notes.actions';
import { canGoBack } from '@registry-web/shared/shared.action';
import { NoteType } from '@registry-web/shared/model/note';

@Component({
  selector: 'app-select-entity-form-container',
  template: ` <app-select-entity-form
    [storedType]="storedType$ | async"
    (handleSubmit)="onContinue($event)"
    (handleCancel)="onCancel($event)"
  >
  </app-select-entity-form>`,
})
export class SelectEntityFormContainerComponent implements OnInit {
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
        )}/select-entity`,
      })
    );
  }
}
