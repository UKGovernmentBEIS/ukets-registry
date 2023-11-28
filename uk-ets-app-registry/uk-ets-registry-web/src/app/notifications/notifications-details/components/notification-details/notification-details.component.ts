import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import {
  Notification,
  NotificationType,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';
import { GdsDatePipe } from '@shared/pipes';
import { getLabelForCustomNotificationTime } from '@registry-web/shared/shared.util';

@Component({
  selector: 'app-notifications-details',
  templateUrl: './notification-details.component.html',
})
export class NotificationDetailsComponent implements OnInit {
  @Input()
  notificationId: string;
  @Input()
  timeOptions: any;
  @Input()
  notificationDetails: Notification;
  @Output() readonly navigateToEmitter = new EventEmitter<string>();

  constructor(private gdsDatePipe: GdsDatePipe) {}

  notificationTypeLabels = NotificationTypeLabels;
  scheduledTimeOptions = [
    {
      label: '',
      value: null,
    },
  ];

  ngOnInit() {
    this.scheduledTimeOptions = this.timeOptions;
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  getNotificationType(): SummaryListItem[] {
    return [
      {
        key: { label: 'Notification ID' },
        value: [
          {
            label: this.notificationId,
          },
        ],
      },
      {
        key: { label: 'Notification category' },
        value: [
          {
            label: this.notificationDetails?.type
              ? this.notificationTypeLabels[this.notificationDetails?.type]
                  .label
              : '',
          },
        ],
      },
    ];
  }

  getActivationDetailsItems(): SummaryListItem[] {
    const results = [
      {
        key: { label: 'Activation details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
      },
      {
        key: { label: 'Scheduled date' },
        value: [
          {
            label: this.gdsDatePipe.transform(
              this.notificationDetails?.activationDetails?.scheduledDate
            ),
          },
        ],
      },
      {
        key: { label: 'Scheduled time' },
        value: [
          {
            label:
              this.scheduledTimeOptions.filter(
                (time) =>
                  time.value ===
                  this.notificationDetails?.activationDetails?.scheduledTime
              )[0]?.label ||
              getLabelForCustomNotificationTime(
                this.notificationDetails?.activationDetails.scheduledDate,
                this.notificationDetails?.activationDetails.scheduledTime
              ),
          },
        ],
      },
    ];
    if (this.notificationDetails?.type !== NotificationType.AD_HOC) {
      results.push({
        key: { label: 'Recur every' },
        value: [
          {
            label: this.notificationDetails?.activationDetails?.recurrenceDays
              ? this.notificationDetails?.activationDetails?.recurrenceDays +
                ' day(s)'
              : '',
          },
        ],
      });
      results.push({
        key: { label: 'Expires by' },
        value: [
          {
            label: this.gdsDatePipe.transform(
              this.notificationDetails?.activationDetails?.expirationDate
            ),
          },
        ],
      });
    }
    if (this.notificationDetails?.type === NotificationType.AD_HOC) {
      results.push({
        key: { label: 'Expiration date' },
        value: [
          {
            label: this.gdsDatePipe.transform(
              this.notificationDetails?.activationDetails?.expirationDate
            ),
          },
        ],
      });
      results.push({
        key: { label: 'Expiration time' },
        value: [
          {
            label: this.scheduledTimeOptions.filter(
              (time) =>
                time.value ===
                this.notificationDetails?.activationDetails?.expirationTime
            )[0]?.label,
          },
        ],
      });
    }
    return results;
  }
}
