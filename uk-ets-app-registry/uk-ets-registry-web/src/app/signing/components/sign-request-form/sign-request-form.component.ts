import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, UntypedFormControl } from '@angular/forms';

@Component({
  selector: 'app-sign-request-form',
  templateUrl: './sign-request-form.component.html',
  styleUrls: ['./sign-request-form.component.scss'],
})
export class SignRequestFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Output() readonly otpCode = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.otpCodeControl().valueChanges.subscribe(() => {
      this.otpCode.emit(this.otpCodeControl().value);
    });
  }

  protected getFormModel() {
    return {
      otpCode: ['', { updateOn: 'change' }],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {};
  }

  otpCodeControl(): UntypedFormControl {
    return this.formGroup.get('otpCode') as UntypedFormControl;
  }

  protected doSubmit() {
    throw new Error('Method not needed.');
  }
}
