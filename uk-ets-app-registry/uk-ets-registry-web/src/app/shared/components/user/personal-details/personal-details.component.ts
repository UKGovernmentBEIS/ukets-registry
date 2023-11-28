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
import { IUser } from '@shared/user';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { keepSingleSpace } from '@shared/shared.util';

@Component({
  selector: 'app-personal-details-input',
  templateUrl: './personal-details.component.html',
})
export class PersonalDetailsComponent
  extends UkFormComponent
  implements OnInit, AfterViewInit
{
  @Input() caption: string;
  @Input() heading: string;
  @Input() isRequestUpdateProcess = false;
  @Input() isMyProfilePage: boolean;
  @Input() countries: IUkOfficialCountry[];
  @Input() user: IUser;
  @Input() isRequestFromAdmin: boolean;
  @Output() readonly outputUser = new EventEmitter<IUser>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected getFormModel() {
    if (this.isRequestUpdateProcess) {
      return {
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        alsoKnownAs: [''],
        birthDate: [''],
        countryOfBirth: ['', Validators.required],
      };
    } else {
      return {
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        alsoKnownAs: [''],
        buildingAndStreet: ['', Validators.required],
        buildingAndStreetOptional: [''],
        buildingAndStreetOptional2: [''],
        postCode: [''],
        townOrCity: ['', Validators.required],
        stateOrProvince: [''],
        country: ['', Validators.required],
        birthDate: [''],
        countryOfBirth: ['', Validators.required],
      };
    }
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      firstName: {
        required: 'Enter your first name',
      },
      lastName: {
        required: 'Enter your last name',
      },
      buildingAndStreet: {
        required: 'Enter your address',
      },
      townOrCity: {
        required: 'Enter your town or city',
      },
      postCode: {
        required: 'Enter a UK postcode',
      },
      birthDate: {
        tooYoung: 'You must be aged 18 or over to register',
        tooOld: 'The birth date is invalid',
        missingField: 'Enter a complete date',
        invalidInput: 'Enter a valid date',
        invalidDate: 'The date is invalid',
        invalidDay: 'Enter a valid day',
        invalidMonth: 'Enter a valid month',
      },
      country: {
        required: 'Select your country',
      },
      countryOfBirth: {
        required: 'Select your country of birth',
      },
    };
  }

  protected doSubmit() {
    this.outputUser.emit(this.formGroup.value);
  }

  ngOnInit() {
    super.ngOnInit();
    if (!this.isRequestUpdateProcess) {
      this.formGroup.get('country').valueChanges.subscribe((value) => {
        if (value !== 'UK') {
          this.formGroup.get('postCode').clearValidators();
        } else {
          this.formGroup.get('postCode').setValidators(Validators.required);
        }
        this.formGroup.get('postCode').updateValueAndValidity();
      });
    }
  }

  ngAfterViewInit(): void {
    if (!this.isRequestUpdateProcess && !this.formGroup.get('country').value) {
      this.formGroup.patchValue({ country: 'UK' });
      this.formGroup.get('country').updateValueAndValidity();
    }

    if (!this.formGroup.get('countryOfBirth').value) {
      this.formGroup.patchValue({ countryOfBirth: 'UK' });
      this.formGroup.get('countryOfBirth').updateValueAndValidity();
    }

    if (this.formGroup.get('firstName').value) {
      this.formGroup.patchValue({
        firstName: this.keepSingleSpace(this.formGroup.get('firstName').value),
      });
      this.formGroup.get('firstName').updateValueAndValidity();
    }

    if (this.formGroup.get('lastName').value) {
      this.formGroup.patchValue({
        lastName: this.keepSingleSpace(this.formGroup.get('lastName').value),
      });
      this.formGroup.get('lastName').updateValueAndValidity();
    }
  }

  countrySelectOptions(): Option[] {
    return this.countries.map((c) => ({ label: c.item[0].name, value: c.key }));
  }

  keepSingleSpace(value: string) {
    return keepSingleSpace(value);
  }

  getKnownAsHintText(): string {
    if (this.isRequestFromAdmin && !this.isMyProfilePage) {
      return 'If they use a different name, enter that name in full. This name will be displayed to everyone else in the registry.';
    } else {
      return 'If you use a different name, enter that name in full. This name will be displayed to everyone else in the registry.';
    }
  }
}
