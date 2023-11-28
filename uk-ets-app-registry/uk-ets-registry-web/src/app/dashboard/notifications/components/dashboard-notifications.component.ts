import { Component, Input } from '@angular/core';
import { DashboardNotification } from '@registry-web/dashboard/notifications/model';

@Component({
  selector: 'app-dashboard-notifications',
  templateUrl: './dashboard-notifications.component.html',
})
export class DashboardNotificationsComponent {
  @Input() notifications: DashboardNotification[];
}
