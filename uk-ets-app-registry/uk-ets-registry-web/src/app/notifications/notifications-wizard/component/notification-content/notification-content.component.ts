import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  Notification,
  NotificationContent,
  NotificationDefinition,
  NotificationType,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';

@Component({
  selector: 'app-notification-content',
  templateUrl: './notification-content.component.html',
})
export class NotificationContentComponent
  extends UkFormComponent
  implements OnInit
{
  @Input()
  notificationRequest: NotificationRequestEnum;
  @Input()
  notificationDefinition: NotificationDefinition;
  @Input()
  newNotification: Notification;
  @Output()
  readonly notificationContent = new EventEmitter<NotificationContent>();
  @Output() readonly cancelEmitter = new EventEmitter();

  notificationTypeLabels = NotificationTypeLabels;
  NotificationType = NotificationType;
  NotificationRequestEnum = NotificationRequestEnum;
  infoText1 = `<details class="govuk-details" data-module="govuk-details">
  <summary class="govuk-details__summary">
    <span class="govuk-details__summary-text">
      Help with parameters and links
    </span>
  </summary>
  <div class="govuk-details__text">
    You can enter the parameters by coping the following codes in the text:
    <ul>
      <li>For first name, enter $&#123;user.firstName&#125;</li>
      <li>For last name, enter $&#123;user.lastName&#125;</li>
      <li>For account holder, enter $&#123;accountHolder.name&#125;</li>
      <li>For account ID, enter $&#123;accountId&#125;</li>
      <li>
        For installation Permit ID, enter $&#123;installation.permitId&#125;
      </li>
      <li>For installation name, enter $&#123;installation.name&#125;</li>
      <li>
        For aviation monitoring plan, enter $&#123;operator.monitoringPlan&#125;
      </li>
      <li>For aviation Operator ID, enter $&#123;operator.Id&#125;</li>
      <li>
        For Balance of compliance obligations or surrenders, enter
        $&#123;balance&#125;
      </li>
      <li>
        For current date obligations or surrenders, enter
        $&#123;currentDate&#125;
      </li>

      <li>For current year, enter $&#123;currentYear&#125;</li>
    </ul>
  </div>
</details>
`;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    this.notificationContent.emit({
      subject: this.formGroup.get('notificationSubject').value,
      content: this.formGroup.get('notificationContent').value,
    });
  }

  protected getFormModel(): any {
    let subjectValue = this.newNotification?.contentDetails?.subject
      ? this.newNotification?.contentDetails?.subject
      : this.notificationDefinition.shortText;
    if (this.notificationRequest == NotificationRequestEnum.CLONE) {
      subjectValue += ' Clone';
    }

    return {
      notificationSubject: [subjectValue, Validators.required],
      notificationContent: [
        this.newNotification?.contentDetails?.content
          ? this.newNotification?.contentDetails?.content
          : this.notificationDefinition.longText,
        Validators.required,
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      notificationSubject: {
        required: 'Add Subject/Title',
      },
      notificationContent: {
        required: 'Add Content',
      },
    };
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
