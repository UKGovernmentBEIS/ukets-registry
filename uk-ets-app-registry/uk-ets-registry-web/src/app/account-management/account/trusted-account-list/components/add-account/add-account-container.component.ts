import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Account, UserDefinedAccountParts } from '@shared/model/account';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { canGoBack, errors } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import {
  selectNewTrustedAccountDescription,
  selectUserDefinedAccountParts
} from '@trusted-account-list/reducers';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { Observable } from 'rxjs';
import { TrustedAccountListActions } from '@trusted-account-list/actions';
import { AddTrustedAccount } from '@trusted-account-list/model/add-trusted-account';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-add-account-container',
  template: `
    <app-add-account
      [account]="account$ | async"
      [userDefinedAccountParts]="userDefinedAccountParts$ | async"
      [trustedAccountDescription]="trustedAccountDescription$ | async"
      (addTrustedAccountEmitter)="onTrustedAccountSubmitted($event)"
      (errorDetails)="onError($event)"
    ></app-add-account>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AddAccountContainerComponent implements OnInit {
  account$: Observable<Account>;
  userDefinedAccountParts$: Observable<UserDefinedAccountParts>;
  trustedAccountDescription$: Observable<string>;

  constructor(
    private store: Store,
    private router: Router,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit() {
    this.account$ = this.store.select(selectAccount);
    this.userDefinedAccountParts$ = this.store.select(
      selectUserDefinedAccountParts
    );
    this.trustedAccountDescription$ = this.store.select(
      selectNewTrustedAccountDescription
    );

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.activatedRoute.snapshot.paramMap.get(
          'accountId'
        )}/trusted-account-list/select-update-type`
      })
    );
  }

  onTrustedAccountSubmitted(value: AddTrustedAccount) {
    this.store.dispatch(
      TrustedAccountListActions.setUserDefinedTrustedAccount({
        userDefinedTrustedAccountParts: value.account,
        userDefinedTrustedAccountDescription: value.description
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
