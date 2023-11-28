import { Component, Input, OnInit } from '@angular/core';
import { TransactionProposalCompleteResponse } from '@task-management/model/task-complete-response.model';

@Component({
  selector: 'app-transaction-approval-confirmation',
  templateUrl: './transaction-approval-confirmation.component.html'
})
export class TransactionApprovalConfirmationComponent {
  @Input()
  transactionProposalCompleteResponse: TransactionProposalCompleteResponse;
}
