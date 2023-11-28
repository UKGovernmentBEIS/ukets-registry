import { Component, Input } from '@angular/core';
import {
  Notification,
  NotificationsWizardPathsModel,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';
import { notificationStatusMap } from '@notifications/notifications-list/model';
import { SearchMode } from '@shared/resolvers/search.resolver';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-notification-header',
  templateUrl: './notification-header.component.html',
  styleUrls: ['../../../sub-headers/styles/sub-header.scss'],
})
export class NotificationHeaderComponent {
  @Input() notification: Notification;
  @Input()
  notificationHeaderVisibility: boolean;
  @Input()
  showRequestUpdate: boolean;
  @Input()
  showClone: boolean;
  @Input()
  showBackToList: boolean;

  searchMode = SearchMode;

  notificationTypeLabels = NotificationTypeLabels;
  notificationStatusMap = notificationStatusMap;

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {}

  goToUpdateNotificationDetails(): void {
    this.router.navigate([
      `notifications`,
      this.notificationId,
      NotificationsWizardPathsModel.BASE_PATH,
    ]);
  }

  goToCloneNotification(): void {
    this.router.navigate([
      `notifications`,
      this.notificationId,
      NotificationsWizardPathsModel.CLONE,
    ]);
  }

  private get notificationId() {
    return this.activatedRoute.snapshot.params['notificationId'];
  }
}
