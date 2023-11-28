import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-delete-note',
  templateUrl: './delete-note.component.html',
})
export class DeleteNoteComponent {
  @Output()
  handleDelete = new EventEmitter<void>();

  onDelete() {
    this.handleDelete.emit();
  }
}
