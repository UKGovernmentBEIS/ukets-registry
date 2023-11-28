import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  createNote,
  navigateCancelAddNote,
  navigateSelectEntity,
} from '@registry-web/notes/store/notes.actions';
import { Observable } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { canGoBack } from '@registry-web/shared/shared.action';
import {
  selectAddNotesDescription,
  selectAddNotesTypeLabel,
} from 'src/app/notes/store/notes.selector';

@Component({
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
        )}/add-note`,
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
        )}/check-and-confirm-add-note`,
      })
    );
  }
}
