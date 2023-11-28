import { Component, Input } from '@angular/core';
import { TransactionDetails } from '@transaction-management/model';
import { ApiEnumTypes } from '@shared/model';
import { TransactionType } from '@shared/model/transaction';
import { empty } from '@shared/shared.util';

@Component({
  selector: 'app-transaction-info',
  templateUrl: './transaction-info.component.html',
  styleUrls: [],
})
export class TransactionInfoComponent {
  @Input()
  transactionDetails: TransactionDetails;
  @Input()
  isAdmin: boolean;
  ApiEnumTypes = ApiEnumTypes;

  showAllocationBasedInfo() {
    return (
      this.transactionDetails.type === TransactionType.AllocateAllowances ||
      this.transactionDetails.type ===
        TransactionType.ReverseAllocateAllowances ||
      this.transactionDetails.type === TransactionType.ExcessAllocation
    );
  }

  isExcess() {
    return this.transactionDetails.type === TransactionType.ExcessAllocation;
  }

  hasReversalRequests() {
    if (
      empty(this.transactionDetails) ||
      empty(this.transactionDetails.transactionConnectionSummary) ||
      empty(
        this.transactionDetails.transactionConnectionSummary.reversalIdentifier
      )
    ) {
      return false;
    }
    return this.transactionDetails.transactionConnectionSummary
      .reversalStatus === 'COMPLETED'
      ? 'This transaction has been reversed.'
      : 'A reversal request has been made for this transaction. You can check the status of the request below.';
  }
}
