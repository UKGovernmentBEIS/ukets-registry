import { Component, Input } from '@angular/core';
import { AllowanceTransactionBlockSummary } from '@shared/model/transaction';

@Component({
  selector: 'app-allowance-transaction-quantity-table',
  templateUrl: './allowance-quantity-table.component.html',
  styleUrls: ['./allowance-quantity-table.component.scss']
})
export class AllowanceQuantityTableComponent {
  @Input()
  activeYear: number;

  _transactionBlockSummariesOrderedByYear: Partial<
    AllowanceTransactionBlockSummary
  >[];

  @Input()
  isEditable = false;
  /**
   * An implementation of the https://angular.io/api/core/TrackByFunction needed to prevent loss of focus in
   * quantity form, at the same time it improves the performance
   */
  trackByFn(index: number, summary: AllowanceTransactionBlockSummary) {
    return summary.year;
  }

  @Input()
  set transactionBlockSummaries(
    transactionBlockSummaries: Partial<AllowanceTransactionBlockSummary>[]
  ) {
    this._transactionBlockSummariesOrderedByYear = (
      transactionBlockSummaries || []
    )
      .slice()
      .sort((a, b) => (a.year < b.year ? -1 : 1));
  }
}
