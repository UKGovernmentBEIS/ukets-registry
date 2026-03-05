import { Component, Input, OnInit } from '@angular/core';
import { RequestAllocationProposalCompleteResponse } from '@shared/task-and-regulator-notice-management/model';

@Component({
  selector: 'app-request-allocation-task-approval-confirmation',
  templateUrl: './request-allocation-task-approval-confirmation.component.html',
})
export class RequestAllocationTaskApprovalConfirmationComponent {
  @Input()
  taskCompleteResponse: RequestAllocationProposalCompleteResponse;
}
