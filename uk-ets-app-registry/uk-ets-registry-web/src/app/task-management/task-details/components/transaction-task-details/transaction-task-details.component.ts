import { Component, Input } from '@angular/core';
import { TransactionTaskDetailsBase } from '@task-management/model';
import {
  TRANSACTION_TYPES_VALUES,
  TransactionType,
} from '@shared/model/transaction';

@Component({
  selector: 'app-transaction-task-details',
  template: `
    <fieldset class="govuk-fieldset">
      <hr class="govuk-section-break govuk-section-break--visible" />
      <app-issue-kp-units-task-details
        *ngIf="
          transactionTaskDetails.trType === TransactionType.IssueOfAAUsAndRMUs
        "
        [transactionTaskDetails]="transactionTaskDetails"
      ></app-issue-kp-units-task-details>
      <app-allowance-task-details
        *ngIf="
          transactionTaskDetails.trType === TransactionType.IssueAllowances
        "
        [transactionTaskDetails]="transactionTaskDetails"
      ></app-allowance-task-details>
      <app-generic-transaction-task-details
        *ngIf="!trTypeValues[transactionTaskDetails.trType].isIssuance"
        [transactionTaskDetails]="transactionTaskDetails"
        [isEtsTransaction]="isEtsTransaction"
        [isTransactionReversal]="isTransactionReversal"
      ></app-generic-transaction-task-details>
    </fieldset>
  `,
})
export class TransactionTaskDetailsComponent {
  TransactionType = TransactionType;

  @Input()
  transactionTaskDetails: TransactionTaskDetailsBase;

  @Input()
  isEtsTransaction: boolean;

  @Input()
  isTransactionReversal: boolean;

  readonly trTypeValues = TRANSACTION_TYPES_VALUES;
}
