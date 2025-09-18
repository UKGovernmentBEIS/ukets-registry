import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  Output,
  ViewChild,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { RecoveryMethodsChangeState } from '../../store/recovery-methods-change.reducer';
import { FormControl, Validators } from '@angular/forms';
import { RemoveRecoveryEmailRequest } from '../../recovery-methods-change.models';
import { ErrorDetail } from '@registry-web/shared/error-summary';
import { UkProtoFormTextComponent } from '@registry-web/shared/form-controls/uk-proto-form-controls';

@Component({
  selector: 'app-remove-recovery-email-form',
  templateUrl: './remove-recovery-email-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RemoveRecoveryEmailFormComponent extends UkFormComponent {
  @Input() state: RecoveryMethodsChangeState;
  @Input() set serverSideErrorDetails(details: ErrorDetail[]) {
    details.forEach((e) => {
      this.formGroup.markAllAsTouched();
      this.formGroup.get(e.componentId)?.setErrors({ invalid: true });

      if (e.componentId === 'otpCode') {
        this.otpCodeInput.validationErrorMessage = e.errorMessage;
      }
    });
  }

  @Output() submitRequest = new EventEmitter<RemoveRecoveryEmailRequest>();

  @ViewChild('otpCodeInput') otpCodeInput: UkProtoFormTextComponent;

  otpCodeControl = new FormControl('', Validators.required);

  protected doSubmit() {
    this.submitRequest.emit({ otpCode: this.otpCodeControl.value });
  }

  protected getFormModel(): any {
    return { otpCode: this.otpCodeControl };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      otpCode: {
        required: 'Enter the 6-digit code shown in the authenticator app',
      },
    };
  }
}
