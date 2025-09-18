import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { PaymentMethod } from '@request-payment/model';
import { submitMakePayment } from '@task-details/actions/task-details.actions';

@Component({
  selector: 'app-payment-select-method-container',
  template: `
    <app-payment-select-method
      (selectedPaymentMethod)="onSelectPaymentMethod($event)"
    >
    </app-payment-select-method>
  `,
  styles: ``,
})
export class PaymentSelectMethodContainerComponent implements OnInit {
  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onSelectPaymentMethod(selectedPayMethod: PaymentMethod) {
    this.store.dispatch(submitMakePayment({ method: selectedPayMethod }));
  }
}
