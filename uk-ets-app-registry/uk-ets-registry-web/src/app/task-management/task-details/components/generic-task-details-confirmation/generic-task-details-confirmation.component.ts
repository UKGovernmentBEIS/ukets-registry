import { Component, Input } from '@angular/core';
import { TaskCompleteResponse } from '@shared/task-and-regulator-notice-management/model';

@Component({
  selector: 'app-generic-task-details-confirmation',
  templateUrl: './generic-task-details-confirmation.component.html',
})
export class GenericTaskDetailsConfirmationComponent {
  @Input()
  taskCompleteResponse: TaskCompleteResponse;
}
