import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-emergency-otp-change-init',
  templateUrl: './emergency-otp-change-init.component.html',
})
export class EmergencyOtpChangeInitComponent {
  @Input() cookiesAccepted: boolean;

  @Output() readonly continueToEmail = new EventEmitter();

  onContinue() {
    this.continueToEmail.emit();
  }
}
