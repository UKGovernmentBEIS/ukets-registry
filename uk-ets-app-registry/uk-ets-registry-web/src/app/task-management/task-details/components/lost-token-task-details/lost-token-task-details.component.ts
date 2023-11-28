import { Component, Input } from '@angular/core';
import { LostTokenTaskDetails } from '@task-management/model';

@Component({
  selector: 'app-lost-token-task-details',
  templateUrl: './lost-token-task-details.component.html',
})
export class LostTokenTaskDetailsComponent {
  @Input()
  taskDetails: LostTokenTaskDetails;
  @Input() isAdmin: boolean;
}
