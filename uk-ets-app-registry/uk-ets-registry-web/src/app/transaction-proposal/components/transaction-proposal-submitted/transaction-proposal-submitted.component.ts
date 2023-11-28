import { Component, Input } from '@angular/core';
import {
  BusinessCheckResult,
  ReturnExcessAllocationTransactionSummary,
  TRANSACTION_TYPES_VALUES,
  TransactionSummary,
  TransactionType,
} from '@shared/model/transaction';
import { Account } from '@shared/model/account';
import { empty } from '@shared/shared.util';

@Component({
  selector: 'app-transaction-proposal-submitted',
  templateUrl: './transaction-proposal-submitted.component.html',
})
export class TransactionProposalSubmittedComponent {
  @Input()
  businessCheckResult: BusinessCheckResult;
  //TODO: Remove account and use transferring account identifier from transaction proposal reducer (UKETS-4581)
  @Input()
  account: Account;
  @Input()
  transactionType: TransactionType;
  @Input()
  enrichedTransactionSummaryForSigning: TransactionSummary;
  @Input()
  isUKAllocationAccount: boolean;
  @Input()
  isAdmin: boolean;
  @Input()
  enrichedReturnExcessAllocationTransactionSummaryForSigning: ReturnExcessAllocationTransactionSummary;

  readonly trTypeValues = TRANSACTION_TYPES_VALUES;

  backToDetails() {
    if (!empty(this.transactionType)) {
      if (this.trTypeValues[this.transactionType].isTransactionReversed) {
        return (
          '/transaction-details/' +
          this.enrichedTransactionSummaryForSigning.reversedIdentifier
        );
      }
      return '/account/' + this.account.identifier;
    }
  }
}
