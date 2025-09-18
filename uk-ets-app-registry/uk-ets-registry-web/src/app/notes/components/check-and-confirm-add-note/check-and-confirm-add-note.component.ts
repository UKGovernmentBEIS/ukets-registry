import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SharedModule } from '@registry-web/shared/shared.module';

@Component({
  standalone: true,
  imports: [SharedModule],
  selector: 'app-check-and-confirm-add-note',
  templateUrl: './check-and-confirm-add-note.component.html',
  styleUrls: ['./check-and-confirm-add-note.component.scss'],
})
export class CheckAndConfirmAddNoteComponent {
  @Input()
  storedTypeLabel: string;

  @Input()
  storedNote: string;

  @Output() readonly handleCancel = new EventEmitter<void>();
  @Output() readonly handleSubmit = new EventEmitter<void>();
  @Output() readonly handleChange = new EventEmitter<void>();

  submitChanges() {
    this.handleSubmit.emit();
  }

  onCancel() {
    this.handleCancel.emit();
  }

  onChange() {
    this.handleChange.emit();
  }
}
