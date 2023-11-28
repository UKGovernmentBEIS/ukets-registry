import { Component, Input } from '@angular/core';
import { TaskCompleteResponse } from '@task-management/model/task-complete-response.model';

@Component({
  selector: 'app-generic-task-details-confirmation',
  templateUrl: './generic-task-details-confirmation.component.html',
})
export class GenericTaskDetailsConfirmationComponent {
  @Input()
  taskCompleteResponse: TaskCompleteResponse;
}
