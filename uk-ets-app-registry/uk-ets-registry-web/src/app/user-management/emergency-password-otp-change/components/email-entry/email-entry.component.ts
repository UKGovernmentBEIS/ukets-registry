import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-email-entry',
  templateUrl: './email-entry.component.html',
})
export class EmailEntryComponent extends UkFormComponent implements OnInit {
  @Input() emailExpiration: number;
  @Output() readonly submitEmail = new EventEmitter<string>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
  }

  protected doSubmit() {
    this.submitEmail.emit(this.formGroup.value.email);
  }

  protected getFormModel(): any {
    return {
      email: ['', [Validators.required, Validators.email]],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      email: {
        required: 'Enter your email address',
        email:
          'Enter an email address in the correct format, like name@example.com',
      },
    };
  }
}
