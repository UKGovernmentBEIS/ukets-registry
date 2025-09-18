import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import {
  Notification,
  NotificationType,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';

@Component({
  selector: 'app-select-notification-type',
  templateUrl: './select-notification-type.component.html',
})
export class SelectNotificationTypeComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  notificationRequest: NotificationRequestEnum;
  @Output()
  readonly selectNotificationType = new EventEmitter<NotificationType>();
  @Output() readonly cancelEmitter = new EventEmitter();
  @Input()
  notification: Notification;
  @Input()
  notificationId: string;

  formRadioGroupInfo: FormRadioGroupInfo;
  NotificationRequestEnum = NotificationRequestEnum;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Select notification type',
      radioGroupHeadingCaption:
        this.notificationRequest === NotificationRequestEnum.UPDATE
          ? 'Request to update the notification'
          : 'New notification',
      key: 'notificationType',
      options: [],
      radioGroupSubHeading: 'Email notifications',
      subGroups: [
        {
          heading: 'Compliance notifications',
          options: [
            {
              label: NotificationTypeLabels.EMISSIONS_MISSING_FOR_OHA.label,
              value: NotificationType.EMISSIONS_MISSING_FOR_OHA,
              hint: NotificationTypeLabels.EMISSIONS_MISSING_FOR_OHA
                .description,
              enabled: !this.notificationId,
            },
            {
              label: NotificationTypeLabels.SURRENDER_DEFICIT_FOR_OHA.label,
              value: NotificationType.SURRENDER_DEFICIT_FOR_OHA,
              hint: NotificationTypeLabels.SURRENDER_DEFICIT_FOR_OHA
                .description,
              enabled: !this.notificationId,
            },
            {
              label: NotificationTypeLabels.EMISSIONS_MISSING_FOR_AOHA.label,
              value: NotificationType.EMISSIONS_MISSING_FOR_AOHA,
              hint: NotificationTypeLabels.EMISSIONS_MISSING_FOR_AOHA
                .description,
              enabled: !this.notificationId,
            },
            {
              label: NotificationTypeLabels.SURRENDER_DEFICIT_FOR_AOHA.label,
              value: NotificationType.SURRENDER_DEFICIT_FOR_AOHA,
              hint: NotificationTypeLabels.SURRENDER_DEFICIT_FOR_AOHA
                .description,
              enabled: !this.notificationId,
            },
            {
              label:
                NotificationTypeLabels
                  .YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS.label,
              hint: NotificationTypeLabels
                .YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS.description,
              value:
                NotificationType.YEARLY_INTRODUCTION_TO_OHA_AOHA_WITH_OBLIGATIONS,
              enabled: !this.notificationId,
            },
          ],
        },
        {
          heading: 'Other notifications',
          options: [
            {
              label: NotificationTypeLabels.USER_INACTIVITY.label,
              value: NotificationType.USER_INACTIVITY,
              hint: NotificationTypeLabels.USER_INACTIVITY.description,
              enabled: !this.notificationId,
            },
            {
              label: NotificationTypeLabels.AD_HOC_EMAIL.label,
              value: NotificationType.AD_HOC_EMAIL,
              hint: NotificationTypeLabels.AD_HOC_EMAIL.description,
              enabled: !this.notificationId,
            },
          ],
        },
        {
          heading: 'Dashboard notifications',
          options: [
            {
              label: NotificationTypeLabels.AD_HOC.label,
              value: NotificationType.AD_HOC,
              hint: NotificationTypeLabels.AD_HOC.description,
              enabled: !this.notificationId,
            },
          ],
        },
      ],
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    this.selectNotificationType.emit(
      this.formGroup.get('notificationType').value
    );
  }

  protected getFormModel(): any {
    return {
      notificationType: [this.notification?.type, Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      notificationType: {
        required: 'Select a notification type',
      },
    };
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
