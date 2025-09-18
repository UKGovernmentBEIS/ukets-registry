import { Component, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-payment-bacs-cancel',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <div class="govuk-fieldset__legend govuk-fieldset__legend--xl">
          <h1 class="govuk-fieldset__heading">
            <span class="govuk-caption-xl">Cancel payment</span>

            Are you sure you want to cancel the payment?
          </h1>
        </div>
        <button
          (click)="onCancel()"
          class="govuk-button govuk-button--warning"
          data-module="govuk-button"
        >
          Cancel request
        </button>
      </div>
    </div>
  `,
  styles: ``,
})
export class PaymentBacsCancelComponent {
  @Output()
  readonly cancelRequest = new EventEmitter();

  onCancel() {
    this.cancelRequest.emit();
  }
}
