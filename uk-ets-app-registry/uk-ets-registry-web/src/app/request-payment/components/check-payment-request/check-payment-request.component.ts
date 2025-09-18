import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-check-payment-request',
  templateUrl: './check-payment-request.component.html',
  styles: ``,
})
export class CheckPaymentRequestComponent {
  @Input()
  recipientName: string;
  @Input()
  amount: number;
  @Input()
  description: string;

  @Output()
  readonly submitRequest = new EventEmitter();
  @Output()
  readonly downloadInvoice = new EventEmitter();

  onContinue() {
    this.submitRequest.emit();
  }

  download() {
    this.downloadInvoice.emit();
  }
}
