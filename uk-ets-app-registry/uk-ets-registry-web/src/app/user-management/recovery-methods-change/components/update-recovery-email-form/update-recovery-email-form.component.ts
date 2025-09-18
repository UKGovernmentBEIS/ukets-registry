import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { RecoveryMethodsChangeState } from '../../store/recovery-methods-change.reducer';
import { FormControl, Validators } from '@angular/forms';
import { UkRegistryValidators } from '@shared/validation';
import { UpdateRecoveryEmailRequest } from '../../recovery-methods-change.models';
import { UkProtoFormTextComponent } from '@registry-web/shared/form-controls/uk-proto-form-controls';
import { ErrorDetail } from '@registry-web/shared/error-summary';

@Component({
  selector: 'app-update-recovery-email-form',
  templateUrl: './update-recovery-email-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UpdateRecoveryEmailFormComponent
  extends UkFormComponent
  implements OnInit
{
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

  @Output() submitRequest = new EventEmitter<UpdateRecoveryEmailRequest>();

  @ViewChild('otpCodeInput') otpCodeInput: UkProtoFormTextComponent;

  newEmailControl: FormControl<string>;
  otpCodeControl: FormControl<string>;

  ngOnInit(): void {
    const newEmailValidators = [
      Validators.required,
      Validators.email,
      Validators.maxLength(256),
    ];

    if (this.state.recoveryEmailAddress) {
      newEmailValidators.push(
        UkRegistryValidators.notPermittedValue(this.state.recoveryEmailAddress)
      );
    }

    this.newEmailControl = new FormControl(
      this.state.newRecoveryEmailAddress ||
        this.state.recoveryEmailAddress ||
        '',
      newEmailValidators
    );

    this.otpCodeControl = new FormControl('', Validators.required);

    super.ngOnInit();
  }

  protected doSubmit() {
    this.submitRequest.emit({
      newRecoveryEmailAddress: this.newEmailControl.value,
      otpCode: this.otpCodeControl.value,
    });
  }

  protected getFormModel(): any {
    return {
      email: this.newEmailControl,
      otpCode: this.otpCodeControl,
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      email: {
        required: 'Enter the new recovery email address',
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Enter an email address with less than 256 characters',
        notPermittedValue:
          'Enter an email address different from your current recovery email address',
      },
      otpCode: {
        required: 'Enter the 6-digit code shown in the authenticator app',
      },
    };
  }
}
