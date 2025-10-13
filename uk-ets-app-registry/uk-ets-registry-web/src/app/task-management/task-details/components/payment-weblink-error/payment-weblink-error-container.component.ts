import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';

@Component({
  selector: 'app-payment-weblink-error-container',
  template: `
    <app-payment-weblink-error [message]="this.message">
    </app-payment-weblink-error>
  `,
  styles: ``,
})
export class PaymentWeblinkErrorContainerComponent implements OnInit {
  message;

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.message = this.route.snapshot.queryParams['message'];
  }
}
