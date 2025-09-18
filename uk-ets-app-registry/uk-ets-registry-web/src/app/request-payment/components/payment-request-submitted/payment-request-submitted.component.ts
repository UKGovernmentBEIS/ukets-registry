import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-payment-request-submitted',
  templateUrl: './payment-request-submitted.component.html',
  styles: ``,
})
export class PaymentRequestSubmittedComponent {
  @Input()
  submittedIdentifier: string;
  @Input()
  parentRequestId: string;
  @Output()
  readonly navigateToEmitter = new EventEmitter<{ parentRequestId: string }>();

  navigateTo() {
    this.navigateToEmitter.emit({
      parentRequestId: this.parentRequestId,
    });
  }
}
