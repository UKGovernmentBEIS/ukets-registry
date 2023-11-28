import {
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkProtoFormTextComponent } from '@registry-web/shared/form-controls/uk-proto-form-controls';
import { ErrorDetail } from '@shared/error-summary';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { FileBase } from '@shared/model/file';

@Component({
  selector: 'app-check-request-and-submit',
  templateUrl: './check-request-and-submit.component.html',
  styles: [],
})
export class CheckRequestAndSubmitComponent extends UkFormComponent {
  @Input()
  fileHeader: FileBase;

  @Output() readonly navigateBackEmitter = new EventEmitter<string>();

  @Output() readonly emissionsTableSubmitted = new EventEmitter<string>();

  @ViewChild('otpTextControl')
  otpTextControl: UkProtoFormTextComponent;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  @Input()
  set serverSideErrorDetails(details: ErrorDetail[]) {
    details.forEach((e) => {
      this.formGroup.markAllAsTouched();
      this.formGroup.get(e.componentId).setErrors({
        invalid: true,
      });
      this.otpTextControl.validationErrorMessage = e.errorMessage;
    });
  }

  navigateBack(value: string): void {
    this.navigateBackEmitter.emit(value);
  }

  protected doSubmit(): void {
    this.emissionsTableSubmitted.emit(this.formGroup.get('otpCode').value);
  }

  protected getFormModel(): any {
    return {
      otpCode: ['', Validators.required],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      otpCode: {
        required: 'Enter the 6-digit code shown in the authenticator app',
      },
    };
  }
}
