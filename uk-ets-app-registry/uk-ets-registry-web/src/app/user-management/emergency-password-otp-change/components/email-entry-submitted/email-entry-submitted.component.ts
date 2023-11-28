import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EmergencyPasswordOtpChangeRoutes } from '@user-management/emergency-password-otp-change/model';

@Component({
  selector: 'app-email-entry-submitted',
  templateUrl: './email-entry-submitted.component.html',
})
export class EmailEntrySubmittedComponent {
  @Input() email: string;

  @Output() readonly emergencyPasswordOtpChange = new EventEmitter();

  readonly routes = EmergencyPasswordOtpChangeRoutes;

  onClick() {
    this.emergencyPasswordOtpChange.emit();
  }
}
