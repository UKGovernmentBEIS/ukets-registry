import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { cancelRequestPaymentConfirmed } from '@request-payment/store/actions';
import { canGoBack } from '@shared/shared.action';

@Component({
  selector: 'app-cancel-request-payment-container',
  template: `
    <app-cancel-update-request
      notification="Are you sure you want to cancel the payment request?"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelRequestPaymentContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      this.goBackRoute = params.goBackRoute;
    });

    this.store.dispatch(
      canGoBack({
        goBackRoute: this.goBackRoute,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel(): void {
    this.store.dispatch(cancelRequestPaymentConfirmed());
  }
}
