import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';
import {
  clearNoteState,
  navigateToAccountNotes,
} from '@registry-web/notes/store/notes.actions';

@Component({
  selector: 'app-delete-note-success-container',
  template: `<app-delete-note-success
    (handleBackToNotes)="onBackToNotes()"
  ></app-delete-note-success>`,
})
export class DeleteNoteSuccessContainerComponent {
  constructor(private store: Store) {
    this.store.dispatch(clearGoBackRoute());
    this.clearState();
  }

  private clearState() {
    this.store.dispatch(clearNoteState());
  }

  onBackToNotes() {
    this.store.dispatch(navigateToAccountNotes());
  }
}
