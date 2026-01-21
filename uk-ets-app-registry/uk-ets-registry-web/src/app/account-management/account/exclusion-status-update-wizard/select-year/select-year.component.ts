import { Component, computed, input, output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import { Validators } from '@angular/forms';
import { VerifiedEmissions } from '@registry-web/account-shared/model';
import { AnnualAllocation } from '@shared/model/account';

@Component({
  selector: 'app-select-year',
  templateUrl: './select-year.component.html',
})
export class SelectYearComponent extends UkFormComponent {
  readonly emissionEntries = input.required<VerifiedEmissions[]>();
  readonly annuals = input.required<AnnualAllocation[]>();

  readonly selectYear = output<VerifiedEmissions>();
  readonly cancelEmitter = output<void>();

  readonly filteredAnnuals = computed<AnnualAllocation[]>(() =>
    this.emissionEntries()?.map(
      ({ year }) =>
        this.annuals().find((annual) => annual.year === year) ??
        ({ year } as AnnualAllocation)
    )
  );

  protected getFormModel(): any {
    return {
      exclusionStatusYears: [
        null,
        { validators: [Validators.required], updateOn: 'submit' },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      exclusionStatusYears: {
        required: 'Please select a year for exclusion.',
      },
    };
  }

  doSubmit() {
    this.selectYear.emit(this.formGroup.value.exclusionStatusYears);
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
