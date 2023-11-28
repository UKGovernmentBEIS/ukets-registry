import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AccountStatusActionOption,
  AccountStatusActionState
} from '@shared/model/account/account-status-action';
import { Store } from '@ngrx/store';
import { ActivatedRoute, Router } from '@angular/router';
import { canGoBack, clearErrors, errors } from '@shared/shared.action';
import {
  selectAccountStatusAction,
  selectAllowedAccountStatusActions
} from '../../store/reducers/account-status.selector';
import {
  cancelAccountStatus,
  setSelectedAccountStatusAction
} from '../../store/actions/account-status.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';

@Component({
  selector: 'app-select-account-status-action-container',
  template: `
    <app-select-account-status-action
      [allowedAccountStatusActions]="allowedAccountStatusActions$ | async"
      [accountStatusAction]="accountStatusAction$ | async"
      (cancelAccountStatusAction)="onCancel()"
      (selectedAccountStatusAction)="onContinue($event)"
      (errorDetails)="onError($event)"
    >
    </app-select-account-status-action>
  `,
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SelectAccountStatusActionContainerComponent implements OnInit {
  allowedAccountStatusActions$: Observable<AccountStatusActionOption[]>;
  accountStatusAction$: Observable<AccountStatusActionState>;
  nextRoute = 'confirm';

  constructor(
    private store: Store,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(clearErrors());
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get('accountId')}`
      })
    );
    this.accountStatusAction$ = this.store.select(selectAccountStatusAction);
    this.allowedAccountStatusActions$ = this.store.select(
      selectAllowedAccountStatusActions
    );
  }

  onContinue(value: AccountStatusActionState) {
    this.store.dispatch(
      setSelectedAccountStatusAction({ accountStatusAction: value })
    );
    this._router.navigate([this.nextRoute], {
      relativeTo: this.route,
      skipLocationChange: false
    });
  }
  onCancel() {
    this.store.dispatch(cancelAccountStatus());
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
