import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { UkValidationMessageHandler } from '@registry-web/shared/validation';
import { FormRadioGroupInfo } from '@registry-web/shared/form-controls/uk-radio-input/uk-radio.model';
import { VerifiedEmissions } from '@registry-web/account-shared/model';

@Component({
  selector: 'app-select-year',
  templateUrl: './select-year.component.html',
})
export class SelectYearComponent extends UkFormComponent implements OnInit {
  @Input() emissionEntries: VerifiedEmissions[];

  @Output() readonly selectYear = new EventEmitter<VerifiedEmissions>();
  @Output() readonly cancelEmitter = new EventEmitter();

  genericValidator: UkValidationMessageHandler;
  validationErrorMessage: { [key: string]: string } = {};
  validationMessages: { [key: string]: { [key: string]: string } };
  formGroup: UntypedFormGroup;
  formRadioGroupInfo: FormRadioGroupInfo;
  selectedYear: number;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      key: 'exclusionStatusYears',
      radioGroupHeadingCaption: 'Update exclusion status',
      radioGroupHeading:
        'Select the year to update the operator exclusion status',
      options: this.getSelectionYearsRadioOptions(),
    };
  }

  protected getFormModel(): any {
    return {
      exclusionStatusYears: [
        this.selectedYear,
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

  private getSelectionYearsRadioOptions() {
    const selectionYears = [];
    this.emissionEntries.forEach((entry) => selectionYears.push(entry.year));
    return selectionYears?.map((y) => ({
      label: y.toString(),
      value: y,
      enabled: true,
    }));
  }

  onCancel() {
    this.cancelEmitter.emit();
  }
}
