import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';
import {
  clearNoteState,
  navigateToAccountNotes,
} from '@registry-web/account-management/account/account-details/notes/store/account-notes.actions';
import { AddNoteSuccessComponent } from '@notes/components/add-note-success/add-note-success.component';

@Component({
  standalone: true,
  selector: 'app-add-note-success-container',
  imports: [AddNoteSuccessComponent],
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
