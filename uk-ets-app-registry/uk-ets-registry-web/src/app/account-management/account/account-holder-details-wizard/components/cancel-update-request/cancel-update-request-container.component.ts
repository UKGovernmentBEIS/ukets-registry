import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack } from '@shared/shared.action';
import { AccountHolderDetailsWizardActions } from '@account-management/account/account-holder-details-wizard/actions';

@Component({
  selector: 'app-cancel-ar-update-request-container',
  template: `
    <app-cancel-update-request
      updateRequestText="account holder"
      (cancelRequest)="onCancel()"
    ></app-cancel-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelUpdateRequestContainerComponent implements OnInit {
  goBackRoute: string;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
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

  onCancel() {
    this.store.dispatch(
      AccountHolderDetailsWizardActions.cancelAccountHolderDetailsUpdateRequest()
    );
  }
}
