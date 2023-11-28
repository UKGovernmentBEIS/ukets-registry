import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { AccountDetails } from '@shared/model/account/account-details';
import { IsBillablePipe } from '@shared/pipes';
import {
  AccountHolder,
  AccountHolderType,
  Individual,
  Organisation,
} from '@shared/model/account/account-holder';
import { AccountType, AccountTypeMap } from '@shared/model/account';
import { UkRegistryValidators } from '@shared/validation';
import { PhoneInfo } from '@shared/form-controls';
import { CountryCodeModel } from '@shared/countries/country-code.model';

@Component({
  providers: [IsBillablePipe],
  selector: 'app-billing-details-form',
  templateUrl: './billing-details-form.component.html',
})
export class BillingDetailsFormComponent
  extends UkFormComponent
  implements OnInit, AfterViewInit
{
  @Input() updateMode = false;
  @Input() accountType: AccountType;
  @Input() accountHolder: AccountHolder;
  @Input() accountHolderType: AccountHolderType;
  _accountDetails: AccountDetails;
  _countries: IUkOfficialCountry[];
  @Input() accountDetailsSameBillingAddress: boolean;
  @Input() countryCodes: CountryCodeModel[];
  @Input() isAdmin: boolean;
  @Input() isSeniorAdmin: boolean;
  @Output() readonly accountDetailsOutput = new EventEmitter<AccountDetails>();
  AccountTypeMap = AccountTypeMap;
  _billingPhoneInfo: PhoneInfo;
  _countryOptions: Option[];

  @Output()
  readonly copyAccountHolderAddressAccountDetails = new EventEmitter<boolean>();

  constructor(
    protected formBuilder: UntypedFormBuilder,
    private isBillablePipe: IsBillablePipe
  ) {
    super();
  }

  @Input()
  set accountDetails(value: AccountDetails) {
    if (value && value.billingContactDetails) {
      this._billingPhoneInfo = {
        phoneNumber: value.billingContactDetails.phoneNumber,
        countryCode: value.billingContactDetails.phoneNumberCountryCode,
      };
    }
    this._accountDetails = value;
  }

  @Input()
  set countries(value: IUkOfficialCountry[]) {
    if (value) {
      this._countries = value;
      this._countryOptions = this.countries.map((c) => ({
        label: c.item[0].name,
        value: c.key,
      }));
    }
  }

  get countries() {
    return this._countries;
  }

  protected getFormModel(): any {
    return {
      address: this.formBuilder.group({
        buildingAndStreet: ['', Validators.required],
        buildingAndStreet2: [''],
        buildingAndStreet3: [''],
        postCode: [''],
        townOrCity: ['', Validators.required],
        stateOrProvince: [''],
        country: ['', Validators.required],
      }),
      billingContactDetails: this.formBuilder.group({
        contactName: ['', Validators.required],
        email: ['', Validators.required],
        sopCustomerId: [''],
      }),
      billingPhone: ['', UkRegistryValidators.allFieldsRequired],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      buildingAndStreet: {
        required: 'Enter the Address line 1.',
      },
      postCode: {
        required: 'Enter the UK postal code or zip.',
      },
      townOrCity: {
        required: 'Enter the Town or City.',
      },
      country: {
        required: 'Enter the Country.',
      },
      contactName: {
        required: 'Enter the billing contact name.',
      },
      billingPhone: {
        allFieldsRequired: 'Please enter the phone number.',
      },
      email: {
        required: 'Enter the email.',
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Email address should not exceed 256 characters',
      },
    };
  }

  ngAfterViewInit(): void {
    if (!this.isBillablePipe.transform(this.accountType)) {
      this.formGroup.get('address').disable();
      this.formGroup.get('billingContactDetails').disable();
      this.formGroup.get('billingPhone').disable();
    }

    this.formGroup.get('address.country').valueChanges.subscribe((value) => {
      if (value !== 'UK') {
        this.formGroup.get('address.postCode').clearValidators();
      } else {
        this.formGroup
          .get('address.postCode')
          .setValidators(Validators.required);
      }
      this.formGroup.get('address.postCode').updateValueAndValidity();
    });

    if (!this.formGroup.get('address.country').value) {
      this.formGroup.get('address').patchValue({ country: 'UK' });
      this.formGroup.get('address.country').updateValueAndValidity();
    }

    if (this._accountDetails && this._accountDetails.billingContactDetails) {
      this.formGroup.patchValue({
        billingPhone: {
          countryCode:
            this._accountDetails.billingContactDetails.phoneNumberCountryCode,
          phoneNumber: this._accountDetails.billingContactDetails.phoneNumber,
        },
      });
    }
  }

  countrySelectOptions(): Option[] {
    return this.countries
      ? this.countries.map((c) => ({ label: c.item[0].name, value: c.key }))
      : [];
  }

  onSameAddressCheckChange(event) {
    if (event.target.checked) {
      this.updateBillingAddress();
    } else {
      this.updateBillingAddress(true);
    }

    this.copyAccountHolderAddressAccountDetails.emit(event.target.checked);
  }

  private updateBillingAddress(empty?: boolean) {
    if (this.accountHolderType === AccountHolderType.INDIVIDUAL) {
      const value = this.accountHolder as Individual;
      this.formGroup.get('address').patchValue({
        buildingAndStreet: empty ? '' : value.address.buildingAndStreet,
        buildingAndStreet2: empty ? '' : value.address.buildingAndStreet2,
        buildingAndStreet3: empty ? '' : value.address.buildingAndStreet3,
        postCode: empty ? '' : value.address.postCode,
        townOrCity: empty ? '' : value.address.townOrCity,
        stateOrProvince: empty ? '' : value.address.stateOrProvince,
        country: empty ? 'UK' : value.address.country,
      });
    } else if (this.accountHolderType === AccountHolderType.ORGANISATION) {
      const value = this.accountHolder as Organisation;
      this.formGroup.get('address').patchValue({
        buildingAndStreet: empty ? '' : value.address.buildingAndStreet,
        buildingAndStreet2: empty ? '' : value.address.buildingAndStreet2,
        buildingAndStreet3: empty ? '' : value.address.buildingAndStreet3,
        postCode: empty ? '' : value.address.postCode,
        townOrCity: empty ? '' : value.address.townOrCity,
        stateOrProvince: empty ? '' : value.address.stateOrProvince,
        country: empty ? 'UK' : value.address.country,
      });
    }
  }

  onContinue() {
    this.onSubmit();
  }

  doSubmit() {
    const outputModel = { ...this._accountDetails };
    if (!this.formGroup.get('address.country').value) {
      this.formGroup.get('address').patchValue({ country: 'UK' });
      this.formGroup.get('address.country').updateValueAndValidity();
    }

    outputModel.address = (
      this.formGroup.get('address') as UntypedFormGroup
    ).getRawValue();
    outputModel.billingContactDetails = (
      this.formGroup.get('billingContactDetails') as UntypedFormGroup
    ).getRawValue();

    const billingPhoneInfo = this.formGroup.get('billingPhone')
      .value as PhoneInfo;
    outputModel.billingContactDetails.phoneNumberCountryCode =
      billingPhoneInfo.countryCode;
    outputModel.billingContactDetails.phoneNumber =
      billingPhoneInfo.phoneNumber;
    this.accountDetailsOutput.emit(outputModel);
  }
}
