import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { PaymentDetails } from '@request-payment/model';
import { User } from '@shared/user';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { Observable } from 'rxjs';
import {
  selectAmount,
  selectCandidateRecipients,
  selectDescription,
  selectOriginatingPath,
  selectRecipientUrid,
} from '@request-payment/store/reducers';
import {
  cancelRequestPayment,
  setPaymentDetails,
} from '@request-payment/store/actions';

@Component({
  selector: 'app-set-payment-details-container',
  template: `
    <app-set-payment-details
      [candidateRecipients]="candidateRecipients$ | async"
      [recipientUrid]="recipientUrid$ | async"
      [amount]="amount$ | async"
      [description]="description$ | async"
      (setPaymentDetails)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-set-payment-details>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: ``,
})
export class SetPaymentDetailsContainerComponent implements OnInit {
  candidateRecipients$: Observable<User[]>;
  recipientUrid$: Observable<string>;
  amount$: Observable<number>;
  description$: Observable<string>;

  originatingPath: string;

  constructor(
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit(): void {
    this.candidateRecipients$ = this.store.select(selectCandidateRecipients);
    this.recipientUrid$ = this.store.select(selectRecipientUrid);
    this.amount$ = this.store.select(selectAmount);
    this.description$ = this.store.select(selectDescription);
    this.store
      .select(selectOriginatingPath)
      // eslint-disable-next-line ngrx/no-store-subscription
      .subscribe((path) => (this.originatingPath = path));

    this.store.dispatch(
      canGoBack({
        goBackRoute: this.originatingPath,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    console.log(`Component onError:${JSON.stringify(details)}`);
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelRequestPayment({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onContinue(details: PaymentDetails) {
    this.store.dispatch(
      setPaymentDetails({
        recipientUrid: details.recipientUrid,
        recipientName: details.recipientName,
        amount: details.amount,
        description: details.description,
      })
    );
  }
}
