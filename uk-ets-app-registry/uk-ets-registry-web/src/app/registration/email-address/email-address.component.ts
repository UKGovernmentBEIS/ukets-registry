import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

@Component({
  selector: 'app-email-address',
  templateUrl: './email-address.component.html',
})
export class EmailAddressComponent extends UkFormComponent implements OnInit {
  @Output() readonly emailAddress = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  protected getFormModel(): any {
    return {
      email: ['', Validators.required],
    };
  }

  get emailControl(): UntypedFormControl {
    return this.formGroup.get('email') as UntypedFormControl;
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      email: {
        required: 'Email address is required.',
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Email address should not exceed 256 characters',
      },
    };
  }

  protected doSubmit() {
    this.emailAddress.emit(this.emailControl.value);
  }
}
