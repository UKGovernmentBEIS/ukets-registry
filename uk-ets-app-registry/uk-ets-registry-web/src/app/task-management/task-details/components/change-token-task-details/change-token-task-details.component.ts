import { Component, Input } from '@angular/core';
import { ChangeTokenTaskDetails } from '@task-management/model';

@Component({
  selector: 'app-change-token-task-details',
  templateUrl: './change-token-task-details.component.html',
})
export class ChangeTokenTaskDetailsComponent {
  @Input()
  taskDetails: ChangeTokenTaskDetails;
  @Input() isAdmin: boolean;
}
