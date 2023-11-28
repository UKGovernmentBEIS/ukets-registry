import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AbstractControl,
  UntypedFormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import {
  Notification,
  NotificationScheduledDate,
  NotificationType,
  NotificationTypeLabels,
} from '@notifications/notifications-wizard/model';
import { UkRegistryValidators } from '@shared/validation';
import { NotificationRequestEnum } from '@notifications/notifications-wizard/model/notification-request.enum';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import {
  NOW,
  handleCustomScheduledTimeOption,
  copy,
  timeNow,
} from '@registry-web/shared/shared.util';

@Component({
  selector: 'app-notifications-scheduled-date',
  templateUrl: './notification-scheduled-date.component.html',
})
export class NotificationScheduledDateComponent
  extends UkFormComponent
  implements OnInit, AfterViewInit
{
  @Input()
  notificationRequest: NotificationRequestEnum;
  @Input()
  timeOptions: Option[];
  @Input()
  timeOptionsWithNow: Option[];
  @Input()
  newNotification: Notification;
  @Output()
  readonly notificationScheduledDate = new EventEmitter<NotificationScheduledDate>();
  @Output() readonly cancelEmitter = new EventEmitter();

  isRecurrenceEnabled: boolean;
  notificationTypeLabels = NotificationTypeLabels;
  NotificationTypesEnum = NotificationType;
  NotificationRequestEnum = NotificationRequestEnum;
  addHocExpirationOption: string;

  scheduledTimeOptions = [
    {
      label: '',
      value: null,
    },
  ];

  expirationTimeOptions = [
    {
      label: '',
      value: null,
    },
  ];

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    this.scheduledTimeOptions = handleCustomScheduledTimeOption(
      this.newNotification,
      copy(this.timeOptionsWithNow),
      true
    );

    this.expirationTimeOptions = this.timeOptions;
    this.addHocExpirationOption =
      this.newNotification?.activationDetails?.hasExpirationDateSection &&
      this.notificationRequest !== NotificationRequestEnum.CLONE
        ? 'WITH_EXPIRATION_DATE'
        : 'NO_EXPIRATION_DATE';
    super.ngOnInit();
    this.isRecurrenceEnabled =
      this.newNotification?.activationDetails?.hasRecurrence;
  }

  onContinue() {
    // when UkProtoFormDateComponent got replaced by UkProtoFormDatePickerComponent
    // the datepicker field in 'Expires by' section remained invalid (ng-invalid class)
    // when the 'No expiration date' radio button was clicked
    if (
      this.formGroup.get('expirationDateDetails.expirationAddHocRadio') !=
        null &&
      this.formGroup.get('expirationDateDetails.expirationAddHocRadio').value ==
        'NO_EXPIRATION_DATE'
    ) {
      this.formGroup.get('expirationDateDetails.expirationDate').reset();
    }

    // same for expirationDateForRecur field
    if (
      this.formGroup.get('recurrenceDetails.hasRecurrence') != null &&
      (!this.formGroup.get('recurrenceDetails.hasRecurrence').value ||
        this.formGroup.get('recurrenceDetails.expirationDateForRecur').value ==
          '')
    ) {
      this.formGroup.get('recurrenceDetails.expirationDateForRecur').reset();
    }
    this.onSubmit();
  }

  ngAfterViewInit(): void {
    this.initializeTimeComponents();
  }

  private initializeTimeComponents() {
    if (this.notificationRequest == NotificationRequestEnum.CLONE) return;

    if (this.newNotification?.activationDetails?.scheduledTime) {
      this.formGroup
        .get('scheduledTime')
        .patchValue(this.newNotification.activationDetails.scheduledTime);
    }

    if (
      this.newNotification?.activationDetails?.expirationTime &&
      this.formGroup.get('expirationDateDetails.expirationTime')
    ) {
      this.formGroup
        .get('expirationDateDetails.expirationTime')
        .patchValue(this.newNotification.activationDetails.expirationTime);
    }
  }

  protected doSubmit() {
    this.notificationScheduledDate.emit({
      scheduledDate: this.formGroup.get('scheduledDate').value,
      scheduledTime: this.formGroup.get('scheduledTime').value,
      scheduledTimeNow: this.formGroup.get('scheduledTime').value === NOW,
      hasRecurrence: this.formGroup.get('recurrenceDetails.hasRecurrence')
        ?.value,
      recurrenceDays: this.formGroup.get('recurrenceDetails.hasRecurrence')
        ?.value
        ? Number(this.formGroup.get('recurrenceDetails.recurDays').value)
        : null,
      hasExpirationDateSection:
        this.formGroup.get('expirationDateDetails.expirationAddHocRadio')
          ?.value === 'WITH_EXPIRATION_DATE',
      expirationDate: this.formGroup.get('expirationDateDetails')
        ? this.formGroup.get('expirationDateDetails.expirationDate')?.value
        : this.formGroup.get('recurrenceDetails.hasRecurrence')?.value
        ? this.formGroup.get('recurrenceDetails.expirationDateForRecur').value
        : null,
      expirationTime: this.formGroup.get('expirationDateDetails')
        ? this.formGroup.get('expirationDateDetails.expirationTime')?.value
        : null,
    });
  }

  protected getFormModel(): any {
    const common = {
      scheduledDate: [
        this.newNotification?.activationDetails?.scheduledDate,
        Validators.required,
      ],
      scheduledTime: [null, Validators.required],
    };
    if (this.newNotification?.type != this.NotificationTypesEnum.AD_HOC) {
      return {
        ...common,
        recurrenceDetails: this.formBuilder.group(
          {
            hasRecurrence: [
              this.newNotification?.activationDetails?.hasRecurrence,
            ],
            expirationDateForRecur: [
              this.newNotification?.activationDetails?.expirationDate,
            ],
            recurDays: [
              this.newNotification?.activationDetails?.recurrenceDays,
              [UkRegistryValidators.numberShouldBeBetween(1, 30)],
            ],
          },
          { validators: this.recurrenceValidator() }
        ),
      };
    } else if (this.notificationRequest == NotificationRequestEnum.CLONE) {
      return {
        scheduledDate: [null, Validators.required],
        scheduledTime: [null, Validators.required],
        expirationDateDetails: this.formBuilder.group(
          {
            expirationTime: [null],
            expirationDate: [null],
            expirationAddHocRadio: ['NO_EXPIRATION_DATE'],
          },
          {
            validators: this.addHocExpirationValidator(),
          }
        ),
      };
    } else {
      return {
        ...common,
        expirationDateDetails: this.formBuilder.group(
          {
            expirationTime: [null],
            expirationDate: [
              this.newNotification?.activationDetails?.expirationDate,
            ],
            expirationAddHocRadio: [
              this.newNotification?.activationDetails?.hasExpirationDateSection
                ? 'WITH_EXPIRATION_DATE'
                : 'NO_EXPIRATION_DATE',
            ],
          },
          {
            validators: this.addHocExpirationValidator(),
          }
        ),
      };
    }
  }

  selectAddHocExpirationDetailsSection(type: string) {
    this.addHocExpirationOption = type;
    // patchValue instead of reset is used because the validator checks for empty values
    // while reset sets the values to null.
    this.formGroup.get('expirationDateDetails.expirationDate').patchValue('');
    this.formGroup.get('expirationDateDetails.expirationTime').patchValue('');
  }

  recurrenceValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.get('hasRecurrence').value) {
        const recurDays = control.get('recurDays').value;
        if (recurDays === null || recurDays === '') {
          return { isRequired: true };
        }
      }
      return null;
    };
  }

  addHocExpirationValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (this.addHocExpirationOption === undefined) {
        return { bothEmpty: true };
      }

      if (this.addHocExpirationOption === 'NO_EXPIRATION_DATE') {
        return null;
      }

      if (this.addHocExpirationOption === 'WITH_EXPIRATION_DATE') {
        const expirationDate = control.get('expirationDate').value;
        if (control.get('expirationDate').errors?.pattern) {
          return { expiredDatePattern: true };
        }
        if (expirationDate === '' || expirationDate?.trim() === '') {
          return { expiredDateRequired: true };
        }
        const expirationTime = control.get('expirationTime').value;
        if (expirationTime == null) {
          return { expiredTimeRequired: true };
        }
        if (expirationTime === '' || expirationTime?.trim() === '') {
          return { expiredTimeRequired: true };
        }
        if (
          this.formGroup.get('scheduledDate').value &&
          this.formGroup.get('scheduledTime').value
        ) {
          let scheduledTime = this.formGroup.get('scheduledTime').value;
          if (this.formGroup.get('scheduledTime').value === NOW) {
            scheduledTime = timeNow();
          }

          const scheduledDateTime =
            this.formGroup.get('scheduledDate').value + ' ' + scheduledTime;
          const expirationDateTime = expirationDate + ' ' + expirationTime;
          if (expirationDateTime <= scheduledDateTime) {
            return { expiredDateTimeEarlier: true };
          }
        }
      }
      return null;
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      scheduledDate: {
        required: 'Select a Scheduled date',
        pattern:
          'Invalid Scheduled date: Enter a Scheduled date in the correct format',
        ngbDate: ' ',
      },
      scheduledTime: {
        required: 'Select a Scheduled time',
      },
      recurrenceDetails: {
        isRequired: 'Recur every field is mandatory',
      },
      hasRecurrence: {
        required: 'The field is mandatory for this type of Notification.',
      },
      recurDays: {
        invalidRange: 'Recur Days should be from 1 to 30',
      },
      expirationDateForRecur: {
        ngbDate: ' ',
        pattern: 'The date you have entered is invalid',
      },
      expirationDateDetails: {
        bothEmpty: 'Select an option in expiration date section',
        expiredDateRequired: 'Enter the expiration date',
        expiredDatePattern: 'The date you have entered is invalid',
        expiredTimeRequired: 'Enter the expiration time',
        expiredDateTimeEarlier:
          'Expiration date time cannot be set earlier or equal than the scheduled date time',
      },
    };
  }

  onCancel() {
    this.cancelEmitter.emit();
  }

  toggle(isChecked) {
    this.isRecurrenceEnabled = isChecked;
  }
}
