import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-payment-weblink-error',
  templateUrl: './payment-weblink-error.component.html',
  styles: ``,
})
export class PaymentWeblinkErrorComponent {
  @Input()
  message: string;
}
