import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EmergencyOtpChangeRoutes } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';

@Component({
  selector: 'app-email-entry-submitted',
  templateUrl: './email-entry-submitted.component.html',
})
export class EmailEntrySubmittedComponent {
  @Input() email: string;

  @Output() readonly emergencyOtpChange = new EventEmitter();

  readonly routes = EmergencyOtpChangeRoutes;

  onClick() {
    this.emergencyOtpChange.emit();
  }
}
