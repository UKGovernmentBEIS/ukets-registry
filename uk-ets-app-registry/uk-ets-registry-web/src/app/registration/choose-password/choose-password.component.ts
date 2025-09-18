import {
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import { PasswordStrengthAsyncValidator } from '@shared/validation/password-strength-async-validator';
import { PasswordBlacklistAsyncValidator } from '@shared/validation/password-blacklist-async-validator';
import { Configuration } from '@shared/configuration/configuration.interface';
import { getConfigurationValue } from '@shared/shared.util';

@Component({
  selector: 'app-choose-password',
  templateUrl: './choose-password.component.html',
  providers: [],
})
export class ChoosePasswordComponent extends UkFormComponent implements OnInit {
  @Input() configuration: Configuration[];
  @Output() readonly choosePasswordRequest = new EventEmitter<{
    newPasswd: string;
  }>();

  showSpinner = false;
  passwordMinChars: number;

  constructor(
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
      passwordsGroup: {
        required: 'Password and confirmed password are required',
        minlength: `Password and confirm password should be ${this.passwordMinChars} characters or more`,
        passwordValidatorCheckAsync:
          'Checking password in progress. Submit the form again in a moment',
        equal: 'Password and confirmed password should match',
        strength: 'Enter a strong password.',
        blacklisted: 'Password is contained in deny list.', //https://www.ncsc.gov.uk/blog-post/terminology-its-not-black-and-white
        serverError: 'There was a network error , try again later.',
        passwordCheckForSpaces: 'Password must not contain any spaces',
        confirmPassCheckForSpaces:
          'Confirm password must not contain any spaces',
      },
    };
  }

  doSubmit() {
    this.choosePasswordRequest.emit({
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
