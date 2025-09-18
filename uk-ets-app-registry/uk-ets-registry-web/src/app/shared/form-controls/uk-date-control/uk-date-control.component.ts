import { Component, forwardRef, inject, Input } from '@angular/core';
import {
  AbstractControl,
  ControlContainer,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  FormGroupDirective,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import { UkProtoFormCompositeComponent } from '../uk-proto-form-composite.component';
import {
  diffInYears,
  empty,
  getDayjs,
  ukDateInvalidAt,
} from '../../shared.util';

@Component({
  selector: 'app-uk-date-control',
  templateUrl: './uk-date-control.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => UkDateControlComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => UkDateControlComponent),
      multi: true,
    },
  ],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkDateControlComponent
  extends UkProtoFormCompositeComponent
  implements Validator
{
  @Input() label: string;
  @Input() hint: string;
  @Input() required = true;
  @Input() minAge: number;
  @Input() maxAge: number;

  stringNumberRegex = new RegExp('^[0-9]+$');
  private fb = inject(UntypedFormBuilder);

  protected getDefaultErrorMessageMap(): { [key: string]: string } {
    return {
      missingField: 'Enter a complete date',
      invalidInput: 'Enter a valid date',
      invalidDate: 'The date is invalid',
      invalidDay: 'Enter a valid day',
      invalidMonth: 'Enter a valid month',
    };
  }

  protected buildForm(): UntypedFormGroup {
    return this.fb.group({
      day: [''],
      month: [''],
      year: [''],
    });
  }

  get dayControl(): UntypedFormControl {
    return this.nestedForm.get('day') as UntypedFormControl;
  }

  get monthControl(): UntypedFormControl {
    return this.nestedForm.get('month') as UntypedFormControl;
  }

  get yearControl(): UntypedFormControl {
    return this.nestedForm.get('year') as UntypedFormControl;
  }

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.nestedForm.disable() : this.nestedForm.enable();
  }

  validate(control: AbstractControl): ValidationErrors {
    // clear previous errors
    Object.keys(this.nestedForm.controls).forEach((key) => {
      this.nestedForm.controls[key].setErrors(null);
    });
    // if the field is not set to required and all fields are empty return null
    // TODO: It would be better if this one was not an input but set in the
    //  reactive form with Validators.required
    if (
      !this.required &&
      empty(this.dayControl.value) &&
      empty(this.monthControl.value) &&
      empty(this.yearControl.value)
    ) {
      return null;
    }

    // check for missing fields
    Object.keys(this.nestedForm.controls).forEach((key) => {
      this.validateNotEmpty(
        this.nestedForm.controls[key] as UntypedFormControl
      );
    });

    if (this.formHasError('missingField')) {
      return { missingField: this.getDefaultErrorMessageMap().missingField };
    }

    // check for invalid numbers e.g 12/1S/1970
    Object.keys(this.nestedForm.controls).forEach((key) => {
      this.validateNumber(this.nestedForm.controls[key] as UntypedFormControl);
    });

    if (this.formHasError('invalidInput')) {
      return { invalidInput: this.getDefaultErrorMessageMap().invalidInput };
    }

    // check for invalid date e.g 44/02/2021
    const date = getDayjs({
      day: this.dayControl.value,
      month: this.monthControl.value,
      year: this.yearControl.value,
    });

    if (!date.isValid()) {
      const invalidAtField = ukDateInvalidAt({
        day: this.dayControl.value,
        month: this.monthControl.value,
        year: this.yearControl.value,
      });
      if (invalidAtField === 1) {
        this.monthControl.setErrors({
          invalidMonth: this.getDefaultErrorMessageMap().invalidMonth,
        });
        return { invalidMonth: this.getDefaultErrorMessageMap().invalidMonth };
      } else if (invalidAtField === 2) {
        this.dayControl.setErrors({
          invalidDay: this.getDefaultErrorMessageMap().invalidDay,
        });
        return { invalidDay: this.getDefaultErrorMessageMap().invalidDay };
      }
      return { invalidDate: true };
    }
    // check for min and max age
    if (this.minAge || this.maxAge) {
      const birthMoment = getDayjs(control.value);
      const age = diffInYears(birthMoment);
      if (this.minAge) {
        if (age < this.minAge) {
          this.markAllControlsWithError({ tooYoung: true });
          return { tooYoung: true };
        }
      }
      if (this.maxAge) {
        if (age > this.maxAge) {
          this.markAllControlsWithError({ tooOld: true });
          return { tooOld: true };
        }
      }
    }
    return null;
  }

  private validateNotEmpty(control: UntypedFormControl) {
    if (empty(control.value)) {
      control.setErrors({
        missingField: this.getDefaultErrorMessageMap().missingField,
      });
    }
  }

  private validateNumber(control: UntypedFormControl) {
    const isFieldNumber = this.stringNumberRegex.test(control.value);
    if (!isFieldNumber) {
      control.setErrors({
        invalidInput: this.getDefaultErrorMessageMap().invalidInput,
      });
    }
  }

  /**
   * check if any of the three controls has a particular error
   * @param errorKey the error key
   */
  private formHasError(errorKey): boolean {
    let result = false;
    Object.keys(this.nestedForm.controls).forEach((key) => {
      if (
        this.hasError(
          this.nestedForm.controls[key] as UntypedFormControl,
          errorKey
        )
      ) {
        result = true;
      }
    });
    return result;
  }

  private hasError(control: UntypedFormControl, errorKey: string): boolean {
    if (control.errors && control.errors[errorKey]) {
      return true;
    }
    return false;
  }

  private markAllControlsWithError(errorKey: ValidationErrors) {
    Object.keys(this.nestedForm.controls).forEach((key) => {
      this.nestedForm.controls[key].setErrors(errorKey);
    });
  }
}
