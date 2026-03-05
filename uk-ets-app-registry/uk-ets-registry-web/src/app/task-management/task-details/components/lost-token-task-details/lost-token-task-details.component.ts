import { Component, Input } from '@angular/core';
import { LostTokenTaskDetails } from '@shared/task-and-regulator-notice-management/model';

@Component({
  selector: 'app-lost-token-task-details',
  templateUrl: './lost-token-task-details.component.html',
})
export class LostTokenTaskDetailsComponent {
  @Input()
  taskDetails: LostTokenTaskDetails;
  @Input() isAdmin: boolean;
}
