import { Component, Input, OnInit } from '@angular/core';

/**
 * Common part for check and submit
 */
@Component({
  selector: 'app-issuance-transaction-summary-table',
  templateUrl: './issuance-transaction-summary-table.component.html',
  styleUrls: ['./issuance-transaction-summary-table.component.scss']
})
export class IssuanceTransactionSummaryTableComponent {
  @Input()
  unitType: string;

  @Input()
  initialQuantity: number;

  @Input()
  consumedQuantity: number;

  @Input()
  pendingQuantity: number;

  @Input()
  remainingQuantity: number;

  @Input()
  selectedQuantity: number;
}
