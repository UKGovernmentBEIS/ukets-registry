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
} from '@shared/model/account/account-holder';
import { AccountType, AccountTypeMap } from '@shared/model/account';
import { UkRegistryValidators } from '@shared/validation';
import { PhoneInfo } from '@shared/form-controls';
import { CountryCodeModel } from '@shared/countries/country-code.model';

@Component({
  providers: [IsBillablePipe],
  selector: 'app-account-details-form',
  templateUrl: './account-details-form.component.html',
})
export class AccountDetailsFormComponent
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
  @Input() showSalesContact: boolean;
  @Output() readonly accountDetailsOutput = new EventEmitter<AccountDetails>();
  AccountTypeMap = AccountTypeMap;
  _salesPhoneInfo: PhoneInfo;
  _countryOptions: Option[];

  @Output()
  readonly copyAccountHolderAddressAccountDetails = new EventEmitter<boolean>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  @Input()
  set accountDetails(value: AccountDetails) {
    if (value?.salesContactDetails) {
      this._salesPhoneInfo = {
        phoneNumber: value.salesContactDetails?.phoneNumber,
        countryCode: value.salesContactDetails?.phoneNumberCountryCode,
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
      name: ['', Validators.required],
      salesContactDetails: this.formBuilder.group({
        emailAddress: this.formBuilder.group({
          emailAddress: [''],
          emailAddressConfirmation: [
            '',
            [UkRegistryValidators.emailVerificationMatcher('emailAddress')],
          ],
        }),
      }),
      salesPhone: [''],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      name: {
        required: 'Enter the Account name.',
      },
      salesPhone: {
        allFieldsRequired: 'Please enter the phone number.',
      },
      emailAddress: {
        required: 'Enter the email.',
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Email address should not exceed 256 characters',
      },
      emailAddressConfirmation: {
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Email address should not exceed 256 characters',
        emailNotMatch:
          'Invalid re-typed email address. The email address and the re-typed email address should match',
      },
    };
  }

  ngAfterViewInit(): void {
    if (this._accountDetails?.salesContactDetails) {
      this.formGroup.patchValue({
        salesPhone: {
          countryCode:
            this._accountDetails.salesContactDetails.phoneNumberCountryCode,
          phoneNumber: this._accountDetails.salesContactDetails.phoneNumber,
        },
      });
    }
  }

  countrySelectOptions(): Option[] {
    return this.countries
      ? this.countries.map((c) => ({ label: c.item[0].name, value: c.key }))
      : [];
  }

  getAccountNameHintText(): string {
    return 'This will be used to help you identify the account';
  }

  onContinue() {
    this.onSubmit();
  }

  doSubmit() {
    const outputModel = new AccountDetails();

    if (this._accountDetails) {
      outputModel.openingDate = this._accountDetails.openingDate;
    }
    outputModel.name = this.formGroup.get('name').value;

    if (this.showSalesContact) {
      outputModel.salesContactDetails = (
        this.formGroup.get('salesContactDetails') as UntypedFormGroup
      ).getRawValue();

      if (
        outputModel.salesContactDetails.emailAddress?.emailAddress?.trim() ===
        ''
      ) {
        outputModel.salesContactDetails.emailAddress = null;
      }

      const salesPhoneInfo = this.formGroup.get('salesPhone')
        .value as PhoneInfo;

      if (salesPhoneInfo.phoneNumber?.trim() !== '') {
        outputModel.salesContactDetails.phoneNumberCountryCode =
          salesPhoneInfo.countryCode;
        outputModel.salesContactDetails.phoneNumber =
          salesPhoneInfo.phoneNumber;
      } else {
        outputModel.salesContactDetails.phoneNumberCountryCode = null;
        outputModel.salesContactDetails.phoneNumber = null;
      }
    }
    this.accountDetailsOutput.emit(outputModel);
  }
}
