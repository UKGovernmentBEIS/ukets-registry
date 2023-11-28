import { Component, Input, OnInit, Output } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { selectAddNotesDescription } from 'src/app/notes/store/notes.selector';
import {
  navigateCancelAddNote,
  navigateCheckAndConfirm,
  saveNoteDescription,
} from '@registry-web/notes/store/notes.actions';
import { canGoBack } from '@registry-web/shared/shared.action';

@Component({
  selector: 'app-add-note-form-container',
  template: ` <app-add-note-form
    [storedNote]="storedNote$ | async"
    (handleSubmit)="onContinue($event)"
    (handleCancel)="onCancel($event)"
  >
  </app-add-note-form>`,
})
export class AddNoteFormContainerComponent implements OnInit {
  storedNote$: Observable<string>;
  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.storedNote$ = this.store.select(selectAddNotesDescription);

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/select-entity`,
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
        )}/add-note`,
      })
    );
  }
}
