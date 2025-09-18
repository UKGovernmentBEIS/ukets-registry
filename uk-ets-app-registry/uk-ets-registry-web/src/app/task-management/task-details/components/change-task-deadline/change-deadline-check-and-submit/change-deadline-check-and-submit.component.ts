import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UkDate } from '@registry-web/shared/model/uk-date';

@Component({
  selector: 'app-change-deadline-check-and-submit',
  templateUrl: './change-deadline-check-and-submit.component.html',
  styleUrls: ['./change-deadline-check-and-submit.component.scss'],
})
export class ChangeDeadlineCheckAndSubmitComponent {
  @Input() initialDeadline: Date;
  @Input() changedDeadline: Date;
  @Output() handleSubmit = new EventEmitter<string>();
  @Output() handleChange = new EventEmitter<string>();

  onChange() {
    this.handleChange.emit();
  }

  submitChanges() {
    this.handleSubmit.emit();
  }
}
