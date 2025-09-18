import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { Validators } from '@angular/forms';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { IUser, User } from '@shared/user';
import { UkRegistryValidators } from '@shared/validation/uk-registry.validators';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import {
  MobileNumberVerificationStatus,
  PhoneInfo,
} from '@shared/form-controls/uk-select-phone';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { CountryCodeModel } from '@shared/countries/country-code.model';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';

@Component({
  selector: 'app-work-details-input',
  templateUrl: './work-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WorkDetailsComponent
  extends UkFormComponent
  implements OnInit, AfterViewInit
{
  @Input() caption: string;
  @Input() heading: string;
  @Input() isRequestUpdateProcess = false;
  @Input() isMyProfilePage: boolean;
  @Input() countries: IUkOfficialCountry[];
  @Input() countryCodes: CountryCodeModel[];
  @Input() hasWorkMobilePhone: boolean = null;
  @Input() user: User;

  @Output() readonly outputUser = new EventEmitter<IUser>();
  @Output() readonly hasWorkMobilePhoneChange = new EventEmitter<boolean>();
  @Output() readonly mobileNumberVerificationStatusChange =
    new EventEmitter<MobileNumberVerificationStatus>();

  hasWorkMobilePhoneRadioGroup: FormRadioGroupInfo;

  @ViewChild('mobilePhoneTemplate', { static: true })
  mobilePhoneTemplate: TemplateRef<ElementRef>;

  @ViewChild('noMobilePhoneTemplate', { static: true })
  noMobilePhoneTemplate: TemplateRef<ElementRef>;

  protected getFormModel() {
    return {
      hasWorkMobilePhone: [
        null,
        { validators: Validators.required, updateOn: 'change' },
      ],
      workMobilePhone: [this.getEmptyPhoneInfo()],
      workAlternativePhone: [this.getEmptyPhoneInfo()],
      noMobilePhoneNumberReason: [''],
      workBuildingAndStreet: ['', Validators.required],
      workBuildingAndStreetOptional: [''],
      workBuildingAndStreetOptional2: [''],
      workTownOrCity: ['', Validators.required],
      workStateOrProvince: [''],
      workCountry: ['', Validators.required],
      workPostCode: [''],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      hasWorkMobilePhone: {
        required: 'Select yes if you have a work mobile number',
      },
      workMobilePhone: {
        allFieldsRequired: 'Enter a work mobile number',
      },
      workAlternativePhone: {
        allFieldsRequired: 'Enter an alternative phone number',
      },
      noMobilePhoneNumberReason: {
        required: 'Enter a reason for not having a work mobile number',
      },
      workBuildingAndStreet: {
        required: 'Enter your work contact address',
      },
      workPostCode: {
        required: 'Enter a UK postcode',
      },
      workTownOrCity: {
        required: 'Enter your work town or city',
      },
    };
  }

  ngOnInit(): void {
    super.ngOnInit();

    this.bindHasWorkMobilePhoneSwitch();
    this.makePostalCodeMandatoryIfInUK();
  }

  ngAfterViewInit(): void {
    // wait for "/assets/uk_countries.json" to load
    // initialize workCountry to UK
    if (!this.formGroup.get('workCountry').value) {
      this.formGroup.get('workCountry').patchValue('UK');
      this.formGroup.get('workCountry').updateValueAndValidity();
    }
  }

  private bindHasWorkMobilePhoneSwitch(): void {
    const hasWorkMobilePhoneFC = this.formGroup.get('hasWorkMobilePhone');
    const workMobilePhoneFC = this.formGroup.get('workMobilePhone');
    const workAlternativePhoneFC = this.formGroup.get('workAlternativePhone');
    const noMobilePhoneNumberReasonFC = this.formGroup.get(
      'noMobilePhoneNumberReason'
    );

    this.hasWorkMobilePhoneRadioGroup = {
      key: 'hasWorkMobilePhone',
      options: [
        {
          label: 'Yes',
          value: true,
          enabled: true,
          conditionalTemplate: this.mobilePhoneTemplate,
        },
        {
          label: 'No',
          value: false,
          enabled: true,
          conditionalTemplate: this.noMobilePhoneTemplate,
        },
      ],
    };

    hasWorkMobilePhoneFC.valueChanges.subscribe((hasWorkMobilePhone) => {
      if (hasWorkMobilePhone) {
        workMobilePhoneFC.setValidators(UkRegistryValidators.allFieldsRequired);
        workAlternativePhoneFC.clearValidators();
        noMobilePhoneNumberReasonFC.clearValidators();
      } else if (hasWorkMobilePhone === false) {
        workMobilePhoneFC.clearValidators();
        workAlternativePhoneFC.setValidators(
          UkRegistryValidators.allFieldsRequired
        );
        noMobilePhoneNumberReasonFC.setValidators(Validators.required);
      }
      workMobilePhoneFC.updateValueAndValidity();
      workAlternativePhoneFC.updateValueAndValidity();
      noMobilePhoneNumberReasonFC.updateValueAndValidity();

      this.hasWorkMobilePhoneChange.emit(hasWorkMobilePhone);
    });

    hasWorkMobilePhoneFC.patchValue(this.hasWorkMobilePhone);

    this.patchPhoneValues();
  }

  private patchPhoneValues(): void {
    this.formGroup.patchValue({
      workMobilePhone: {
        countryCode: this.user?.workMobileCountryCode || '',
        phoneNumber: this.user?.workMobilePhoneNumber || '',
      },
    });
    this.formGroup.patchValue({
      workAlternativePhone: {
        countryCode: this.user?.workAlternativeCountryCode || '',
        phoneNumber: this.user?.workAlternativePhoneNumber || '',
      },
    });
  }

  private makePostalCodeMandatoryIfInUK() {
    this.formGroup.get('workCountry').valueChanges.subscribe((value) => {
      const workPostCodeControl = this.formGroup.get('workPostCode');
      if (value !== 'UK') {
        workPostCodeControl.clearValidators();
      } else {
        workPostCodeControl.setValidators(Validators.required);
      }
      workPostCodeControl.updateValueAndValidity();
    });
  }

  countrySelectOptions(): Option[] {
    return this.countries.map((c) => ({ label: c.item[0].name, value: c.key }));
  }

  onMobileNumberVerificationStatusChange(
    event: MobileNumberVerificationStatus
  ) {
    this.mobileNumberVerificationStatusChange.emit(event);
  }

  private getEmptyPhoneInfo(): PhoneInfo {
    return { countryCode: '', phoneNumber: '' };
  }

  doSubmit() {
    const formValues = { ...this.formGroup.value };

    // Empty fields depending on hasWorkMobilePhone value
    const hasMobile = formValues.hasWorkMobilePhone;
    const mobilePhone = formValues.workMobilePhone;
    const alternativePhone = formValues.workAlternativePhone;

    const phoneInfoData = {
      workMobilePhoneNumber: hasMobile ? mobilePhone.phoneNumber : '',
      workMobileCountryCode: hasMobile ? mobilePhone.countryCode : '',
      workAlternativePhoneNumber: alternativePhone.phoneNumber,
      workAlternativeCountryCode: alternativePhone.phoneNumber
        ? alternativePhone.countryCode
        : '',
      noMobilePhoneNumberReason: hasMobile
        ? ''
        : formValues.noMobilePhoneNumberReason,
    };
    delete formValues.hasWorkMobilePhone;
    delete formValues.workMobilePhone;
    delete formValues.workAlternativePhone;

    this.outputUser.emit({ ...formValues, ...phoneInfoData });
  }
}
