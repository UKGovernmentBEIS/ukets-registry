import { Component, EventEmitter, Input, Output } from '@angular/core';
import { EnrolmentKey } from '@user-management/user-details/model';

@Component({
  selector: 'app-activation-code-request',
  templateUrl: './activation-code-request.component.html',
})
export class ActivationCodeRequestComponent {
  @Input() enrolmentKeyDetails: EnrolmentKey;
  @Input() email: string;

  @Output() readonly submitRequest = new EventEmitter();

  onSubmit() {
    this.submitRequest.emit();
  }

  keyExpirationLabel(): string {
    return this.isActivationKeyValid()
      ? 'Current code expires:'
      : 'Current code expired:';
  }

  //Compares the activation code expiration day,month,year
  //with now
  isActivationKeyValid(): boolean {
    const expirationDate = new Date(
      this.enrolmentKeyDetails?.enrolmentKeyDateExpired
    );
    const now = new Date();
    return (
      expirationDate.getFullYear() >= now.getFullYear() &&
      expirationDate.getMonth() >= now.getMonth() &&
      expirationDate.getDate() >= now.getDate()
    );
  }
}
