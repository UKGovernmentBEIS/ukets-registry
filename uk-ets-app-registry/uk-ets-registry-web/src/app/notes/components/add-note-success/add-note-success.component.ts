import { Component, EventEmitter, Output } from '@angular/core';

import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  imports: [RouterModule],
  selector: 'app-add-note-success',
  templateUrl: './add-note-success.component.html',
})
export class AddNoteSuccessComponent {
  @Output() handleBackToNotes = new EventEmitter<void>();

  onBackToNotes() {
    this.handleBackToNotes.emit();
  }
}
