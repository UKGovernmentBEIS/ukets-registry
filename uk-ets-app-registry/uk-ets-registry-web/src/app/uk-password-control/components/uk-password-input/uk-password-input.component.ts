/* eslint-disable @typescript-eslint/no-empty-function */
import { LiveAnnouncer } from '@angular/cdk/a11y';
import { Component, Input } from '@angular/core';
import {
  AbstractControl,
  ControlValueAccessor,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  Validators,
} from '@angular/forms';
import { UkRegistryValidators } from '@shared/validation';

@Component({
  selector: 'app-uk-password-input',
  templateUrl: './uk-password-input.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: UkPasswordInputComponent,
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: UkPasswordInputComponent,
      multi: true,
    },
  ],
  styleUrls: ['./uk-password-input.component.scss'],
})
export class UkPasswordInputComponent
  implements ControlValueAccessor, Validator
{
  @Input() validationErrorMessage: string;
  @Input() _parentFormGroupName: string;
  @Input() passwordLabel = 'Password';
  @Input() confirmPasswordLabel = 'Confirm password';
  passwordModel: UntypedFormGroup;

  strength: number;
  labelId: string;
  private onTouched = () => {};

  constructor(
    private fb: UntypedFormBuilder,
    private announcer: LiveAnnouncer
  ) {
    this.passwordModel = this.fb.group(
      {
        password: new UntypedFormControl('', {
          validators: [Validators.required, Validators.minLength(8)],
        }),
        pconfirm: new UntypedFormControl('', {
          validators: [Validators.required, Validators.minLength(8)],
        }),
      },
      {
        validators: [UkRegistryValidators.equalValidator],
      }
    );
  }

  @Input() set parentFormGroupName(value: string) {
    if (value !== undefined) {
      this._parentFormGroupName = value;
      this.labelId = this._parentFormGroupName
        ? this._parentFormGroupName + '-label'
        : 'password-label';
    }
  }

  get parentFormGroupName(): string {
    return this._parentFormGroupName;
  }

  get password(): AbstractControl {
    return this.passwordModel.get('password');
  }

  registerOnChange(fn: any): void {
    this.passwordModel.valueChanges.subscribe(fn);
  }
  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  writeValue(obj: any): void {
    if (obj) {
      this.passwordModel.setValue(obj, { emitEvent: false });
    }
  }

  validate(c: AbstractControl): ValidationErrors | null {
    if (!this.passwordModel.get('password').valid) {
      // Return the original angular ValidationError object
      return this.passwordModel.get('password').errors;
    }
    if (!this.passwordModel.get('pconfirm').valid) {
      // Return the original angular ValidationError object
      return this.passwordModel.get('pconfirm').errors;
    }
    if (!this.passwordModel.valid) {
      // Return our group ValidationError object
      return this.passwordModel.errors;
    }
  }

  /**
   * Called when the password strength changes
   * @param $event The listener event
   */
  onStrengthChange($event: number): void {
    this.strength = $event;
  }

  announcePasswordStrength(): void {
    const noStrongPasswordAnnouncement =
      'Only strong passwords are allowed. Please select another password.';
    const strongPasswordAnnouncement = 'The password is strong.';
    if (this.strength < 3) {
      this.announcer.announce(noStrongPasswordAnnouncement, 'polite');
    } else {
      this.announcer.announce(strongPasswordAnnouncement, 'polite');
    }
  }

  showError(): boolean {
    return (
      this.showGroupError() ||
      this.showPasswordError() ||
      this.showPconfirmError()
    );
  }

  showPasswordError(): boolean {
    return (
      !this.passwordModel.get('password').valid &&
      !!this.passwordModel.get('password').errors &&
      !!this.validationErrorMessage
    );
  }

  showPconfirmError(): boolean {
    return (
      !this.passwordModel.get('pconfirm').valid &&
      !!this.passwordModel.get('pconfirm').errors &&
      !!this.validationErrorMessage
    );
  }

  showGroupError(): boolean {
    return (
      !this.passwordModel.valid &&
      !!this.passwordModel.errors &&
      !!this.validationErrorMessage
    );
  }
}
