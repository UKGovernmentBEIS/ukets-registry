import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  BulkClaimAccountActions,
  BulkClaimAccountNavigationActions,
} from '@bulk-claim-account/store/actions';

@Component({
  selector: 'app-cancel-bulk-claim-account-container',
  template: ` <div
      appScreenReaderPageAnnounce
      [pageTitle]="'Cancel bulk claim account request'"
    ></div>

    <app-cancel-update-request
      [notification]="
        'Are you sure you want to cancel the bulk claim account request and return back to the account page?'
      "
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: ``,
})
export class CancelBulkClaimAccountContainerComponent {
  constructor(private store: Store) {}

  onCancel() {
    this.store.dispatch(
      BulkClaimAccountActions.cancelBulkClaimAccountRequest()
    );
  }
}
