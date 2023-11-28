import { Component, Input } from '@angular/core';
import {
  ProposedTransactionType,
  TRANSACTION_TYPES_VALUES,
} from '@shared/model/transaction';

@Component({
  selector: 'app-title-proposal-transaction-type',
  template: `
    <span class="govuk-caption-xl">
      {{
        titleLabel[proposalTransactionType?.type]?.label
          .transactionProposalLabel
      }}
    </span>
  `,
})
export class TitleProposalTransactionTypeComponent {
  @Input()
  proposalTransactionType: ProposedTransactionType;

  readonly titleLabel = TRANSACTION_TYPES_VALUES;
}
