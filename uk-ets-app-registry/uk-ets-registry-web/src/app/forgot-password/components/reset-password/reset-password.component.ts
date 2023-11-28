import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { PasswordStrengthAsyncValidator } from '@shared/validation/password-strength-async-validator';
import { PasswordBlacklistAsyncValidator } from '@shared/validation/password-blacklist-async-validator';
import { getConfigurationValue } from '@shared/shared.util';
import { Configuration } from '@shared/configuration/configuration.interface';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styles: [],
})
export class ResetPasswordComponent extends UkFormComponent implements OnInit {
  @Input() configuration: Configuration[];
  @Output() readonly resetPasswordRequest = new EventEmitter<{
    otp: string;
    newPasswd: string;
  }>();

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

  ngOnInit() {
    super.ngOnInit();
    this.passwordMinChars = getConfigurationValue(
      'password.policy.minimum-chars',
      this.configuration
    );
  }

  protected getFormModel(): any {
    return {
      otp: ['', [Validators.required, Validators.pattern('\\d{6}')]],
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
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      otp: {
        required: 'OTP is required.',
        pattern: 'OTP should consist of 6 digits',
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
    };
  }

  protected doSubmit() {
    this.resetPasswordRequest.emit({
      otp: this.formGroup.get('otp').value,
      newPasswd: this.formGroup.get('passwordsGroup').value.pconfirm,
    });
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
