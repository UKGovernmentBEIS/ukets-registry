import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  BulkClaimAccountActions,
  BulkClaimAccountNavigationActions,
} from '@bulk-claim-account/store/actions';
import { selectNumberAffectedAccounts } from '@bulk-claim-account/store/reducers';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-confirm-bulk-claim-account-container',
  template: `
    <app-confirm-bulk-claim-account
      [numberAffectedAccounts]="numberAffectedAccounts$ | async"
      (submitRequest)="onSubmit($event)"
    >
    </app-confirm-bulk-claim-account>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
})
export class ConfirmBulkClaimAccountContainerComponent implements OnInit {
  numberAffectedAccounts$!: Observable<number>;

  constructor(private store: Store) {}

  ngOnInit(): void {
    this.numberAffectedAccounts$ = this.store.select(
      selectNumberAffectedAccounts
    );
  }

  onCancel() {
    // Dispatch an action to navigate to the cancel request
    this.store.dispatch(
      BulkClaimAccountNavigationActions.navigateToBulkAccountClaimCancel()
    );
  }

  onSubmit($event: any) {
    // Dispatch an action to navigate to the submitted request
    this.store.dispatch(BulkClaimAccountActions.sendBulkClaimAccount());
  }
}
