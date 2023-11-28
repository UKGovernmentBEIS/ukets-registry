import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-email-change-verification',
  templateUrl: './email-change-verification.component.html',
})
export class EmailChangeVerificationComponent {
  @Input() newEmail: string;
  @Output() readonly restartEmailChangeWizard = new EventEmitter();

  requestEmailChangeAgain() {
    this.restartEmailChangeWizard.emit();
  }
}
