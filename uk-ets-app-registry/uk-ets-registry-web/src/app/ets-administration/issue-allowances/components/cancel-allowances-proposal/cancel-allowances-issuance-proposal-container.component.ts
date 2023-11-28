import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { cancelAllowanceProposalConfirmed } from '@issue-allowances/actions/issue-allowance.actions';

@Component({
  selector: 'app-cancel-allowances-issuance-proposal-container',
  template: `
    <app-cancel-update-request
      updateRequestText="issuance of allowances"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelAllowancesIssuanceProposalContainerComponent
  implements OnInit {
  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute:
          'ets-administration/issue-allowances/check-request-and-sign',
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelAllowanceProposalConfirmed());
  }
}
