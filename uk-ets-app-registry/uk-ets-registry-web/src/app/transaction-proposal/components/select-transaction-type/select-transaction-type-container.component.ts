import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import {
  ProposedTransactionType,
  TransactionTypesResult,
} from '@shared/model/transaction';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import {
  selectAllowedTransactionTypes,
  selectITLNotificationId,
  selectTransactionType,
} from '@transaction-proposal/reducers';
import { Store } from '@ngrx/store';
import { TransactionProposalActions } from '@transaction-proposal/actions';
import { canGoBack, errors } from '@shared/shared.action';
import { cancelClicked } from '@transaction-proposal/actions/transaction-proposal.actions';
import { ErrorDetail, ErrorSummary } from '@shared/error-summary';
import { isAdmin } from '@registry-web/auth/auth.selector';
import { Account } from '@registry-web/shared/model/account';
import { selectAccount } from '@registry-web/account-management/account/account-details/account.selector';

@Component({
  selector: 'app-select-transaction-type-container',
  template: `
    <app-select-transaction-type
      [allowedTransactionTypes]="allowedTransactionTypes$ | async"
      [transactionType]="transactionType$ | async"
      [isAdmin]="isAdmin$ | async"
      [itlNotificationId]="itlNotificationId$ | async"
      [transferringAccount] ="transferringAccount$ | async"
      (selectedTransactionType)="onContinue($event)"
      (errorDetails)="onError($event)"
    ></app-select-transaction-type>
    <app-cancel-request-link (goToCancelScreen)="onCancel()">
    </app-cancel-request-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectTransactionTypeContainerComponent implements OnInit {
  allowedTransactionTypes$: Observable<TransactionTypesResult>;
  transactionType$: Observable<ProposedTransactionType>;
  itlNotificationId$: Observable<number>;
  isAdmin$: Observable<boolean>;
  transferringAccount$: Observable<Account>;

  constructor(private store: Store, private route: ActivatedRoute) {}

  ngOnInit() {
    this.store.dispatch(
      canGoBack({
        goBackRoute: `/account/${this.route.snapshot.paramMap.get(
          'accountId'
        )}`,
      })
    );
    this.allowedTransactionTypes$ = this.store.select(
      selectAllowedTransactionTypes
    );
    this.transactionType$ = this.store.select(selectTransactionType);
    this.itlNotificationId$ = this.store.select(selectITLNotificationId);
    this.transferringAccount$ = this.store.select(selectAccount);
    this.isAdmin$ = this.store.select(isAdmin);
  }

  onContinue({
    proposedTransactionType,
    itlNotificationId,
    clearNextStepsInWizard,
  }) {
    this.store.dispatch(
      TransactionProposalActions.setSelectedTransactionType({
        proposedTransactionType,
        itlNotificationId,
        clearNextStepsInWizard,
      })
    );
  }

  onCancel() {
    this.store.dispatch(
      cancelClicked({ route: this.route.snapshot['_routerState'].url })
    );
  }

  onError(details: ErrorDetail[]) {
    const summary: ErrorSummary = {
      errors: details,
    };
    this.store.dispatch(errors({ errorSummary: summary }));
  }
}
