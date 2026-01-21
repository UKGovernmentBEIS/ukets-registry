import { Component, input } from '@angular/core';
import { ControlContainer, FormGroupDirective } from '@angular/forms';
import { UkProtoFormComponent } from '@shared/form-controls/uk-proto-form.component';
import {
  ALLOCATION_STATUS_LABELS,
  AllocationStatus,
  AnnualAllocation,
} from '@shared/model/account';

@Component({
  selector: 'app-select-year-table-radio-input',
  templateUrl: './select-year-table-radio-input.component.html',
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class SelectYearTableRadioInputComponent extends UkProtoFormComponent {
  readonly annuals = input.required<AnnualAllocation[]>();

  readonly key = 'exclusionStatusYears';
  readonly AllocationStatus = AllocationStatus;
  readonly allocationStatusLabels = ALLOCATION_STATUS_LABELS;
}
