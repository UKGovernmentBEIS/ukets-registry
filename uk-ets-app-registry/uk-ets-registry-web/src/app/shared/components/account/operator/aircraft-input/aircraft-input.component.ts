import { regulatorMap } from '@account-management/account-list/account-list.model';
import {
  Component,
  EventEmitter,
  Input,
  Output
} from '@angular/core';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { AircraftOperator, OperatorType } from '@shared/model/account';
import { getOptionsFromMap } from '@shared/shared.util';
import { UkRegistryValidators, ExistingEmitterIdAsyncValidator } from '@shared/validation';
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-aircraft-input',
  templateUrl: './aircraft-input.component.html',
})
export class AircraftInputComponent extends UkFormComponent {
  @Input() aircraft: AircraftOperator;
  @Input() title: string;
  @Input() headerTitle: string;
  @Input() isSeniorOrJuniorAdmin: boolean;

  @Output() readonly aircraftOutput = new EventEmitter<AircraftOperator>();

  regulatorOptions: Option[] = getOptionsFromMap(regulatorMap);

  protected getFormModel() {
    return {
      emitter: this.formBuilder.group({
        emitterId: [
          this.aircraft?.emitterId,
          [Validators.required,Validators.pattern('^[a-zA-Z0-9-_]*$')],
          (control) => this.existingEmitterIdAsyncValidator.validateEmitterId(this.aircraft?.identifier)(control)
        ],
      }),
      monitoringPlan: this.formBuilder.group({
        id: ['', Validators.required],
      }),
      regulator: ['', Validators.required],
      yearsOfVerifiedEmission: this.formBuilder.group(
        {
          firstYear: [this.aircraft?.firstYear],
          lastYear: [this.aircraft?.lastYear],
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

  @Input()
  set aircraftOperator(value: AircraftOperator) {
    this.aircraft = value;
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      emitterId: {
        required: 'Enter the Emitter ID',
        exists: 'This emitter ID is used by another account',
        pattern: 'The Emitter ID cannot contain any special characters'        
      },
      id: {
        required: 'Enter the Monitoring plan ID',
      },
      regulator: {
        required: 'Select the Regulator',
      },
      firstYear: {
        required: 'Enter the first year of verified emission submission',
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
    const updateObject: AircraftOperator = {
      type: OperatorType.AIRCRAFT_OPERATOR,
      identifier:this.aircraft?.identifier,
      emitterId: this.formGroup.get('emitter.emitterId').value,
      monitoringPlan: {
        id: this.formGroup.get('monitoringPlan.id').value,
      },
      regulator: this.formGroup.get('regulator').value,
      firstYear: this.formGroup.get('yearsOfVerifiedEmission').get('firstYear')
        .value,
      lastYear: this.formGroup.get('yearsOfVerifiedEmission').get('lastYear')
        .value,
    };
    this.aircraftOutput.emit(updateObject);
  }

  constructor(protected formBuilder: UntypedFormBuilder,private existingEmitterIdAsyncValidator:ExistingEmitterIdAsyncValidator) {
    super();
  }

  onContinue() {
    this.onSubmit();
  }

  onSubmit() {
    if (this.formGroup.pending) {
      this.formGroup.statusChanges.
      pipe(take(1)).
      subscribe(() => {
       super.onSubmit();
      });
    } else {
      super.onSubmit();
    }
  }
}
