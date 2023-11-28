import { Component, Input, OnInit } from '@angular/core';
import { TaskCompleteResponse } from '@task-management/model/task-complete-response.model';

@Component({
  selector: 'app-allocation-table-approval-confirmation',
  templateUrl: './allocation-table-approval-confirmation.component.html'
})
export class AllocationTableApprovalConfirmationComponent {
  @Input()
  taskCompleteResponse: TaskCompleteResponse;
}
