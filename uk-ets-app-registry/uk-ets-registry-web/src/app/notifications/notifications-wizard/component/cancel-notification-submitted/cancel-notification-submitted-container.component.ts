import {
  ChangeDetectionStrategy,
  Component,
  OnInit
} from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-cancel-notification-submitted-container',
  template: `<app-request-submitted
    [confirmationMessageTitle]="
      'The notification has been cancelled.'
    "
    [notificationId]="notificationId"
    [isAdmin]="true"
  ></app-request-submitted>`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelNotificationSubmittedContainerComponent  implements OnInit
{
  notificationId: string;

  constructor(private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.notificationId = this.activatedRoute.snapshot.paramMap.get('notificationId');
  }
}
