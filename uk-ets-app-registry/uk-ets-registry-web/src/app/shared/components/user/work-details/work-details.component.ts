import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';

import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { IUser, User } from '@shared/user';
import { UkRegistryValidators } from '@shared/validation/uk-registry.validators';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { PhoneInfo } from '@shared/form-controls/uk-select-phone';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { CountryCodeModel } from '@shared/countries/country-code.model';

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
  @Input() sameAddress: boolean;
  @Input() sameEmail: boolean;

  @Output() readonly outputUser = new EventEmitter<IUser>();
  outputModel: IUser;

  @Output() readonly copyHomeEmailToWorkEmail = new EventEmitter<boolean>();
  @Output() readonly copyHomeAddressToWorkAddress = new EventEmitter<boolean>();

  _phoneInfo: PhoneInfo;
  _user: User;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel() {
    return {
      workPhone: ['', UkRegistryValidators.allFieldsRequired],
      sameWorkEmail: ['', { updateOn: 'change' }],
      workEmailAddress: ['', [Validators.required, Validators.email]],
      workEmailAddressConfirmation: [
        '',
        [
          Validators.required,
          UkRegistryValidators.emailVerificationMatcher('workEmailAddress'),
        ],
      ],
      sameWorkAddress: ['', { updateOn: 'change' }],
      workBuildingAndStreet: ['', Validators.required],
      workBuildingAndStreetOptional: [''],
      workBuildingAndStreetOptional2: [''],
      workTownOrCity: ['', Validators.required],
      workStateOrProvince: [''],
      workPostCode: [''],
      workCountry: ['', Validators.required],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      workPhone: {
        allFieldsRequired: 'Enter a phone number',
      },
      workBuildingAndStreet: {
        required: 'Enter your work contact address',
      },
      workPostCode: {
        required: 'Enter a UK postcode',
      },
      workEmailAddress: {
        required: 'Enter your email address',
        email:
          'Enter an email address in the correct format, like name@example.com',
      },
      workTownOrCity: {
        required: 'Enter your work town or city',
      },
      workEmailAddressConfirmation: {
        required: 'Confirm your email address',
        emailNotMatch:
          'Invalid re-typed email address. The email address and the re-typed email address should match',
        email:
          'Enter an email address in the correct format, like name@example.com',
      },
    };
  }

  @Input()
  set user(value: User) {
    if (value && value.workCountryCode && value.workPhoneNumber) {
      this._phoneInfo = {
        phoneNumber: value.workPhoneNumber,
        countryCode: value.workCountryCode,
      };
    }
    this._user = value;
  }

  ngOnInit(): void {
    super.ngOnInit();
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

  ngAfterViewInit(): void {
    this.sameEmail = true;
    // initialize  workCountry to UK
    if (!this.formGroup.get('workCountry').value) {
      this.formGroup.patchValue({ workCountry: 'UK' });
      this.formGroup.get('workCountry').updateValueAndValidity();
    }
    if (this.sameEmail) {
      this.formGroup.patchValue({ sameWorkEmail: true });
      this.updateHomeEmail();
    }
    if (this.sameAddress) {
      this.formGroup.patchValue({ sameWorkAddress: true });
      this.updateWorkAddress();
    }
    if (this._user && this._user.workCountry) {
      this.formGroup.patchValue({
        workPhone: {
          countryCode: this._user.workCountryCode,
          phoneNumber: this._user.workPhoneNumber,
        },
      });
    }
  }

  countrySelectOptions(): Option[] {
    return this.countries.map((c) => ({ label: c.item[0].name, value: c.key }));
  }

  onEmailCheckChange(event) {
    if (event.target.checked) {
      this.updateHomeEmail();
    } else {
      this.updateHomeEmail(true);
    }
    this.copyHomeEmailToWorkEmail.emit(event.target.checked);
  }

  onAddressCheckChange(event) {
    if (event.target.checked) {
      this.updateWorkAddress();
    } else {
      this.updateWorkAddress(true);
    }
    this.copyHomeAddressToWorkAddress.emit(event.target.checked);
  }

  private updateHomeEmail(empty?: boolean) {
    this.formGroup.patchValue({
      workEmailAddress: empty ? '' : this._user.emailAddress,
      workEmailAddressConfirmation: empty ? '' : this._user.emailAddress,
    });
  }

  // updates work address from input user or cleans it when empty is set to true
  private updateWorkAddress(empty?: boolean) {
    this.formGroup.patchValue({
      workBuildingAndStreet: empty ? '' : this._user.buildingAndStreet,
      workBuildingAndStreetOptional: empty
        ? ''
        : this._user.buildingAndStreetOptional,
      workBuildingAndStreetOptional2: empty
        ? ''
        : this._user.buildingAndStreetOptional2,
      workCountry: empty ? 'UK' : this._user.country,
      workTownOrCity: empty ? '' : this._user.townOrCity,
      workStateOrProvince: empty ? '' : this._user.stateOrProvince,
      workPostCode: empty ? '' : this._user.postCode,
    });
  }

  doSubmit() {
    // flatten form fields -> TODO: put it in a util class
    const flatForm = Object.assign(
      {},
      ...(function _flatten(o) {
        return [].concat(
          ...Object.keys(o).map((k) =>
            typeof o[k] === 'object' && o[k] !== null
              ? _flatten(o[k])
              : { [k]: o[k] }
          )
        );
      })(this.formGroup.value)
    );
    delete flatForm.sameWorkAddress;
    delete flatForm.sameWorkEmail;
    this.outputModel = flatForm;

    const phoneInfo = this.formGroup.get('workPhone').value as PhoneInfo;
    this.outputModel.workPhoneNumber = phoneInfo.phoneNumber;
    this.outputModel.workCountryCode = phoneInfo.countryCode;
    this.outputUser.emit(this.outputModel);
  }
}
