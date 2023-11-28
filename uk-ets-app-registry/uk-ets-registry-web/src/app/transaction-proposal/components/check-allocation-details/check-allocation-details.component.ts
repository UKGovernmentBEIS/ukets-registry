import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AccountInfo,
  AcquiringAccountInfo,
  ProposedTransactionType,
  TRANSACTION_TYPES_VALUES,
  TransactionBlockSummary,
  TransactionType,
} from '@shared/model/transaction';
import { TransactionProposalRoutePaths } from '@transaction-proposal/model';

@Component({
  selector: 'app-check-allocation-details',
  templateUrl: './check-allocation-details.component.html',
  styleUrls: ['./check-allocation-details.component.scss'],
})
export class CheckAllocationDetailsComponent {
  @Input()
  transactionBlocks: TransactionBlockSummary[];
  @Input()
  transferringAccountInfo: AccountInfo;
  @Input()
  acquiringAccountInfo: AcquiringAccountInfo;
  @Input()
  proposedTransactionType: ProposedTransactionType;
  @Input()
  natQuantity: number;
  @Input()
  nerQuantity: number;
  @Input()
  natAcquiringAccountInfo: AcquiringAccountInfo;
  @Input()
  nerAcquiringAccountInfo: AcquiringAccountInfo;
  @Input()
  natReturnTransactionIdentifier: string;
  @Input()
  nerReturnTransactionIdentifier: string;
  @Input()
  totalOverAllocatedQuantity: number;
  @Input()
  allocationType: string;
  @Input()
  transactionReference: string;
  @Input()
  isTaskBased: boolean;
  @Input()
  singleTransactionIdentifier: string;

  @Output() readonly transactionChecked = new EventEmitter<{
    comment: string;
    otpCode: string;
  }>();

  @Output() readonly navigateToEmitter =
    new EventEmitter<TransactionProposalRoutePaths>();

  readonly transactionType = TransactionType;
  readonly trTypeValues = TRANSACTION_TYPES_VALUES;

  public transactionProposalRoutePaths = TransactionProposalRoutePaths;

  calculateQuantityToTransfer() {
    let quantity = 0;
    this.transactionBlocks.forEach(
      (block) => (quantity += Number(block.quantity))
    );
    return quantity;
  }

  onNavigateTo() {
    this.navigateToEmitter.emit(
      TransactionProposalRoutePaths['select-unit-types-quantity']
    );
  }
}
