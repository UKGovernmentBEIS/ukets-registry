import { Component, EventEmitter, Input, Output } from '@angular/core';
import { PaymentMethod, PaymentStatus } from '@request-payment/model';
import {
  PaymentCompleteResponse,
  RequestPaymentTaskDetails,
} from '@task-management/model';

@Component({
  selector: 'app-payment-confirmation',
  templateUrl: './payment-confirmation.component.html',
  styles: ``,
})
export class PaymentConfirmationComponent {
  @Input()
  paymentCompletionDetails: PaymentCompleteResponse;
  @Input()
  isAuthenticated: boolean;
  @Output()
  readonly navigateToTaskDetailsEmitter = new EventEmitter();
  @Output()
  readonly downloadReceiptEmitter = new EventEmitter();

  onBackToTask() {
    this.navigateToTaskDetailsEmitter.emit();
  }

  downloadReceipt() {
    this.downloadReceiptEmitter.emit();
  }

  paymentMethod() {
    return PaymentMethod.label(
      (
        this.paymentCompletionDetails
          .taskDetailsDTO as RequestPaymentTaskDetails
      ).paymentMethod
    );
  }

  paymentStatus() {
    return PaymentStatus.label(
      (
        this.paymentCompletionDetails
          .taskDetailsDTO as RequestPaymentTaskDetails
      ).paymentStatus
    );
  }

  isBACSPayment() {
    return (
      (
        this.paymentCompletionDetails
          .taskDetailsDTO as RequestPaymentTaskDetails
      ).paymentMethod === 'BACS'
    );
  }

  isCardOrLinkPayment() {
    const requestPaymentTaskDetail = this.paymentCompletionDetails
      .taskDetailsDTO as RequestPaymentTaskDetails;
    return (
      requestPaymentTaskDetail.paymentMethod === 'CARD_OR_DIGITAL_WALLET' ||
      requestPaymentTaskDetail.paymentMethod === 'WEBLINK'
    );
  }
}
