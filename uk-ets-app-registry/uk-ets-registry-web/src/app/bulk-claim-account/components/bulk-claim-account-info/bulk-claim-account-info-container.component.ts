import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { BulkClaimAccountActions } from '@bulk-claim-account/store/actions';

@Component({
  selector: 'app-bulk-claim-account-info-container',
  template: `
    <app-bulk-claim-account-info
      (sendInvitationClicked)="
        navigateToBulkAccountClaimCheckRequestAndSubmit()
      "
    >
    </app-bulk-claim-account-info>
  `,
  styles: ``,
})
export class BulkClaimAccountInfoContainerComponent {
  constructor(private store: Store) {}

  navigateToBulkAccountClaimCheckRequestAndSubmit() {
    // Dispatch an action to fetch the number of eligible accounts
    // before navigating to the Bulk Account Claim Check Request and Submit page
    this.store.dispatch(
      BulkClaimAccountActions.countEligibleBulkClaimAccounts()
    );
  }
}
