import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Store } from '@ngrx/store';
import {
  Notification,
  NotificationType,
  NotificationTypeLabels
} from '@notifications/notifications-wizard/model';
import { getLabelForCustomNotificationTime } from '@registry-web/shared/shared.util';
import { GdsDatePipe } from '@shared/pipes';
import { downloadEmailsFile } from '@shared/shared.action';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';

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

  constructor(
    private gdsDatePipe: GdsDatePipe,
    private store: Store
  ) {}

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

  downloadFile() {
    this.store.dispatch(
      downloadEmailsFile({ fileId: this.notificationDetails.uploadedFileId })
    );
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

  getNotificationRecipientItems(): SummaryListItem[] {
    if (this.notificationDetails?.type === NotificationType.AD_HOC_EMAIL) {
      return [
        {
          key: { label: 'Recipients', class: 'govuk-heading-m' },
          value: [
            {
              label: '',
            },
          ],
        },
        {
          key: { label: 'File uploaded', class: '' },
          value: {
            label: '',
            class: 'forcibly-hide',
          },
          action: {
            label: 'recipients.xslx',
            visible: true,
            visuallyHidden: '',
            clickEvent: 'download-emails-file',
            class: 'summary-list-link',
          },
        },
      ];
    }
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
            label: this.notificationDetails?.activationDetails?.expirationDate == null ? '' :
              this.gdsDatePipe.transform(
                this.notificationDetails?.activationDetails?.expirationDate
              )
          },
        ],
      });
    }
    if (this.notificationDetails?.type === NotificationType.AD_HOC) {
      results.push({
        key: { label: 'Expiration date' },
        value: [
          {
            label: this.notificationDetails?.activationDetails?.expirationDate == null ? '':
             this.gdsDatePipe.transform(
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
