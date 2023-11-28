import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { Store } from '@ngrx/store';
import { TaskDetails } from '@task-management/model';

@Component({
  selector: 'app-task-completion-pending-confirmation',
  templateUrl: './task-completion-pending-confirmation.component.html',
})
export class TaskCompletionPendingConfirmationComponent {
  @Input() taskDetails: TaskDetails;
  @Input() leaveUrl: string;

  constructor(private router: Router, private store: Store) {}

  onGoBack() {
    this.router.navigate([`/task-details/${this.taskDetails.requestId}`]);
  }

  onLeave() {
    this.router.navigateByUrl(this.leaveUrl);
  }
}
