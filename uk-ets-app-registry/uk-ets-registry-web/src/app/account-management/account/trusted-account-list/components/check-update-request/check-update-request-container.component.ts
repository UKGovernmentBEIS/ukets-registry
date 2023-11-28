import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { canGoBack } from '@shared/shared.action';
import { ActivatedRoute, Data, Router } from '@angular/router';
import {
  selectAccountsToUpdate,
  selectIsKyotoAccountAction,
  selectUpdateType,
} from '@account-management/account/trusted-account-list/reducers';
import { TrustedAccountListActions } from '@account-management/account/trusted-account-list/actions';
import { Observable } from 'rxjs';
import {
  TrustedAccountListRoutePaths,
  TrustedAccountListUpdateType,
} from '@account-management/account/trusted-account-list/model';
import { Account, TrustedAccount } from '@shared/model/account';
import {
  cancelClicked,
  submitUpdateRequest,
} from '@account-management/account/trusted-account-list/actions/trusted-account-list.actions';
import { selectAccount } from '@account-management/account/account-details/account.selector';

@Component({
  selector: 'app-check-update-request-container',
  template: `
    <app-check-update-request
      [account]="account$ | async"
      [isTrustedAccountKyotoType]="isTrustedAccountKyotoType$ | async"
      [updateType]="updateType$ | async"
      [trustedAccounts]="trustedAccounts$ | async"
      (updateRequestChecked)="onContinue()"
      (navigateToEmitter)="onStepBack($event)"
      (cancel)="onCancel($event)"
    ></app-check-update-request>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CheckUpdateRequestContainerComponent implements OnInit {
  updateType$: Observable<TrustedAccountListUpdateType>;
  trustedAccounts$: Observable<TrustedAccount[]>;
  account$: Observable<Account>;
  isTrustedAccountKyotoType$: Observable<boolean>;

  goBackPath: string;

  constructor(
    private store: Store,
    private activatedRoute: ActivatedRoute,
    private route: ActivatedRoute,
    private _router: Router
  ) {}

  ngOnInit() {
    this.route.data.subscribe((data: Data) => {
      this.initData(data);
    });
    this.updateType$ = this.store.select(selectUpdateType);
    this.trustedAccounts$ = this.store.select(selectAccountsToUpdate);
    this.account$ = this.store.select(selectAccount);
    this.isTrustedAccountKyotoType$ = this.store.select(
      selectIsKyotoAccountAction
    );
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/trusted-account-list/${this.goBackPath}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  protected initData(data: Data) {
    this.goBackPath = data.goBackPath;
  }

  onStepBack(step: TrustedAccountListRoutePaths) {
    this.store.dispatch(
      TrustedAccountListActions.navigateTo({
        route: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/trusted-account-list/${TrustedAccountListRoutePaths[step]}`,
        extras: { skipLocationChange: true },
      })
    );
  }

  onContinue() {
    this.store.dispatch(submitUpdateRequest());
  }

  onCancel(route: string) {
    this.store.dispatch(cancelClicked({ route }));
  }
}
