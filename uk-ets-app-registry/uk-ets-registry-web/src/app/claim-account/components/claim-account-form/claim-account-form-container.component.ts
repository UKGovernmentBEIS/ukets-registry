import { Component, inject } from '@angular/core';
import { Store } from '@ngrx/store';
import { errors } from '@registry-web/shared/shared.action';
import { ClaimAccountFormComponent } from '@claim-account/components/claim-account-form/claim-account-form.component';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { AccountClaimDTO } from '@registry-web/shared/model/account';
import { ClaimAccountActions } from '@registry-web/claim-account/store';

@Component({
  selector: 'app-claim-account-form-container',
  template: `<app-claim-account-form
    (formSubmit)="onFormSubmit($event)"
    (errorDetails)="onError($event)"
  />`,
  standalone: true,
  imports: [ClaimAccountFormComponent],
})
export class ClaimAccountFormContainerComponent {
  private readonly store = inject(Store);

  onFormSubmit(accountClaimDTO: AccountClaimDTO) {
    this.store.dispatch(
      ClaimAccountActions.SUBMIT_REQUEST({ accountClaimDTO })
    );
  }

  onError(details: ErrorDetail[]) {
    this.store.dispatch(errors({ errorSummary: { errors: details } }));
  }
}
