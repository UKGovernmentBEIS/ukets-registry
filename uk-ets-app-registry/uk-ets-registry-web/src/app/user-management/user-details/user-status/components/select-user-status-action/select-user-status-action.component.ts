import { Component, OnInit, EventEmitter, Input, Output } from '@angular/core';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import {
  AbstractControl,
  UntypedFormBuilder,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  UserStatusActionOption,
  UserStatusActionState,
} from '@user-management/model';

@Component({
  selector: 'app-select-user-status-action',
  templateUrl: './select-user-status-action.component.html',
  styles: [],
})
export class SelectUserStatusActionComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() allowedUserStatusActions: UserStatusActionOption[];
  @Input() userStatusAction: UserStatusActionState;
  @Input() userSuspendedByTheSystem: boolean;
  @Output()
  readonly selectedUserStatusAction = new EventEmitter<UserStatusActionState>();
  @Output() readonly cancelUserStatusAction = new EventEmitter();

  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Select Action',
      radioGroupHeadingCaption: 'Change the user status',
      radioGroupHint: 'Select one option',
      key: 'userStatusAction',
      options: this.getAllowedUserStatuses(),
    };
  }

  onContinue() {
    this.onSubmit();
  }

  protected doSubmit() {
    const selectedOption = this.allowedUserStatusActions.find(
      (option) => option.value === this.formGroup.get('userStatusAction').value
    );
    this.selectedUserStatusAction.emit({
      value: selectedOption.value,
      label: selectedOption.label,
      newStatus: selectedOption.newStatus,
      message: selectedOption.message,
    });
  }

  protected getFormModel(): any {
    return {
      userStatusAction: [
        this.userStatusAction ? this.userStatusAction.value : null,
        Validators.compose([
          Validators.required,
          this.userSuspendedByTheSystemValidator(),
        ]),
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      userStatusAction: {
        required: 'You must select an action',
        suspendedByTheSystem:
          'You cannot restore a user manually who is suspended by the system. There is request pending approval.',
      },
    };
  }

  onCancel() {
    this.cancelUserStatusAction.emit();
  }

  getAllowedUserStatuses() {
    if (this.allowedUserStatusActions) {
      return this.allowedUserStatusActions.map((userStatusActionOption) => {
        return {
          label: userStatusActionOption.label,
          value: userStatusActionOption.value,
          enabled: userStatusActionOption.enabled,
        };
      });
    }
  }

  userSuspendedByTheSystemValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      if (control.value === 'RESTORE' && this.userSuspendedByTheSystem) {
        return { suspendedByTheSystem: true };
      } else {
        null;
      }
    };
  }
}
