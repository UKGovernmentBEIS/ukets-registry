import { Input } from '@angular/core';
import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import {
  UntypedFormBuilder,
  Validators,
  UntypedFormControl,
} from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';

@Component({
  selector: 'app-submit-email',
  templateUrl: './submit-email.component.html',
  styles: [],
})
export class SubmitEmailComponent extends UkFormComponent implements OnInit {
  @Output() readonly emailAddress = new EventEmitter<string>();
  @Input()
  linkExpiration: number;
  @Input()
  cookiesAccepted: boolean;

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
