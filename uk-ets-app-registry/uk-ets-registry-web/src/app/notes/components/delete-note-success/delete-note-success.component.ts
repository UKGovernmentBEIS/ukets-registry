import { Component, EventEmitter, Output } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  standalone: true,
  imports: [RouterModule],
  selector: 'app-delete-note-success',
  templateUrl: './delete-note-success.component.html',
})
export class DeleteNoteSuccessComponent {
  @Output()
  handleBackToNotes = new EventEmitter();

  onBackToNotes() {
    this.handleBackToNotes.emit();
  }
}
