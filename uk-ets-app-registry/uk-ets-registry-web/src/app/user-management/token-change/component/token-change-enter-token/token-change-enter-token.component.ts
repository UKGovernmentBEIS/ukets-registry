import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

@Component({
  selector: 'app-token-change-enter-token',
  templateUrl: './token-change-enter-token.component.html',
})
export class TokenChangeEnterTokenComponent
  extends UkFormComponent
  implements OnInit
{
  @Output() readonly submitOtpCode = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected doSubmit() {
    this.submitOtpCode.emit(this.otpControl.value);
  }

  get otpControl(): UntypedFormControl {
    return this.formGroup.get('otpCode') as UntypedFormControl;
  }

  protected getFormModel(): any {
    return {
      otpCode: ['', Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      otpCode: {
        required: 'Enter a code',
      },
    };
  }
}
