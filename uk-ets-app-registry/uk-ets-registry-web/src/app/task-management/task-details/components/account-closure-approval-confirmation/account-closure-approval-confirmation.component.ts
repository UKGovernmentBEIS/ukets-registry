import { Component, Input } from '@angular/core';
import { TaskCompleteResponse } from '@shared/task-and-regulator-notice-management/model';

@Component({
  selector: 'app-account-closure-approval-confirmation',
  templateUrl: './account-closure-approval-confirmation.component.html',
})
export class AccountClosureApprovalConfirmationComponent {
  @Input()
  taskCompleteResponse: TaskCompleteResponse;
}
