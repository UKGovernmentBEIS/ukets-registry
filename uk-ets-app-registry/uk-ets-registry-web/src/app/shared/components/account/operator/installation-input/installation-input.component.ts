import { Component, EventEmitter, Input, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  Installation,
  InstallationActivityType,
  OperatorType,
} from '@shared/model/account';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { getOptionsFromMap } from '@shared/shared.util';
import { regulatorMap } from '@account-management/account-list/account-list.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkRegistryValidators } from '@shared/validation';

@Component({
  selector: 'app-installation-input',
  templateUrl: './installation-input.component.html',
})
export class InstallationInputComponent extends UkFormComponent {
  @Input() installation: Installation;
  @Input() title: string;
  @Input() headerTitle: string;
  @Output() readonly installationOutput = new EventEmitter<Installation>();
  activityTypes = InstallationActivityType;
  regulatorOptions: Option[] = getOptionsFromMap(regulatorMap);

  protected getFormModel() {
    return {
      name: ['', Validators.required],
      activityType: ['', Validators.required],
      permit: this.formBuilder.group({
        id: ['', Validators.required],
      }),
      regulator: ['', Validators.required],
      yearsOfVerifiedEmission: this.formBuilder.group(
        {
          firstYear: [this.installation?.firstYear],
          lastYear: [this.installation?.lastYear],
        },
        {
          validators: [
            UkRegistryValidators.doFirstAndLastYearFieldChecks(
              'firstYear',
              'lastYear',
              2021,
              2100
            ),
          ],
        }
      ),
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      name: {
        required: 'Enter the installation name',
      },
      activityType: {
        required: 'Enter the  installation activity type',
      },
      id: {
        required: 'Enter the permit ID',
      },
      regulator: {
        required: 'Enter the regulator',
      },
      firstYear: {
        required: 'Enter the first year of verified emission submission',
        // TODO  support parameters in validators e.g 2021
        invalid: 'Invalid first year of verified emission submission',
        yearAfter: 'Year cannot be less than 2021',
        noLetters: 'Invalid year number',
        yearBefore: 'Year cannot be greater than 2100',
      },
      lastYear: {
        invalid: 'Invalid last year of verified emission submission',
        yearAfter: 'Year cannot be less than 2021',
        notGreaterOrEqualThan:
          'Last year of verified emission submission cannot be before First year of verified emission submission',
        noLetters: 'Invalid year number',
        yearBefore: 'Year cannot be greater than 2100',
      },
    };
  }
  protected doSubmit() {
    this.installationOutput.emit({
      type: OperatorType.INSTALLATION,
      name: this.formGroup.get('name').value,
      activityType: this.formGroup.get('activityType').value,
      permit: {
        id: this.formGroup.get('permit').get('id').value,
      },
      regulator: this.formGroup.get('regulator').value,
      firstYear: this.formGroup.get('yearsOfVerifiedEmission').get('firstYear')
        .value,
      lastYear: this.formGroup.get('yearsOfVerifiedEmission').get('lastYear')
        .value,
    });
  }

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  activityTypeOptions(): Option[] {
    return Object.keys(this.activityTypes)
      .sort((a, b) => (this.activityTypes[a] > this.activityTypes[b] ? 1 : -1))
      .map((c) => ({ label: this.activityTypes[c], value: c }));
  }

  onContinue() {
    // this.logAllErrors();
    this.onSubmit();
  }
}
