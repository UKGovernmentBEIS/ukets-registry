import { Component, Input, OnInit } from '@angular/core';
import { TaskCompleteResponse } from '@shared/task-and-regulator-notice-management/model';

@Component({
  selector: 'app-allocation-table-approval-confirmation',
  templateUrl: './allocation-table-approval-confirmation.component.html',
})
export class AllocationTableApprovalConfirmationComponent {
  @Input()
  taskCompleteResponse: TaskCompleteResponse;
}
