import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import { AuthoritySettingState } from '@authority-setting/reducer';

@Component({
  selector: 'app-set-authority-user-form',
  templateUrl: './set-authority-user-form.component.html',
})
export class SetAuthorityUserFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Output() readonly submitEnrolledUserId = new EventEmitter<string>();
  @Input() state: AuthoritySettingState;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  get uridControl(): UntypedFormControl {
    return this.formGroup.get('urid') as UntypedFormControl;
  }

  protected doSubmit() {
    this.submitEnrolledUserId.emit(this.uridControl.value);
  }

  protected getFormModel(): any {
    return {
      urid: [
        this.state.enrolledUser?.userId,
        [Validators.required, Validators.maxLength(256)],
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      urid: {
        required: 'Add User ID',
        maxLength: 'Enter a user id with less than 256 characters',
      },
    };
  }
}
