import { Component, Output, EventEmitter } from '@angular/core';

@Component({
  standalone: true,
  imports: [],
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
