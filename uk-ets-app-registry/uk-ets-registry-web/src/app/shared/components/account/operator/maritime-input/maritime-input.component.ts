import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { MaritimeOperator, OperatorType } from '@shared/model/account';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Option } from '@shared/form-controls/uk-select-input/uk-select.model';
import { getOptionsFromMap } from '@shared/shared.util';
import { regulatorMap } from '@account-management/account-list/account-list.model';
import { UntypedFormBuilder, Validators } from '@angular/forms';
import {
  UkRegistryValidators,
  ExistingEmitterIdAsyncValidator,
} from '@shared/validation';
import { take } from 'rxjs';
import {createSelector} from "@ngrx/store";

@Component({
  selector: 'app-maritime-input',
  templateUrl: './maritime-input.component.html',
})
export class MaritimeInputComponent extends UkFormComponent {
  @Input()
  maritime: MaritimeOperator;
  @Input() title: string;
  @Input() headerTitle: string;
  @Input() isSeniorOrJuniorAdmin: boolean;
  @Input() emissionStartYear: number;
  @Output() readonly maritimeOutput = new EventEmitter<MaritimeOperator>();

  regulatorOptions: Option[] = getOptionsFromMap(regulatorMap);

  protected getFormModel() {
    return {
      emitter: this.formBuilder.group({
        emitterId: [
          this.maritime?.emitterId,
          [Validators.required, Validators.pattern('^[a-zA-Z0-9-_]*$')],
          (control) =>
            this.existingEmitterIdAsyncValidator.validateEmitterId(
              this.maritime?.identifier
            )(control),
        ],
      }),
      monitoringPlan: this.formBuilder.group({
        id: ['', Validators.required],
      }),
      imo: ['', Validators.required],
      regulator: ['', Validators.required],
      yearsOfVerifiedEmission: this.formBuilder.group(
        {
          firstYear: [this.maritime?.firstYear],
          lastYear: [this.maritime?.lastYear],
        },
        {
          validators: [
            UkRegistryValidators.doFirstAndLastYearFieldChecks(
              'firstYear',
              'lastYear',
              this.emissionStartYear,
              2100
            ),
          ],
        }
      ),
    };
  }

  @Input()
  set maritimeOperator(value: MaritimeOperator) {
    this.maritime = value;
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      emitterId: {
        required: 'Enter the Emitter ID',
        exists: 'This emitter ID is used by another account',
        pattern: 'The Emitter ID cannot contain any special characters',
      },
      id: {
        required: 'Enter the Monitoring plan ID',
      },
      imo: {
        required: 'Enter the Company IMO number',
      },
      regulator: {
        required: 'Select the Regulator',
      },
      firstYear: {
        required: 'Enter the first year of verified emission submission',
        invalid: 'Invalid first year of verified emission submission',
        yearAfter: 'Year cannot be less than 2026',
        noLetters: 'Invalid year number',
        yearBefore: 'Year cannot be greater than 2100',
      },
      lastYear: {
        invalid: 'Invalid last year of verified emission submission',
        yearAfter: 'Year cannot be less than 2026',
        notGreaterOrEqualThan:
          'Last year of verified emission submission cannot be before First year of verified emission submission',
        noLetters: 'Invalid year number',
        yearBefore: 'Year cannot be greater than 2100',
      },
    };
  }

  protected doSubmit() {
    const updateObject: MaritimeOperator = {
      type: OperatorType.MARITIME_OPERATOR,
      identifier: this.maritime?.identifier,
      emitterId: this.formGroup.get('emitter.emitterId').value,
      monitoringPlan: {
        id: this.formGroup.get('monitoringPlan.id').value,
      },
      imo: this.formGroup.get('imo').value,
      regulator: this.formGroup.get('regulator').value,
      firstYear: this.formGroup.get('yearsOfVerifiedEmission').get('firstYear')
        .value,
      lastYear: this.formGroup.get('yearsOfVerifiedEmission').get('lastYear')
        .value,
    };
    this.maritimeOutput.emit(updateObject);
  }

  constructor(
    protected formBuilder: UntypedFormBuilder,
    private existingEmitterIdAsyncValidator: ExistingEmitterIdAsyncValidator
  ) {
    super();
  }

  onContinue() {
    this.onSubmit();
  }

  onSubmit() {
    if (this.formGroup.pending) {
      this.formGroup.statusChanges.pipe(take(1)).subscribe(() => {
        super.onSubmit();
      });
    } else {
      super.onSubmit();
    }
  }
}
