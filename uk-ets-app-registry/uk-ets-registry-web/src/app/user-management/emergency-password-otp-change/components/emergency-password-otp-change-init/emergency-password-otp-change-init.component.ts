import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-emergency-password-otp-change-init',
  templateUrl: './emergency-password-otp-change-init.component.html',
})
export class EmergencyPasswordOtpChangeInitComponent {
  @Input() cookiesAccepted: boolean;
  @Output() readonly continueToEmail = new EventEmitter();

  onContinue() {
    this.continueToEmail.emit();
  }
}
