import { Component, Input } from '@angular/core';
import { RequestType } from '@shared/task-and-regulator-notice-management/model';
import { TaskCompleteResponse } from '@shared/task-and-regulator-notice-management/model';

@Component({
  selector: 'app-task-approval-confirmation',
  templateUrl: './task-approval-confirmation.component.html',
})
export class TaskApprovalConfirmationComponent {
  @Input() taskCompleteResponse: TaskCompleteResponse;
  @Input() taskType: RequestType;

  requestTypes = RequestType;
}
