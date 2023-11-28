import {
  AfterContentInit,
  Component,
  forwardRef,
  Injector,
  Input,
} from '@angular/core';
import {
  AbstractControl,
  ControlContainer,
  UntypedFormBuilder,
  UntypedFormGroup,
  FormGroupDirective,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import { PhoneNumberUtil } from 'google-libphonenumber';
import { emptyProp } from '../../shared.util';
import { UkProtoFormCompositeComponent } from '../uk-proto-form-composite.component';
import { PhoneInfo } from './phone.model';
import { PhoneNumberValidationResult } from '@shared/form-controls/uk-select-phone/phone-number-validation-result.enum';

const phoneNumberUtil = PhoneNumberUtil.getInstance();

@Component({
  selector: 'app-uk-phone-select',
  templateUrl: './uk-select-phone.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => UKSelectPhoneComponent),
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: forwardRef(() => UKSelectPhoneComponent),
      multi: true,
    },
  ],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UKSelectPhoneComponent
  extends UkProtoFormCompositeComponent
  implements Validator, AfterContentInit
{
  @Input() countryCodes: any[];
  @Input() countryCodeLabel = 'Country code';
  @Input() phoneNumberLabel = 'Phone number';

  phoneNumberRegex = new RegExp('^[\\d \\-()]*$');

  constructor(
    protected parentF: FormGroupDirective,
    private fb: UntypedFormBuilder,
    protected injector: Injector
  ) {
    super(parentF, injector);
  }

  protected getDefaultErrorMessageMap(): { [key: string]: string } {
    return {
      allFieldsRequired: 'Enter a phone number',
      invalidCodeForCountry:
        'Please enter a valid phone format for the selected country',
      tooShort: 'The phone number is too short for your country code',
      tooLong: 'The phone number is too long for your country code',
      invalidLength: 'The phone number length is invalid',
      parseException: 'The string supplied did not seem to be a phone number',
      invalidChars: 'The phone number contains invalid characters',
    };
  }

  protected buildForm(): UntypedFormGroup {
    return this.fb.group({
      countryCode: [''],
      phoneNumber: [''],
    });
  }

  @Input()
  set phoneInfo(value: PhoneInfo) {
    if (value && this.nestedForm) {
      this.nestedForm.patchValue(value);
    }
  }

  ngAfterContentInit(): void {
    super.ngAfterContentInit();
    if (!this.nestedForm.get('countryCode').value) {
      this.nestedForm.patchValue({ countryCode: 'UK (44)' });
      this.nestedForm.get('countryCode').updateValueAndValidity();
    }
  }

  setDisabledState?(isDisabled: boolean): void {
    isDisabled ? this.nestedForm.disable() : this.nestedForm.enable();
  }

  /** Implements Validator */
  validate(control: AbstractControl): ValidationErrors {
    if (emptyProp(control.value)) {
      return null;
    }
    let validationResult;
    let validNumber = false;
    let countryCode = control.value.countryCode;
    const phone = control.value.phoneNumber;

    /* As libphonenumber library treats some non-digit characters as digits,
     * we have to check ourselves for invalid characters. */
    const isPhoneNumber = this.phoneNumberRegex.test(phone);
    if (!isPhoneNumber) {
      return { invalidChars: this.getDefaultErrorMessageMap().invalidChars };
    }

    if (countryCode) {
      countryCode = countryCode
        .toString()
        .substring(countryCode.indexOf('(') + 1, countryCode.indexOf(')'));
    }
    try {
      const regionCode =
        phoneNumberUtil.getRegionCodeForCountryCode(countryCode);
      const phoneNumber = phoneNumberUtil.parseAndKeepRawInput(
        phone,
        regionCode
      );
      validNumber = phoneNumberUtil.isValidNumber(phoneNumber);
      validationResult =
        phoneNumberUtil.isPossibleNumberWithReason(phoneNumber);
    } catch (e) {
      return {
        parseException: this.getDefaultErrorMessageMap().parseException,
      };
    }
    if (!validNumber) {
      switch (validationResult) {
        case PhoneNumberValidationResult.TOO_SHORT:
          return { tooShort: this.getDefaultErrorMessageMap().tooShort };
        case PhoneNumberValidationResult.TOO_LONG:
          return { tooLong: this.getDefaultErrorMessageMap().tooLong };
        case PhoneNumberValidationResult.INVALID_LENGTH:
          return {
            invalidLength: this.getDefaultErrorMessageMap().invalidLength,
          };
        default:
          return {
            invalidCodeForCountry:
              this.getDefaultErrorMessageMap().invalidCodeForCountry,
          };
      }
    }
    return null;
  }
}
