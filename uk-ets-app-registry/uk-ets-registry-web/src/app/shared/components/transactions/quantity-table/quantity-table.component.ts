import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonTransactionSummary } from '@shared/model/transaction';
import { TransactionProposalRoutePaths } from '@transaction-proposal/model';

@Component({
  selector: 'app-transaction-quantity-table',
  templateUrl: './quantity-table.component.html',
  styleUrls: ['./quantity-table.component.scss'],
})
export class QuantityTableComponent {
  @Input()
  transactionBlocks: CommonTransactionSummary[];

  @Input()
  isExcessAllocationTransaction: boolean;

  @Input()
  isEtsTransaction: boolean;

  @Input()
  allocationType: string;

  @Output() navigateToEmitter =
    new EventEmitter<TransactionProposalRoutePaths>();

  @Input()
  isTaskBased: boolean;

  onNavigateTo() {
    this.navigateToEmitter.emit(
      TransactionProposalRoutePaths['select-unit-types-quantity']
    );
  }

  getClassByAllocationType(allocationType: string, isKey: boolean) {
    if (allocationType === null || allocationType === undefined) {
      return isKey
        ? 'govuk-summary-list__'.concat('key no-border')
        : 'govuk-summary-list__'.concat('value no-border');
    } else if (allocationType === 'NAT_AND_NER') {
      return 'govuk-one-third-width-cell no-border';
    } else if (
      allocationType === 'NAT' ||
      allocationType === 'NAVAT' ||
      allocationType === 'NER'
    ) {
      return isKey
        ? 'govuk-summary-list__'.concat('key')
        : 'govuk-summary-list__'.concat('value');
    }
  }
}
