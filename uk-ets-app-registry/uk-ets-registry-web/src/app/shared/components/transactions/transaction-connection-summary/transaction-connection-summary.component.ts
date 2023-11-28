import { Component, Input } from '@angular/core';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import {
  TransactionConnectionSummary,
  transactionStatusMap,
} from '@shared/model/transaction';
import { empty } from '@shared/shared.util';

@Component({
  selector: 'app-transaction-connection-summary',
  templateUrl: './transaction-connection-summary.component.html',
})
export class TransactionConnectionSummaryComponent {
  @Input()
  transactionConnectionSummary: TransactionConnectionSummary;
  transactionStatusMap = transactionStatusMap;

  get originalDetailsSummaryList(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'Original transaction',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
        ],
      },
      {
        key: {
          label: 'Transaction ID',
        },
        value: [
          {
            label: this.transactionConnectionSummary.originalIdentifier,
            url: `/transaction-details/${this.transactionConnectionSummary.originalIdentifier}`,
          },
        ],
      },
    ];
  }

  get reversedDetailsSummaryList(): SummaryListItem[] {
    return [
      {
        key: {
          label: 'Reversed transaction',
          class: 'govuk-heading-m',
        },
        value: [
          {
            label: '',
          },
        ],
      },
      {
        key: {
          label: 'Transaction ID',
        },
        value: [
          {
            label: this.transactionConnectionSummary.reversalIdentifier,
            url: `/transaction-details/${this.transactionConnectionSummary.reversalIdentifier}`,
          },
        ],
      },
      {
        key: { label: 'Status' },
        value: [
          {
            label:
              transactionStatusMap[
                this.transactionConnectionSummary.reversalStatus
              ].label,
            class:
              'govuk-summary-list__value tag tag-' +
              transactionStatusMap[
                this.transactionConnectionSummary.reversalStatus
              ].color,
          },
        ],
      },
    ];
  }

  get isReversalTransaction(): boolean {
    return !empty(this.transactionConnectionSummary.originalIdentifier);
  }
}
