import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { navigateToBACSCancelPaymentMethod } from '@task-details/actions/task-details-navigation.actions';
import { bacsPaymentCompleteOrCancelled } from '@task-details/actions/task-details.actions';

@Component({
  selector: 'app-payment-bacs-confirm-container',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <div class="govuk-fieldset__legend govuk-fieldset__legend--xl">
          <h1 class="govuk-fieldset__heading">
            <span class="govuk-caption-xl">Make payment</span>
            Do you confirm that the payment will be made?
          </h1>
        </div>
        <button
          class="govuk-button"
          data-module="govuk-button"
          appDebounceClick
          (debounceClick)="onComplete()"
          id="submit"
        >
          Yes, confirm and complete
        </button>
      </div>
    </div>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  styles: ``,
})
export class PaymentBacsConfirmContainerComponent implements OnInit {
  constructor(
    private store: Store,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/task-details/${this.route.snapshot.paramMap.get(
          'requestId'
        )}/payment-bacs-details`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(navigateToBACSCancelPaymentMethod());
  }

  onComplete() {
    this.store.dispatch(
      bacsPaymentCompleteOrCancelled({ status: 'SUBMITTED' })
    );
  }
}
