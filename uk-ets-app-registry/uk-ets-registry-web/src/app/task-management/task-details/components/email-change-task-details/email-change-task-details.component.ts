import { Component, Input, OnInit } from '@angular/core';
import { EmailChangeTaskDetails } from '@task-management/model';

@Component({
  selector: 'app-email-change-task-details',
  templateUrl: './email-change-task-details.component.html',
  styleUrls: ['./email-change-task-details.component.scss'],
})
export class EmailChangeTaskDetailsComponent {
  @Input() taskDetails: EmailChangeTaskDetails;
  @Input() isAdmin: boolean;
}
