import { Component, Input } from '@angular/core';
import { RequestType } from '@task-management/model';
import { TaskCompleteResponse } from '@task-management/model/task-complete-response.model';

@Component({
  selector: 'app-task-approval-confirmation',
  templateUrl: './task-approval-confirmation.component.html',
})
export class TaskApprovalConfirmationComponent {
  @Input() taskCompleteResponse: TaskCompleteResponse;
  @Input() taskType: RequestType;

  requestTypes = RequestType;
}
