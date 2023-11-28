import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { cancelExcludeBillingConfirm } from '@account-management/account/account-details/account.actions';

@Component({
  selector: 'app-cancel-exclude-billing-container',
  template: `
    <app-cancel-update-request
      notification="Are you sure you want to cancel the exclusion?"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelExcludeBillingContainerComponent implements OnInit {
  constructor(private store: Store, private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}/exclude-billing`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onCancel() {
    this.store.dispatch(cancelExcludeBillingConfirm());
  }
}
