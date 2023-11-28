import { Component, Input } from '@angular/core';
import { EmergencyOtpChangeTaskResponse } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';

@Component({
  selector: 'app-confirm-emergency-otp-change',
  templateUrl: './confirm-emergency-otp-change.component.html'
})
export class ConfirmEmergencyOtpChangeComponent {
  @Input() taskResponse: EmergencyOtpChangeTaskResponse;
  constructor() {
    console.log(this.taskResponse);
  }
}
