import {
  AfterContentInit,
  Component,
  EventEmitter,
  forwardRef,
  inject,
  Input,
  Output,
} from '@angular/core';
import {
  AbstractControl,
  ControlContainer,
  FormGroupDirective,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
  FormGroup,
  FormBuilder,
} from '@angular/forms';
import { PhoneNumberUtil, PhoneNumberType } from 'google-libphonenumber';
import { emptyProp } from '../../shared.util';
import { UkProtoFormCompositeComponent } from '../uk-proto-form-composite.component';
import { MobileNumberVerificationStatus, PhoneInfo } from './phone.model';
import { PhoneNumberValidationResult } from '@shared/form-controls/uk-select-phone/phone-number-validation-result.enum';

const phoneNumberUtil = PhoneNumberUtil.getInstance();

@Component({
  selector: 'app-uk-phone-select',
  templateUrl: './uk-select-phone.component.html',
  styleUrls: ['uk-select-phone.component.scss'],
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
  @Input() mustBeMobileNumber = false;

  @Output() readonly mobileNumberVerificationStatus =
    new EventEmitter<MobileNumberVerificationStatus>();

  private phoneNumberRegex = new RegExp('^[\\d \\-()]*$');
  private fb = inject(FormBuilder);

  protected getDefaultErrorMessageMap(): { [key: string]: string } {
    return {
      allFieldsRequired: 'Enter a phone number',
      invalidCodeForCountry:
        'Enter a valid phone format for the selected country',
      tooShort: 'The phone number is too short for your country code',
      tooLong: 'The phone number is too long for your country code',
      invalidLength: 'The phone number length is invalid',
      parseException: 'The string supplied did not seem to be a phone number',
      invalidChars: 'The phone number contains invalid characters',
      notMobile: 'Enter a valid mobile number',
    };
  }

  protected buildForm(): FormGroup {
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
    let regionCode;
    let phoneNumber;

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
      regionCode = phoneNumberUtil.getRegionCodeForCountryCode(countryCode);
      phoneNumber = phoneNumberUtil.parseAndKeepRawInput(phone, regionCode);
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
    if (this.mustBeMobileNumber) {
      const numberType = phoneNumberUtil.getNumberType(phoneNumber);

      if (
        numberType !== PhoneNumberType.MOBILE &&
        numberType !== PhoneNumberType.FIXED_LINE_OR_MOBILE &&
        numberType !== PhoneNumberType.UNKNOWN
      ) {
        return { notMobile: this.getDefaultErrorMessageMap().notMobile };
      }

      switch (numberType) {
        case PhoneNumberType.MOBILE:
          this.mobileNumberVerificationStatus.emit('MOBILE');
          break;
        case PhoneNumberType.FIXED_LINE_OR_MOBILE:
          this.mobileNumberVerificationStatus.emit('FIXED_LINE_OR_MOBILE');
          break;
        case PhoneNumberType.UNKNOWN:
          this.mobileNumberVerificationStatus.emit('UNKNOWN');
          break;
      }
    }
    return null;
  }
}
