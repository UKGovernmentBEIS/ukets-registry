import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  Notification,
  NotificationsWizardPathsModel,
  NotificationType,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';
import { SummaryListItem } from '@shared/summary-list/summary-list.info';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';
import { GdsDatePipe } from '@shared/pipes';
import { NotificationsUtils } from '@shared/utils/notifications.utils';
import {
  NOW,
  handleCustomScheduledTimeOption,
  copy,
  timeNow,
} from '@registry-web/shared/shared.util';
import { NotificationsFile } from '@shared/model/file';

@Component({
  selector: 'app-notification-check-and-submit',
  styleUrls: ['notification-check-and-submit.component.scss'],
  templateUrl: `./notification-check-and-submit.component.html`,
})
export class NotificationCheckAndSubmitComponent implements OnInit {
  @Input()
  timeOptions: any;
  @Input()
  notificationId: string;
  @Input()
  notificationRequest: NotificationRequestEnum;
  @Input()
  currentNotification: Notification;
  @Input()
  newNotification: Notification;
  @Input()
  tentativeRecipients: number;
  @Input()
  recipientsEmailsFile: NotificationsFile;
  @Output() readonly cancelEmitter = new EventEmitter();
  @Output() readonly navigateToEmitter = new EventEmitter<string>();
  @Output() readonly submitRequest = new EventEmitter<{
    notification: Notification;
    notificationId: string;
  }>();

  changedValues = {};
  notificationTypeLabels = NotificationTypeLabels;
  NotificationTypesEnum = NotificationType;
  NotificationRequestEnum = NotificationRequestEnum;
  scheduledTimeOptions = [
    {
      label: '',
      value: null,
    },
  ];

  constructor(private gdsDatePipe: GdsDatePipe) {}

  ngOnInit() {
    this.scheduledTimeOptions = copy(this.timeOptions);
    this.scheduledTimeOptions = handleCustomScheduledTimeOption(
      this.newNotification,
      this.scheduledTimeOptions
    );
    this.scheduledTimeOptions = handleCustomScheduledTimeOption(
      this.currentNotification,
      this.scheduledTimeOptions
    );
    if (this.notificationId) {
      this.changedValues = this.getObjectDiff(
        this.currentNotification,
        this.newNotification
      );
    }
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
            label: this.currentNotification?.type
              ? this.notificationTypeLabels[this.currentNotification?.type]
                  .label
              : '',
          },
        ],
      },
    ];
  }

  getObjectDiff(current, changed) {
    return {
      type: this.getOnlyChangedValues(current['type'], changed['type']),
      activationDetails: this.getOnlyChangedValues(
        current['activationDetails'],
        changed['activationDetails']
      ),
      contentDetails: this.getOnlyChangedValues(
        current['contentDetails'],
        changed['contentDetails']
      ),
    };
  }

  getOnlyChangedValues(initialValues, updatedValues) {
    const diff = {};
    if (updatedValues) {
      Object.keys(updatedValues).forEach((r) => {
        if (updatedValues[r] !== initialValues[r]) {
          diff[r] = updatedValues[r];
        }
      });
    }
    return diff;
  }

  onSubmit() {
    let notificationId = this.notificationId;
    const notification = copy(this.newNotification);
    if (notification.activationDetails.scheduledTime === NOW) {
      notification.activationDetails.scheduledTimeNow = true;
      notification.activationDetails.scheduledTime = timeNow(); //NOTE: This is needed to pass convertNotificationTimeZone
    }
    //NOTE: This is needed to send the data to the POST endpoint, instead of the PUT
    if (this.notificationRequest == NotificationRequestEnum.CLONE) {
      notificationId = null;
    }
    this.submitRequest.emit({
      notification: NotificationsUtils.convertNotificationTimeZone(
        notification,
        true
      ),
      notificationId: notificationId,
    });
  }

  navigateTo(value: string) {
    this.navigateToEmitter.emit(value);
  }

  getNotificationTypeItems(): SummaryListItem[] {
    return [
      {
        key: { label: 'Notification type' },
        value: [
          {
            label:
              this.notificationTypeLabels[this.newNotification?.type]?.label,
          },
        ],
      },
    ];
  }

  getActivationDetailsHeaderItems(): SummaryListItem[] {
    return [
      {
        key: { label: 'Activation details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          clickEvent: NotificationsWizardPathsModel.SCHEDULED_DATE,
        },
      },
    ];
  }

  getActivationDetailsItems(): SummaryListItem[] {
    const results = [
      {
        key: { label: 'Scheduled date' },
        value: [
          {
            label: this.gdsDatePipe.transform(
              this.newNotification?.activationDetails?.scheduledDate
            ),
          },
        ],
      },
      {
        key: { label: 'Scheduled time' },
        value: [
          {
            label: this.scheduledTimeOptions.filter(
              (time) =>
                time.value ===
                this.newNotification?.activationDetails?.scheduledTime
            )[0]?.label,
          },
        ],
      },
    ];
    if (this.newNotification?.type !== NotificationType.AD_HOC) {
      results.push({
        key: { label: 'Recur every' },
        value: [
          {
            label: this.newNotification?.activationDetails?.recurrenceDays
              ? this.newNotification?.activationDetails?.recurrenceDays +
                ' day(s)'
              : '',
          },
        ],
      });
      results.push({
        key: { label: 'Expires by' },
        value: [
          {
            label:
              this.newNotification?.activationDetails?.expirationDate == null
                ? ''
                : this.gdsDatePipe.transform(
                    this.newNotification?.activationDetails?.expirationDate
                  ),
          },
        ],
      });
    }
    if (this.newNotification?.type === NotificationType.AD_HOC) {
      results.push({
        key: { label: 'Expiration date' },
        value: [
          {
            label:
              this.newNotification?.activationDetails?.expirationDate == null
                ? ''
                : this.gdsDatePipe.transform(
                    this.newNotification?.activationDetails?.expirationDate
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
                this.newNotification?.activationDetails?.expirationTime
            )[0]?.label,
          },
        ],
      });
    }
    return results;
  }

  getNotificationDetailsHeaderItems(): SummaryListItem[] {
    return [
      {
        key: { label: 'Notification details', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
        action: {
          label: 'Change',
          visible: true,
          visuallyHidden: '',
          url: '',
          clickEvent: NotificationsWizardPathsModel.CONTENT,
        },
      },
    ];
  }

  getActivationDetailsSummaryDiff(): SummaryListItem[] {
    const results = [
      {
        key: { label: 'Field' },
        value: [
          {
            label: 'Current value',
            class: 'summary-list-change-header-font-weight',
          },
          {
            label: 'Changed value',
            class: 'summary-list-change-header-font-weight',
          },
        ],
      },
      {
        key: { label: 'Scheduled date' },
        value: [
          {
            label: this.gdsDatePipe.transform(
              this.currentNotification?.activationDetails?.scheduledDate
            ),
          },
          {
            label: this.gdsDatePipe.transform(
              this.changedValues['activationDetails']['scheduledDate']
            ),
            class: this.changedValues['activationDetails']['scheduledDate']
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
      {
        key: { label: 'Scheduled time' },
        value: [
          {
            label: this.scheduledTimeOptions.filter(
              (time) =>
                time.value ===
                this.currentNotification?.activationDetails?.scheduledTime
            )[0]?.label,
          },
          {
            label: this.scheduledTimeOptions.filter(
              (time) =>
                time.value ===
                this.changedValues['activationDetails']['scheduledTime']
            )[0]?.label,
            class: this.changedValues['activationDetails']['scheduledTime']
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      },
    ];
    if (this.newNotification?.type !== NotificationType.AD_HOC) {
      results.push({
        key: {
          label: 'Recur every',
        },
        value: [
          {
            label: this.currentNotification?.activationDetails?.recurrenceDays
              ? this.currentNotification?.activationDetails?.recurrenceDays +
                ' day(s)'
              : '',
          },
          {
            label: this.changedValues['activationDetails']['recurrenceDays']
              ? this.changedValues['activationDetails']['recurrenceDays'] +
                ' day(s)'
              : '',
            class:
              this.changedValues['activationDetails']['recurrenceDays'] &&
              this.changedValues['activationDetails']['recurrenceDays'] !=
                this.currentNotification?.activationDetails?.recurrenceDays
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      });
      results.push({
        key: { label: 'Expires by' },
        value: [
          {
            label:
              this.currentNotification?.activationDetails?.expirationDate ==
              null
                ? ''
                : this.gdsDatePipe.transform(
                    this.currentNotification?.activationDetails?.expirationDate
                  ),
          },
          {
            label:
              this.changedValues['activationDetails']['expirationDate'] == null
                ? ''
                : this.gdsDatePipe.transform(
                    this.changedValues['activationDetails']['expirationDate']
                  ),
            class:
              this.changedValues['activationDetails']['expirationDate'] !=
              undefined
                ? 'summary-list-change-notification'
                : '',
          },
        ],
      });
    }
    if (this.newNotification?.type === NotificationType.AD_HOC) {
      results.push({
        key: { label: 'Expiration date' },
        value: [
          {
            label:
              this.currentNotification?.activationDetails?.expirationDate ==
              null
                ? ''
                : this.gdsDatePipe.transform(
                    this.currentNotification?.activationDetails?.expirationDate
                  ),
          },
          {
            label:
              this.changedValues['activationDetails']['expirationDate'] == null
                ? ''
                : this.gdsDatePipe.transform(
                    this.changedValues['activationDetails']['expirationDate']
                  ),
            class: this.changedValues['activationDetails']['expirationDate']
              ? 'summary-list-change-notification'
              : '',
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
                this.currentNotification?.activationDetails?.expirationTime
            )[0]?.label,
          },
          {
            label: this.scheduledTimeOptions.filter(
              (time) =>
                time.value ===
                this.changedValues['activationDetails']['expirationTime']
            )[0]?.label,
            class: this.changedValues['activationDetails']['expirationTime']
              ? 'summary-list-change-notification'
              : '',
          },
        ],
      });
    }

    return results;
  }

  onCancel() {
    this.cancelEmitter.emit();
  }

  getNotificationRecipientHeaderItems(): SummaryListItem[] {
    return [
      {
        key: { label: 'Recipients', class: 'govuk-heading-m' },
        value: [
          {
            label: '',
          },
        ],
        action:
          this.newNotification?.type === NotificationType.AD_HOC_EMAIL
            ? {
                label: 'Change',
                visible: true,
                visuallyHidden: '',
                clickEvent: NotificationsWizardPathsModel.ADHOC_EMAIL,
              }
            : null,
      },
    ];
  }

  getNotificationRecipientItems(): SummaryListItem[] {
    const notificationRecipientItems: SummaryListItem[] = [
      {
        key: { label: 'Total number of tentative recipients', class: '' },
        value: [
          {
            label: this.tentativeRecipients?.toString(),
          },
        ],
      },
    ];
    if (this.newNotification?.type === NotificationType.AD_HOC_EMAIL) {
      notificationRecipientItems.splice(1, 0, {
        key: { label: 'File uploaded', class: '' },
        value: {
          label: '',
          class: 'forcibly-hide',
        },
        action: {
          label: this.recipientsEmailsFile.fileName,
          visible: true,
          visuallyHidden: '',
          clickEvent: 'download-emails-file',
          class: 'summary-list-link',
        },
      });
    }
    return notificationRecipientItems;
  }
}
