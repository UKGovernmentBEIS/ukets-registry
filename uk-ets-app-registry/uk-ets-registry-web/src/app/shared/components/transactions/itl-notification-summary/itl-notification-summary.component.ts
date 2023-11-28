import { Component, Input } from '@angular/core';
import { ItlNotification } from '@shared/model/transaction/itl-notification';

@Component({
  selector: 'app-itl-notification-summary',
  templateUrl: './itl-notification-summary.component.html',
})
export class ItlNotificationSummaryComponent {
  @Input()
  itlNotification: ItlNotification;
}
