import { Component, EventEmitter, Output } from '@angular/core';

@Component({
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
