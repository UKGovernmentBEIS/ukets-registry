import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  UkRegistryValidators,
  UkValidationMessageHandler,
} from '@registry-web/shared/validation';
import { VerifiedEmissions } from '@registry-web/account-shared/model';

@Component({
  selector: 'app-select-exclusion-status',
  templateUrl: './select-exclusion-status.component.html',
})
export class SelectExclusionStatusComponent
  extends UkFormComponent
  implements OnInit
{
  @Output()
  readonly selectExclusionStatus = new EventEmitter<boolean>();
  @Input()
  year: number;
  @Input()
  emissionEntries: VerifiedEmissions[];
  genericValidator: UkValidationMessageHandler;
  validationErrorMessage: { [key: string]: string } = {};
  validationMessages: { [key: string]: { [key: string]: string } };
  formGroup: UntypedFormGroup;
  formRadioGroupInfo: FormRadioGroupInfo;
  excluded: string;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    const alreadyExcluded = this.checkPreviousStatus();
    super.ngOnInit();
    this.formRadioGroupInfo = {
      radioGroupHeading: alreadyExcluded
        ? 'Do you want to reverse the exclusion for the selected year?'
        : 'Do you want to exclude the operator for the selected year?',
      radioGroupHeadingCaption: 'Update exclusion status',
      radioGroupSubHeading: 'Year ' + this.year,
      key: 'selectExclusionStatus',
      options: [
        {
          label: 'Yes',
          value: !alreadyExcluded,
          enabled: true,
        },
        {
          label: 'No',
          value: alreadyExcluded,
          enabled: true,
        },
      ],
    };
  }

  protected doSubmit() {
    this.selectExclusionStatus.emit(
      this.formGroup.get('selectExclusionStatus').value
    );
  }

  protected getFormModel(): any {
    return {
      selectExclusionStatus: [
        this.excluded,
        {
          validators: [
            Validators.required,
            UkRegistryValidators.exclusionStatusShouldBeChanged(
              this.checkPreviousStatus()
            ),
          ],
          updateOn: 'submit',
        },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      selectExclusionStatus: {
        required: 'Please select an option.',
        notPermittedValue: 'You must select the other option',
      },
    };
  }

  public checkPreviousStatus() {
    const entry = this.emissionEntries.filter(
      (entry) => entry.year == this.year
    )[0];
    if (entry.reportableEmissions == 'Excluded') {
      return true;
    }
    return false;
  }
}
