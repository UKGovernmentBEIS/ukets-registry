import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { IUkOfficialCountry } from '@shared/countries/country.interface';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AccountHolder,
  Individual,
} from '@shared/model/account/account-holder';
import { UkRegistryValidators } from '@shared/validation';

@Component({
  selector: 'app-account-holder-individual-details',
  templateUrl: './account-holder-individual-details.component.html',
})
export class AccountHolderIndividualDetailsComponent
  extends UkFormComponent
  implements AfterViewInit
{
  @Input() caption: string;
  @Input() header: string;
  @Input() isAHUpdateWizard = false;
  @Input() accountHolder: AccountHolder;
  @Input() countries: IUkOfficialCountry[];
  @Output() readonly outputUser = new EventEmitter<AccountHolder>();

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  protected doSubmit() {
    const updateObject = new Individual();
    updateObject.details = this.formGroup.get('details').value;
    updateObject.details.name = [
      updateObject.details.firstName,
      updateObject.details.lastName,
    ].join(' ');
    this.outputUser.emit(updateObject);
  }

  protected getFormModel(): any {
    return {
      details: this.formBuilder.group({
        firstName: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
        lastName: [
          '',
          [
            Validators.required,
            UkRegistryValidators.checkForOnlyWhiteSpaces('required'),
          ],
        ],
        countryOfBirth: ['', Validators.required],
        isOverEighteen: [false, Validators.requiredTrue],
      }),
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      firstName: {
        required: 'Enter the first and middle names.',
      },
      lastName: {
        required: 'Enter the last name.',
      },
      isOverEighteen: {
        required: `The account holder must be aged 18 or over.`,
      },
    };
  }

  ngAfterViewInit(): void {
    if (!this.formGroup.get('details.countryOfBirth').value) {
      this.formGroup.get('details').patchValue({ countryOfBirth: 'UK' });
      this.formGroup.get('details.countryOfBirth').updateValueAndValidity();
    }
  }

  countrySelectOptions(): Option[] {
    return this.countries.map((c) => ({ label: c.item[0].name, value: c.key }));
  }
}
