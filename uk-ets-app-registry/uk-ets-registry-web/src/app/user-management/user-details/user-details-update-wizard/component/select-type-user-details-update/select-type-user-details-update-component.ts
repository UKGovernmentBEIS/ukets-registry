import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  FormRadioGroupInfo,
  FormRadioOption,
} from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import {
  UpdateUserDetailsRequestTypeMap,
  UserUpdateDetailsType,
} from '@user-update/model';

@Component({
  selector: 'app-select-type-user-details-update',
  templateUrl: './select-type-user-details-update-component.html',
})
export class SelectTypeUserDetailsUpdateComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() updateType: UserUpdateDetailsType;
  @Input() isMyProfilePage: boolean;
  @Output() readonly cancelEmitter = new EventEmitter();
  @Output()
  readonly selectUpdateType = new EventEmitter<UserUpdateDetailsType>();

  updateTypes: FormRadioOption[];
  formRadioGroupInfo: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: 'Select type of update',
      radioGroupHeadingCaption: 'Request to update the user details',
      radioGroupHint: '',
      key: 'userDetailsType',
      options: this.getOptions(),
    };
  }

  private getOptions(): FormRadioOption[] {
    const options = [];
    Object.keys(UpdateUserDetailsRequestTypeMap).forEach((key) => {
      const value = UpdateUserDetailsRequestTypeMap[key];
      options.push({
        label: this.isMyProfilePage ? value.userLabel : value.adminLabel,
        value: key,
        enabled: value.enabled,
        hint: this.isMyProfilePage ? value.userHint : value.adminHint,
      });
    });
    return options;
  }

  protected doSubmit() {
    this.selectUpdateType.emit(this.formGroup.get('userDetailsType').value);
  }

  protected getFormModel(): any {
    return {
      userDetailsType: [this.updateType, Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      userDetailsType: {
        required: 'Select the type of update',
      },
    };
  }

  onContinue() {
    this.onSubmit();
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
