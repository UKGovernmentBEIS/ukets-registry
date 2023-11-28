import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { PhoneInfo } from '@shared/form-controls/uk-select-phone';
import { UkRegistryValidators } from '@shared/validation/uk-registry.validators';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Individual } from '@shared/model/account/account-holder';

@Component({
  selector: 'app-account-holder-individual-contact-details',
  templateUrl: './account-holder-individual-contact-details.component.html',
})
export class AccountHolderIndividualContactDetailsComponent
  extends UkFormComponent
  implements OnInit, AfterViewInit
{
  _accountHolder: Individual;
  @Input() caption: string;
  @Input() header: string;
  @Input() isAHUpdateWizard = false;
  @Input() countryCodes: any[];
  @Output() readonly output = new EventEmitter<Individual>();
  _countries: IUkOfficialCountry[];
  _countryOptions: Option[];
  _phoneInfo1: PhoneInfo;
  _phoneInfo2: PhoneInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
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
  }

  getFormModel(): any {
    return {
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
    };
  }

  protected doSubmit() {
    const updateObject = this.convertToPerson();
    this.output.emit(updateObject);
  }

  @Input()
  set accountHolder(value: Individual) {
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
      this._accountHolder = value;
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
    if (this._accountHolder) {
      this.initFormValues(this._accountHolder);
    }
    // TODO: set this to the initial state of the individual!
    if (!this.formGroup.get('address.country').value) {
      this.formGroup.get('address').patchValue({ country: 'UK' });
      this.formGroup.get('address.country').updateValueAndValidity();
    }
  }

  onContinue() {
    this.onSubmit();
  }

  markAllAsTouched() {
    super.markAllAsTouched();
    this.formGroup.get('address').markAllAsTouched();
    this.formGroup.get('emailAddress').markAllAsTouched();
  }

  // TODO: see Individual TODO list.  phoneNumber should be converted to an array and this convertion will be simplified
  convertToPerson(): Individual {
    const updateObject = new Individual();
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
  initFormValues(person: Individual) {
    this.formGroup.get('address').patchValue(person.address);
    this.formGroup.get('phone1').patchValue({
      countryCode: person.phoneNumber.countryCode1,
      phoneNumber: person.phoneNumber.phoneNumber1,
    });
    if (person.phoneNumber.phoneNumber2 !== '') {
      this.formGroup.get('phone2').patchValue({
        countryCode: person.phoneNumber.countryCode2,
        phoneNumber: person.phoneNumber.phoneNumber2,
      });
    }
    this.formGroup.get('emailAddress').patchValue(person.emailAddress);
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      phone1: {
        allFieldsRequired:
          'Enter a telephone number, for example +44 0808 157 0192',
      },
      phone2: {},
      buildingAndStreet: {
        required: 'Enter their address',
      },
      postCode: {
        required: 'Enter the UK postal code or zip',
      },
      emailAddress: {
        required: 'Enter the email address',
        email:
          'Enter an email address in the correct format, like name@example.com',
      },
      townOrCity: {
        required: 'Enter the town or city',
      },
      emailAddressConfirmation: {
        required: 'Retype the email address',
        emailNotMatch: 'The emails did not match',
        email: ' ',
      },
    };
  }
}
