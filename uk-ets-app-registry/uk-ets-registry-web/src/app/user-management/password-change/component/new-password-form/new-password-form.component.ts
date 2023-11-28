import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormControl,
  Validators,
} from '@angular/forms';
import { PasswordChangeRequest } from '@password-change/model';
import { ErrorDetail } from '@shared/error-summary';
import { PasswordStrengthAsyncValidator } from '@shared/validation/password-strength-async-validator';
import { PasswordBlacklistAsyncValidator } from '@shared/validation/password-blacklist-async-validator';
import { UkRegistryValidators } from '@registry-web/shared/validation';
import { Configuration } from '@shared/configuration/configuration.interface';
import { getConfigurationValue } from '@shared/shared.util';

@Component({
  selector: 'app-new-password-form',
  templateUrl: './new-password-form.component.html',
})
export class NewPasswordFormComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() configuration: Configuration[];
  @Output()
  readonly submitPasswordChangeRequest = new EventEmitter<PasswordChangeRequest>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();

  showSpinner = false;
  passwordLabel = 'New password';
  newPasswordLabel = 'Confirm new password';
  passwordMinChars: number;

  constructor(
    protected formBuilder: UntypedFormBuilder,
    private passwordStrengthAsyncValidator: PasswordStrengthAsyncValidator,
    private passwordBlacklistAsyncValidator: PasswordBlacklistAsyncValidator
  ) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.passwordMinChars = getConfigurationValue(
      'password.policy.minimum-chars',
      this.configuration
    );
  }

  protected doSubmit() {
    this.formGroup.markAllAsTouched();
    if (this.formGroup.valid) {
      this.submitPasswordChangeRequest.emit({
        currentPassword: this.currentPasswordControl.value,
        newPassword: this.newPasswordControl.value.password,
        otp: this.otpControl.value,
      });
    } else {
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.formGroup
      );
      const errorDetails: ErrorDetail[] =
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        );
      this.errorDetails.emit(errorDetails);
    }
  }

  get newPasswordControl(): UntypedFormControl {
    return this.formGroup.get('passwordsGroup') as UntypedFormControl;
  }

  get currentPasswordControl(): UntypedFormControl {
    return this.formGroup.get('currentPassword') as UntypedFormControl;
  }

  get otpControl(): UntypedFormControl {
    return this.formGroup.get('otpCode') as UntypedFormControl;
  }

  protected getFormModel(): any {
    return {
      currentPassword: ['', [Validators.required]],
      passwordsGroup: [
        { password: '', pconfirm: '' },
        {
          asyncValidators: [
            this.passwordStrengthAsyncValidator.validate.bind(
              this.passwordStrengthAsyncValidator
            ),
            this.passwordBlacklistAsyncValidator.validate.bind(
              this.passwordBlacklistAsyncValidator
            ),
          ],
        },
      ],
      otpCode: ['', [Validators.required, Validators.pattern('\\d{6}')]],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      currentPassword: {
        required: 'Enter the current password',
        checkForSpaces: 'Current password must not contain any spaces',
      },
      passwordsGroup: {
        required: 'Password and confirmed password are required',
        minlength: `Password and confirm password should be ${this.passwordMinChars} characters or more`,
        passwordValidatorCheckAsync:
          'Checking password in progress. Submit the form again in a moment',
        equal: 'Password and confirmed password should match',
        strength: 'Enter a strong password.',
        blacklisted: 'Password is contained in deny list', //https://www.ncsc.gov.uk/blog-post/terminology-its-not-black-and-white
        serverError: 'There was a network error , try again later.',
        passwordCheckForSpaces: `${this.passwordLabel} must not contain any spaces`,
        confirmPassCheckForSpaces: `${this.newPasswordLabel} must not contain any spaces`,
      },
      otpCode: {
        required: 'Enter the 6-digit code shown in the authenticator app',
        pattern: 'OTP should consist of 6 digits',
      },
    };
  }

  showCurrentPasswordError(): boolean {
    return (
      !this.formGroup.get('currentPassword').valid &&
      !!this.formGroup.get('currentPassword').errors &&
      !!this.validationErrorMessage.currentPassword
    );
  }

  onSubmit() {
    if (this.formGroup.pending) {
      this.showSpinner = true;
      this.formGroup.statusChanges.subscribe(() => {
        this.showSpinner = false;
        super.onSubmit();
      });
    } else {
      super.onSubmit();
    }
  }
}
