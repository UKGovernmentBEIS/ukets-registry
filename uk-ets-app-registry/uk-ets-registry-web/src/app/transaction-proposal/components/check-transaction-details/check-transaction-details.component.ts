import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AccountInfo,
  AcquiringAccountInfo,
  ProposedTransactionType,
  ReturnExcessAllocationTransactionSummary,
  TRANSACTION_TYPES_VALUES,
  TransactionBlockSummary,
  TransactionSummary,
  TransactionType,
} from '@shared/model/transaction';
import { TransactionProposalRoutePaths } from '@transaction-proposal/model';
import { ItlNotification } from '@shared/model/transaction/itl-notification';
import { ExcessAmountPerAllocationAccount } from '@transaction-proposal/model/transaction-proposal-model';
import { ErrorSummary } from '@registry-web/shared/error-summary';

@Component({
  selector: 'app-check-transaction-details',
  templateUrl: './check-transaction-details.component.html',
  styleUrls: ['./check-transaction-details.component.scss'],
})
export class CheckTransactionDetailsComponent implements OnInit {
  otpCode: string;
  comment: string;
  disableSubmit = false;

  @Input()
  approvalRequired: boolean;
  @Input()
  approvalRole: string;
  @Input()
  itlNotification: ItlNotification;
  @Input()
  calculatedTransactionTypeDescription: string;
  @Input()
  transactionBlocks: TransactionBlockSummary[];
  @Input()
  transferringAccountInfo: AccountInfo;
  @Input()
  reversedTransferringAccountInfo: AccountInfo;
  @Input()
  acquiringAccountInfo: AcquiringAccountInfo;
  @Input()
  proposedTransactionType: ProposedTransactionType;
  @Input()
  transactionSummary: TransactionSummary;
  @Input()
  returnExcessAllocationTransactionSummary: ReturnExcessAllocationTransactionSummary;
  @Input()
  totalOverAllocatedQuantity: ExcessAmountPerAllocationAccount;
  @Input()
  isAdmin: boolean;
  @Input()
  allocationYear: number;
  @Input()
  allocationType: string;
  @Input()
  transactionReference: string;
  @Input()
  set errorSummary(value: ErrorSummary) {
    this.disableSubmit = false;
  }

  @Output() readonly transactionChecked = new EventEmitter<{
    comment: string;
    otpCode: string;
  }>();

  @Output() readonly navigateToEmitter =
    new EventEmitter<TransactionProposalRoutePaths>();

  readonly transactionType = TransactionType;
  readonly trTypeValues = TRANSACTION_TYPES_VALUES;

  public transactionProposalRoutePaths = TransactionProposalRoutePaths;

  ngOnInit(): void {
    this.transferringAccountInfo = this.transactionSummary.reversedIdentifier
      ? this.reversedTransferringAccountInfo
      : this.transferringAccountInfo;
  }

  onContinue() {
    this.disableSubmit = true;
    this.transactionChecked.emit({
      comment: this.comment,
      otpCode: this.otpCode,
    });
  }

  getTitleForYearSection() {
    switch (this.proposedTransactionType.type) {
      case TransactionType.ExcessAllocation:
        return 'Year of return';
      case TransactionType.ReverseAllocateAllowances:
        return 'Year of allocation';
      default:
    }
  }

  calculateQuantityToTransfer() {
    let quantity = 0;
    this.transactionBlocks.forEach(
      (block) => (quantity += Number(block.quantity))
    );
    return quantity;
  }

  navigateTo(value: TransactionProposalRoutePaths) {
    this.navigateToEmitter.emit(value);
  }

  setOtpCode(otpCode: string) {
    this.otpCode = otpCode;
  }

  setComment(comment: string) {
    this.comment = comment;
  }
}
