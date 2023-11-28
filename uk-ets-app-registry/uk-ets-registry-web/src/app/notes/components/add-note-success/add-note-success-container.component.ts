import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';
import {
  clearNoteState,
  navigateToAccountNotes,
} from '@registry-web/notes/store/notes.actions';

@Component({
  selector: 'app-add-note-success-container',
  template: `<app-add-note-success
    (handleBackToNotes)="onBackToNotes()"
  ></app-add-note-success>`,
})
export class AddNoteSuccessContainerComponent {
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
