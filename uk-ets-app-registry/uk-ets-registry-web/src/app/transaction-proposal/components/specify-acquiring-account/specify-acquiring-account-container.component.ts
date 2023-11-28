import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import {
  AccountInfo,
  AcquiringAccountInfo,
  CandidateAcquiringAccounts,
  ProposedTransactionType,
  TransactionType,
} from '@shared/model/transaction';
import { Store } from '@ngrx/store';
import {
  acquiringAccount,
  selectTransactionType,
  trustedAccountsResult,
  selectUserDefinedAccountParts,
  selectIsETSTransaction,
} from '@transaction-proposal/reducers';
import { SpecifyAcquiringAccountActions } from '@transaction-proposal/actions';
import { Observable } from 'rxjs';
import { canGoBack, errors } from '@shared/shared.action';
import { ActivatedRoute } from '@angular/router';
import { Account, UserDefinedAccountParts } from '@shared/model/account';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { cancelClicked } from '@transaction-proposal/actions/transaction-proposal.actions';

@Component({
  selector: 'app-specify-acquiring-account-container',
  template: `
    <app-specify-acquiring-account
      *ngIf="
        (transactionType$ | async)?.type !==
        transactionTypes.CentralTransferAllowances
      "
      [isEtsTransaction]="isEtsTransaction$ | async"
      [transactionType]="transactionType$ | async"
      [trustedAccountsResult]="candidateAcquiringAccountsResult$ | async"
      [userDefinedAccountParts]="userDefinedAccountParts$ | async"
      [selectedIdentifier]="selectedAcquiringIdentifier$ | async"
      [transferringAccount]="transferringAccount$ | async"
      (selectedAccountInfoEmitter)="onAcquiringAccountSubmitted($event)"
      (userDefinedAccountPartsEmitter)="onUserDefinedAccountSubmitted($event)"
      (errorDetails)="onError($event)"
    ></app-specify-acquiring-account>
    <app-select-acquiring-predefined-accounts
      *ngIf="
        (transactionType$ | async)?.type ===
        transactionTypes.CentralTransferAllowances
      "
      [transactionType]="transactionType$ | async"
      [candidateAcquiringAccountsResult]="
        candidateAcquiringAccountsResult$ | async
      "
      [selectedIdentifier]="selectedAcquiringIdentifier$ | async"
      (predefinedAccountEmitter)="onAcquiringAccountSubmitted($event)"
    >
    </app-select-acquiring-predefined-accounts>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SpecifyAcquiringAccountContainerComponent implements OnInit {
  transactionType$: Observable<ProposedTransactionType>;
  candidateAcquiringAccountsResult$: Observable<CandidateAcquiringAccounts>;
  userDefinedAccountParts$: Observable<UserDefinedAccountParts>;
  selectedAcquiringIdentifier$: Observable<AccountInfo>;
  transferringAccount$: Observable<Account>;
  isEtsTransaction$: Observable<boolean>;

  transactionTypes = TransactionType;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.transactionType$ = this.store.select(selectTransactionType);
    this.candidateAcquiringAccountsResult$ = this.store.select(
      trustedAccountsResult
    );
    this.userDefinedAccountParts$ = this.store.select(
      selectUserDefinedAccountParts
    );
    this.selectedAcquiringIdentifier$ = this.store.select(acquiringAccount);
    //TODO: Remove account selectors from transaction proposal (UKETS-4581)
    this.transferringAccount$ = this.store.select(selectAccount);

    this.isEtsTransaction$ = this.store.select(selectIsETSTransaction);

    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}/transactions/select-unit-types-quantity`,
        extras: {
          skipLocationChange: true,
        },
      })
    );
  }

  onUserDefinedAccountSubmitted(value: UserDefinedAccountParts) {
    this.store.dispatch(
      SpecifyAcquiringAccountActions.setUserDefinedAcquiringAccount({
        userDefinedAcquiringAccount: value,
      })
    );
  }

  onAcquiringAccountSubmitted(value: AcquiringAccountInfo) {
    this.store.dispatch(
      SpecifyAcquiringAccountActions.setAcquiringAccount({
        acquiringAccount: value,
      })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }
}
