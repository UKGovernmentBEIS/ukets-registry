import {
  AfterViewInit,
  Component,
  computed,
  EventEmitter,
  Input,
  OnInit,
  Output,
  TemplateRef,
  viewChild,
} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  ValidationErrors,
  ValidatorFn,
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
import { PhoneInfo } from '@shared/form-controls';
import { CountryCodeModel } from '@shared/countries/country-code.model';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { takeUntil } from 'rxjs';

const Ukas = ['uka1To99', 'uka100To999', 'uka1000Plus'] as const;
type Ukas = (typeof Ukas)[number];

@Component({
  providers: [IsBillablePipe],
  selector: 'app-account-details-form',
  templateUrl: './account-details-form.component.html',
})
export class AccountDetailsFormComponent
  extends UkFormComponent
  implements AfterViewInit, OnInit
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

  readonly ukasOptions = [
    { label: '1-99 UKAs', value: 'uka1To99' },
    { label: '100-999 UKAs', value: 'uka100To999' },
    { label: '1000+ UKAs', value: 'uka1000Plus' },
  ];

  private readonly salesContactDetailsTemplate = viewChild.required(
    'salesContactDetailsTemplate',
    { read: TemplateRef }
  );

  readonly sellingAllowancesRadioGroup = computed<FormRadioGroupInfo>(() => ({
    key: 'sellingAllowances',
    options: [
      {
        label: 'Yes',
        value: true,
        enabled: true,
        conditionalTemplate: this.salesContactDetailsTemplate(),
      },
      {
        label: 'No',
        value: false,
        enabled: true,
      },
    ],
  }));

  private get sellingAllowancesControl() {
    return this.formGroup.get('sellingAllowances') as FormControl<boolean>;
  }
  private get emailAddressControl() {
    return this.formGroup
      .get('salesContactDetails')
      .get('emailGroup')
      .get('emailAddress') as FormControl<string>;
  }
  private get emailAddressConfirmationControl() {
    return this.formGroup
      .get('salesContactDetails')
      .get('emailGroup')
      .get('emailAddressConfirmation') as FormControl<string>;
  }
  private get salesPhoneControl() {
    return this.formGroup
      .get('salesContactDetails')
      .get('salesPhone') as FormControl<PhoneInfo>;
  }
  private get ukasControl() {
    return this.formGroup.get('ukas') as FormControl<Ukas[]>;
  }

  protected getFormModel(): any {
    return {
      name: ['', Validators.required],
      sellingAllowances: [
        this.accountDetails ? this.accountDetails.sellingAllowances : null,
        {
          validators: [Validators.required],
          updateOn: 'change',
        },
      ],
      salesContactDetails: this.formBuilder.group(
        {
          emailGroup: this.formBuilder.group(
            {
              emailAddress: [''],
              emailAddressConfirmation: [''],
            },
            { validators: [emailsMustMatch()] }
          ),
          salesPhone: [
            this._salesPhoneInfo ?? { countryCode: '', phoneNumber: '' },
          ],
        },
        { validators: [atLeastOneContactDetailFilled()] }
      ),
      ukas: new FormControl<Ukas[]>([], Validators.required),
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      name: {
        required: 'Enter the Account name',
      },
      sellingAllowances: {
        required: 'Select yes if you want to sell UK Allowances',
      },
      salesContactDetails: {
        bothContactDetailsEmpty: 'Enter at least email or phone number',
      },
      emailGroup: {
        emailsDontMatch:
          'Invalid re-typed email address. The email address and the re-typed email address should match',
      },
      emailAddress: {
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Email address should not exceed 256 characters',
      },
      emailAddressConfirmation: {
        email:
          'Enter an email address in the correct format, like name@example.com',
        maxLength: 'Email address should not exceed 256 characters',
      },
      salesPhone: {
        allFieldsRequired: 'Please enter the phone number',
      },
      ukas: { required: 'Select at least one item' },
    };
  }

  ngOnInit() {
    super.ngOnInit();
    //Init value
    if (this.sellingAllowancesControl.value) {
      this.emailAddressControl.enable();
      this.emailAddressConfirmationControl.enable();
      this.salesPhoneControl.enable();
      this.ukasControl.enable();
      this.formGroup.get('salesContactDetails').enable();
    } else {
      this.emailAddressControl.disable();
      this.emailAddressConfirmationControl.disable();
      this.salesPhoneControl.disable();
      this.ukasControl.disable();
      this.formGroup.get('salesContactDetails').disable();
    }

    //Subscribe for changes
    this.sellingAllowancesControl.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe((sellingAllowances) => {
        if (sellingAllowances) {
          this.emailAddressControl.enable();
          this.emailAddressConfirmationControl.enable();
          this.salesPhoneControl.enable();
          this.ukasControl.enable();
        } else {
          this.emailAddressControl.disable();
          this.emailAddressConfirmationControl.disable();
          this.salesPhoneControl.disable();
          this.ukasControl.disable();
        }
        this.formGroup.markAsUntouched();
        this.formGroup.updateValueAndValidity();
        this.errorDetails.emit([]);
      });
  }

  ngAfterViewInit(): void {
    const emailAddress =
      this._accountDetails?.salesContactDetails?.emailAddress?.emailAddress ??
      '';
    const emailAddressConfirmation =
      this._accountDetails?.salesContactDetails?.emailAddress
        ?.emailAddressConfirmation ?? '';
    this.emailAddressControl.patchValue(emailAddress);
    this.emailAddressConfirmationControl.patchValue(emailAddressConfirmation);

    const salesPhone: PhoneInfo = {
      countryCode:
        this._accountDetails?.salesContactDetails?.phoneNumberCountryCode ?? '',
      phoneNumber: this._accountDetails?.salesContactDetails?.phoneNumber ?? '',
    };
    this.salesPhoneControl.patchValue(salesPhone);

    const ukasValue: Ukas[] = [];
    if (this._accountDetails?.salesContactDetails?.uka1To99) {
      ukasValue.push('uka1To99');
    }
    if (this._accountDetails?.salesContactDetails?.uka100To999) {
      ukasValue.push('uka100To999');
    }
    if (this._accountDetails?.salesContactDetails?.uka1000Plus) {
      ukasValue.push('uka1000Plus');
    }
    this.ukasControl.patchValue(ukasValue);
  }

  countrySelectOptions(): Option[] {
    return this.countries
      ? this.countries.map((c) => ({ label: c.item[0].name, value: c.key }))
      : [];
  }

  getAccountNameHintText(): string {
    return 'This will be used to help you identify the account';
  }

  showSalesContactDetailsError(): boolean {
    return (
      this.formGroup.touched &&
      this.sellingAllowancesControl.value &&
      !!this.formGroup.get('salesContactDetails').errors &&
      !!this.validationErrorMessage.salesContactDetails
    );
  }

  showEmailGroupError(): boolean {
    return (
      this.formGroup.touched &&
      this.sellingAllowancesControl.value &&
      !!this.formGroup.get('salesContactDetails').get('emailGroup').errors &&
      !!this.validationErrorMessage.emailGroup
    );
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
    outputModel.sellingAllowances = this.sellingAllowancesControl.value;

    if (this.showSalesContact && outputModel.sellingAllowances) {
      const salesContactDetails: AccountDetails['salesContactDetails'] = {};

      if (this.emailAddressControl.value.trim() === '') {
        salesContactDetails.emailAddress = null;
      } else {
        salesContactDetails.emailAddress = {
          emailAddress: this.emailAddressControl.value,
          emailAddressConfirmation: this.emailAddressConfirmationControl.value,
        };
      }

      const salesPhoneInfo = this.salesPhoneControl.value;

      if (salesPhoneInfo.phoneNumber?.trim() === '') {
        salesContactDetails.phoneNumberCountryCode = null;
        salesContactDetails.phoneNumber = null;
      } else {
        salesContactDetails.phoneNumberCountryCode = salesPhoneInfo.countryCode;
        salesContactDetails.phoneNumber = salesPhoneInfo.phoneNumber;
      }

      const ukasValue = this.ukasControl.value;
      salesContactDetails.uka1To99 = ukasValue.includes('uka1To99');
      salesContactDetails.uka100To999 = ukasValue.includes('uka100To999');
      salesContactDetails.uka1000Plus = ukasValue.includes('uka1000Plus');

      outputModel.salesContactDetails = salesContactDetails;
    } else {
      outputModel.salesContactDetails = null;
    }

    this.accountDetailsOutput.emit(outputModel);
  }
}

function atLeastOneContactDetailFilled(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const emailField: string = control
      .get('emailGroup')
      .get('emailAddress').value;
    const phoneField: PhoneInfo = control.get('salesPhone').value;
    if (
      (emailField === '' || emailField === null) &&
      (phoneField?.phoneNumber === '' || phoneField === null)
    ) {
      return { bothContactDetailsEmpty: true };
    }
    return null;
  };
}

function emailsMustMatch(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const email = control.get('emailAddress');
    const emailConfirmation = control.get('emailAddressConfirmation');
    if (
      email?.invalid ||
      emailConfirmation?.invalid ||
      email?.value === emailConfirmation?.value
    ) {
      return null;
    }

    return { emailsDontMatch: true };
  };
}
