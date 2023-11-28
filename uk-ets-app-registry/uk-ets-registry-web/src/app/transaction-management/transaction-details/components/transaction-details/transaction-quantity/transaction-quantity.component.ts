import { Component, Input, OnInit } from '@angular/core';
import { TransactionBlock } from '@transaction-management/model';

@Component({
  selector: 'app-transaction-quantity',
  template: `
    <app-transaction-quantity-table
      [transactionBlocks]="transactionBlocks"
      [isEtsTransaction]="isEtsTransaction"
    ></app-transaction-quantity-table>
  `,
})
export class TransactionQuantityComponent {
  @Input()
  transactionBlocks: Array<TransactionBlock>;
  @Input()
  transactionType: string;

  @Input()
  isEtsTransaction: boolean;
}
