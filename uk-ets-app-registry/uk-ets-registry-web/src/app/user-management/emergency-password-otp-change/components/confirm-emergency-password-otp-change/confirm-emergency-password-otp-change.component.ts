import { Component, Input } from '@angular/core';
import { EmergencyPasswordOtpChangeTaskResponse } from '@user-management/emergency-password-otp-change/model';

@Component({
  selector: 'app-confirm-emergency-password-otp-change',
  templateUrl: './confirm-emergency-password-otp-change.component.html'
})
export class ConfirmEmergencyPasswordOtpChangeComponent {
  @Input() taskResponse: EmergencyPasswordOtpChangeTaskResponse;
  constructor() {
    console.log(this.taskResponse);
  }
}
