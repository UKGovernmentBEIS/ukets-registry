import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { clearGoBackRoute } from '@registry-web/shared/shared.action';
import {
  clearNoteState,
  navigateToAccountNotes,
} from '../../store/notes.actions';

@Component({
  selector: 'app-add-note-success',
  templateUrl: './add-note-success.component.html',
})
export class AddNoteSuccessComponent {
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
