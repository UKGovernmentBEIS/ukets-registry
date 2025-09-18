import { Component, EventEmitter, Output } from '@angular/core';
import { Store } from '@ngrx/store';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  imports: [RouterModule],
  selector: 'app-add-note-success',
  templateUrl: './add-note-success.component.html',
})
export class AddNoteSuccessComponent {
  @Output()
  handleBackToNotes = new EventEmitter<void>();

  constructor(private store: Store) {}

  onBackToNotes() {
    this.handleBackToNotes.emit();
  }
}
