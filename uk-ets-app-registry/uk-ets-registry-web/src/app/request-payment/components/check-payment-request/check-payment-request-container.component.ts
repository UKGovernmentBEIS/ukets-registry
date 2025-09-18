import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import {
  cancelRequestPayment,
  downloadInvoice,
  submitPaymentRequest,
} from '@request-payment/store/actions';
import {
  selectAmount,
  selectDescription,
  selectRecipientName,
} from '@request-payment/store/reducers';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-check-payment-request-container',
  template: `
    <app-check-payment-request
      [recipientName]="recipientName$ | async"
      [amount]="amount$ | async"
      [description]="description$ | async"
      (submitRequest)="onSubmit()"
      (downloadInvoice)="onInvoiceDownload()"
    >
    </app-check-payment-request>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: ``,
})
export class CheckPaymentRequestContainerComponent implements OnInit {
  recipientName$: Observable<string>;
  amount$: Observable<number>;
  description$: Observable<string>;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.recipientName$ = this.store.select(selectRecipientName);
    this.amount$ = this.store.select(selectAmount);
    this.description$ = this.store.select(selectDescription);

    this.store.dispatch(
      canGoBack({
        goBackRoute: '/request-payment/set-payment-details',
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelRequestPayment({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onSubmit() {
    this.store.dispatch(submitPaymentRequest());
  }

  onInvoiceDownload() {
    this.store.dispatch(downloadInvoice());
  }
}
