import { Component, Input } from '@angular/core';
import { ReturnExcessAllocationTransactionTaskDetailsDTO } from '@task-management/model';
import {
  TRANSACTION_TYPES_VALUES,
  TransactionBlockSummary,
  TransactionType,
} from '@shared/model/transaction';

@Component({
  selector: 'app-generic-transaction-task-details',
  templateUrl: './generic-transaction-task-details.component.html',
})
export class GenericTransactionTaskDetailsComponent {
  @Input()
  transactionTaskDetails: ReturnExcessAllocationTransactionTaskDetailsDTO;

  @Input()
  isEtsTransaction: boolean;

  @Input()
  isTransactionReversal: boolean;

  readonly trTypeValues = TRANSACTION_TYPES_VALUES;

  showAllocationYear() {
    switch (this.transactionTaskDetails.trType) {
      case TransactionType.ExcessAllocation:
        return 'Year of return';
      case TransactionType.AllocateAllowances:
      case TransactionType.ReverseAllocateAllowances:
        return 'Year of allocation';
      default:
        return false;
    }
  }

  showAllocationType() {
    switch (this.transactionTaskDetails.trType) {
      case TransactionType.AllocateAllowances:
        return true;
      default:
        return false;
    }
  }

  setTransactionBlocks(): TransactionBlockSummary[] {
    if (
      this.transactionTaskDetails.natTransactionBlocks === undefined &&
      this.transactionTaskDetails.nerTransactionBlocks === undefined
    ) {
      return this.transactionTaskDetails.transactionBlocks;
    }

    const transactionBlocks: TransactionBlockSummary[] = [];
    const summedBlocks: TransactionBlockSummary[] = [];
    this.transactionTaskDetails.natTransactionBlocks.forEach((block) =>
      transactionBlocks.push(block)
    );
    this.transactionTaskDetails.nerTransactionBlocks.forEach((block) =>
      transactionBlocks.push(block)
    );

    const newObj = transactionBlocks.reduce((accumulator, currentValue) => {
      const newAccumulator = { ...accumulator }; // create a copy of the accumulator object
      newAccumulator.quantity = (
        parseInt(accumulator.quantity) + parseInt(currentValue.quantity)
      ).toString();
      return newAccumulator;
    });
    summedBlocks.push(newObj);
    return summedBlocks;
  }
}
