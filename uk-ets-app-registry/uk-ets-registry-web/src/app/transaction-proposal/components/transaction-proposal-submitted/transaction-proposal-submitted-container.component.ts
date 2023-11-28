import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { canGoBack } from '@shared/shared.action';
import { Store } from '@ngrx/store';
import {
  selectEnrichedReturnExcessAllocationTransactionSummaryForSigning,
  selectEnrichedTransactionSummaryForSigning,
  selectEnrichedTransactionType,
  submissionBusinessCheckResult,
} from '@transaction-proposal/reducers';
import {
  BusinessCheckResult,
  ReturnExcessAllocationTransactionSummary,
  TransactionSummary,
  TransactionType,
} from '@shared/model/transaction';
import { Account } from '@shared/model/account';
import {
  selectAccount,
  selectIsUKAllocationorSurrenderAccount,
} from '@account-management/account/account-details/account.selector';
import { isAdmin } from '@registry-web/auth/auth.selector';

@Component({
  selector: 'app-transaction-proposal-submitted-container',
  template: `
    <app-transaction-proposal-submitted
      [businessCheckResult]="businessCheckResult$ | async"
      [isUKAllocationAccount]="isUKAllocationAccount$ | async"
      [enrichedTransactionSummaryForSigning]="
        enrichedTransactionSummaryForSigning$ | async
      "
      [enrichedReturnExcessAllocationTransactionSummaryForSigning]="
        enrichedReturnExcessAllocationTransactionSummaryForSigning$ | async
      "
      [transactionType]="enrichedTransactionType$ | async"
      [account]="account$ | async"
      [isAdmin]="isAdmin$ | async"
    ></app-transaction-proposal-submitted>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransactionProposalSubmittedContainerComponent implements OnInit {
  businessCheckResult$: Observable<BusinessCheckResult>;
  account$: Observable<Account>;
  enrichedTransactionSummaryForSigning$: Observable<TransactionSummary>;
  enrichedTransactionType$: Observable<TransactionType>;
  isUKAllocationAccount$: Observable<boolean>;
  isAdmin$: Observable<boolean>;
  enrichedReturnExcessAllocationTransactionSummaryForSigning$: Observable<ReturnExcessAllocationTransactionSummary>;

  constructor(private store: Store) {}

  ngOnInit() {
    this.store.dispatch(canGoBack({ goBackRoute: null }));
    this.isAdmin$ = this.store.select(isAdmin);
    this.businessCheckResult$ = this.store.select(
      submissionBusinessCheckResult
    );
    this.enrichedTransactionSummaryForSigning$ = this.store.select(
      selectEnrichedTransactionSummaryForSigning
    );
    this.enrichedTransactionType$ = this.store.select(
      selectEnrichedTransactionType
    );
    //TODO: Remove account selectors from transaction proposal (UKETS-4581)
    this.account$ = this.store.select(selectAccount);
    this.isUKAllocationAccount$ = this.store.select(
      selectIsUKAllocationorSurrenderAccount
    );
    this.enrichedReturnExcessAllocationTransactionSummaryForSigning$ =
      this.store.select(
        selectEnrichedReturnExcessAllocationTransactionSummaryForSigning
      );
  }
}
