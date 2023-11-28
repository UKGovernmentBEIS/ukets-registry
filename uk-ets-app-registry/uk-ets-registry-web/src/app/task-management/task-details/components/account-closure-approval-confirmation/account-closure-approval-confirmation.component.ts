import { Component, Input } from '@angular/core';
import { TaskCompleteResponse } from '@task-management/model/task-complete-response.model';

@Component({
  selector: 'app-account-closure-approval-confirmation',
  templateUrl: './account-closure-approval-confirmation.component.html',
})
export class AccountClosureApprovalConfirmationComponent {
  @Input()
  taskCompleteResponse: TaskCompleteResponse;
}
