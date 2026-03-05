import { Component, Input, OnInit } from '@angular/core';
import { TransactionProposalCompleteResponse } from '@shared/task-and-regulator-notice-management/model';

@Component({
  selector: 'app-transaction-approval-confirmation',
  templateUrl: './transaction-approval-confirmation.component.html',
})
export class TransactionApprovalConfirmationComponent {
  @Input()
  transactionProposalCompleteResponse: TransactionProposalCompleteResponse;
}
