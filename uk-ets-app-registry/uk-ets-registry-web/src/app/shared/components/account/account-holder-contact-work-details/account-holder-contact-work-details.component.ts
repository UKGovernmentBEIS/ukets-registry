import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { AccountHolderContact } from '@shared/model/account';
import { ErrorDetail } from '@shared/error-summary';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { PhoneInfo } from '@shared/form-controls';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import {
  UkRegistryValidators,
  UkValidationMessageHandler,
} from '@shared/validation';
import { ActivatedRoute } from '@angular/router';
import { ContactType } from '@shared/model/account-holder-contact-type';

@Component({
  selector: 'app-account-holder-contact-work-details',
  templateUrl: './account-holder-contact-work-details.component.html',
})
export class AccountHolderContactWorkDetailsComponent
  implements OnInit, AfterViewInit
{
  _accountHolderContact: AccountHolderContact;
  @Input() isAHUpdateWizard = false;
  @Input() useUpdateLabel = true;
  @Input() accountHolderAddress: any;
  @Input() countryCodes: any[];
  @Input() sameAddress: boolean;
  @Input() contactType: string;

  @Output() readonly output = new EventEmitter<AccountHolderContact>();
  @Output() readonly errorDetails = new EventEmitter<ErrorDetail[]>();
  @Output()
  readonly copyAccountHolderAddressToWorkAddress = new EventEmitter<boolean>();

  _countries: IUkOfficialCountry[];
  _countryOptions: Option[];
  _phoneInfo1: PhoneInfo;
  _phoneInfo2: PhoneInfo;
  formGroup: UntypedFormGroup;

  showErrors: boolean;

  validationErrorMessage: { [key: string]: string } = {};
  validationMessages: { [key: string]: { [key: string]: string } };

  private genericValidator: UkValidationMessageHandler;

  constructor(
    private formBuilder: UntypedFormBuilder,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.formGroup = this.initForm();

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

    this.validationMessages = {
      positionInCompany: {
        required: 'Please enter the position in the company.',
      },
      phone1: {
        allFieldsRequired: 'Please enter the phone number.',
      },
      phone2: {},
      buildingAndStreet: {
        required: 'Enter the address line 1.',
      },
      postCode: {
        required: 'Enter the UK postal code or zip.',
      },
      emailAddress: {
        required: 'Enter the email address.',
        email:
          'Enter an email address in the correct format, like name@example.com',
      },
      townOrCity: {
        required: 'Enter the town or city.',
      },
      emailAddressConfirmation: {
        required: 'Retype the email address.',
        emailNotMatch:
          'Invalid re-typed email address. The email address and the re-typed email address should match.',
        email:
          'Enter an email address in the correct format, like name@example.com',
      },
    };
    this.genericValidator = new UkValidationMessageHandler(
      this.validationMessages
    );
  }

  initForm(): UntypedFormGroup {
    return (this.formGroup = this.formBuilder.group(
      {
        positionInCompany: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
        sameWorkAddress: [''],
        address: this.formBuilder.group({
          buildingAndStreet: [
            '',
            [
              Validators.required,
              UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
            ],
          ],
          buildingAndStreet2: [''],
          buildingAndStreet3: [''],
          postCode: [''],
          townOrCity: [
            '',
            [
              Validators.required,
              UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
            ],
          ],
          stateOrProvince: [''],
          country: [''],
        }),
        phone1: ['', UkRegistryValidators.allFieldsRequired],
        phone2: [''],
        emailAddress: this.formBuilder.group({
          emailAddress: [
            '',
            [
              Validators.required,
              UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
            ],
          ],
          emailAddressConfirmation: [
            '',
            [
              Validators.required,
              UkRegistryValidators.emailVerificationMatcher('emailAddress'),
              UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
            ],
          ],
        }),
      },
      { updateOn: 'submit' }
    ));
  }

  @Input()
  set accountHolderContact(value: AccountHolderContact) {
    if (this.formGroup) {
      this.initFormValues(value);
    }
    if (value && value.phoneNumber) {
      this._phoneInfo1 = {
        phoneNumber: value.phoneNumber.phoneNumber1,
        countryCode: value.phoneNumber.countryCode1,
      };
      this._phoneInfo2 = {
        phoneNumber: value.phoneNumber.phoneNumber1,
        countryCode: value.phoneNumber.countryCode1,
      };
      this._accountHolderContact = value;
    }
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

  ngAfterViewInit(): void {
    if (this._accountHolderContact) {
      this.initFormValues(this._accountHolderContact);
    }
    if (!this.formGroup.get('address.country').value) {
      this.formGroup.get('address').patchValue({ country: 'UK' });
      this.formGroup.get('address.country').updateValueAndValidity();
    }
    if (this.sameAddress) {
      this.formGroup.patchValue({ sameWorkAddress: true });
      this.updateWorkAddress();
    }
  }

  onContinue() {
    this.markAllAsTouched();
    this.showErrors = true;
    if (this.formGroup.valid) {
      const updateObject = this.convertToAccountHolderContact();
      this.output.emit(updateObject);
    } else {
      // TODO this is a repeatable pattern in all the forms, it should be moved to a common base class
      this.validationErrorMessage = this.genericValidator.processMessages(
        this.formGroup
      );
      this.errorDetails.emit(
        this.genericValidator.mapMessagesToErrorDetails(
          this.validationErrorMessage
        )
      );
    }
  }
  // TODO: Angular does not mark as touched  https://github.com/angular/angular/issues/24003
  // this should be also part of a base component
  markAllAsTouched() {
    this.formGroup.markAllAsTouched();
    this.formGroup.get('address').markAllAsTouched();
    this.formGroup.get('emailAddress').markAllAsTouched();
  }

  // TODO: phoneNumber should be converted to an array and this convertion will be simplified
  convertToAccountHolderContact(): AccountHolderContact {
    const updateObject = new AccountHolderContact();
    updateObject.positionInCompany =
      this.formGroup.get('positionInCompany').value;
    updateObject.address = this.formGroup.get('address').value;
    const phoneInfo1 = this.formGroup.get('phone1').value as PhoneInfo;
    const phoneInfo2 = this.formGroup.get('phone2').value as PhoneInfo;
    updateObject.phoneNumber = {
      countryCode1: phoneInfo1.countryCode,
      phoneNumber1: phoneInfo1.phoneNumber,
      countryCode2: phoneInfo2.phoneNumber ? phoneInfo2.countryCode : '',
      phoneNumber2: phoneInfo2.phoneNumber ? phoneInfo2.phoneNumber : '',
    };
    updateObject.emailAddress = this.formGroup.get('emailAddress').value;
    return updateObject;
  }
  // TODO: this will be simplified when the API change

  initFormValues(legalRep: AccountHolderContact) {
    this.formGroup
      .get('positionInCompany')
      .patchValue(legalRep.positionInCompany);
    this.formGroup.get('address').patchValue(legalRep.address);
    this.formGroup.get('phone1').patchValue({
      countryCode: legalRep.phoneNumber.countryCode1,
      phoneNumber: legalRep.phoneNumber.phoneNumber1,
    });
    if (legalRep.phoneNumber.phoneNumber2 !== '') {
      this.formGroup.get('phone2').patchValue({
        countryCode: legalRep.phoneNumber.countryCode2,
        phoneNumber: legalRep.phoneNumber.phoneNumber2,
      });
    }
    this.formGroup.get('emailAddress').patchValue(legalRep.emailAddress);
  }

  getSubtitle(): string {
    if (this.isAHUpdateWizard && this.useUpdateLabel) {
      return `Update the ${this.getContactLabel()} details`;
    } else {
      return `Add the ${this.getContactLabel()} details`;
    }
  }

  onAddressCheckChange(event) {
    if (event.target.checked) {
      this.updateWorkAddress();
    } else {
      this.updateWorkAddress(true);
    }
    this.copyAccountHolderAddressToWorkAddress.emit(event.target.checked);
  }

  // updates work address from input user or cleans it when empty is set to true
  private updateWorkAddress(empty?: boolean) {
    this.formGroup.get('address').patchValue({
      buildingAndStreet: empty
        ? ''
        : this.accountHolderAddress.buildingAndStreet,
      buildingAndStreet2: empty
        ? ''
        : this.accountHolderAddress.buildingAndStreet2,
      buildingAndStreet3: empty
        ? ''
        : this.accountHolderAddress.buildingAndStreet3,
      country: empty ? 'UK' : this.accountHolderAddress.country,
      townOrCity: empty ? '' : this.accountHolderAddress.townOrCity,
      stateOrProvince: empty ? '' : this.accountHolderAddress.stateOrProvince,
      postCode: empty ? '' : this.accountHolderAddress.postCode,
    });
  }

  getContactLabel() {
    return this.contactType === ContactType.PRIMARY
      ? 'Primary Contact'
      : 'alternative Primary Contact';
  }
}
