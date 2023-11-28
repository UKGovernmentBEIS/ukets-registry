import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { UkValidationMessageHandler } from '@shared/validation';
import { FormRadioGroupInfo } from '@shared/form-controls/uk-radio-input/uk-radio.model';
import { UkFormComponent } from '@shared/form-controls/uk-form.component';
import {
  AllocationCategory,
  AllocationCategoryLabel,
} from '@registry-web/shared/model/allocation';

@Component({
  selector: 'app-request-allocation',
  templateUrl: './request-allocation.component.html',
})
export class RequestAllocationComponent
  extends UkFormComponent
  implements OnInit
{
  private _allocationYears: number[];

  @Input()
  public set allocationYears(value: number[]) {
    this._allocationYears = value;
    if (this.formRadioGroupInfo) {
      this.formRadioGroupInfo.options = this.getAllocationYearsRadioOptions();
    }
  }
  public get allocationYears() {
    return this._allocationYears;
  }
  @Input() selectedAllocationYear: number;
  @Input() allocationCategories: string[];
  @Input() selectedAllocationCategory: string;

  @Output() readonly selectAllocationYear = new EventEmitter<number>();
  @Output() readonly selectAllocationCategory =
    new EventEmitter<AllocationCategory>();

  genericValidator: UkValidationMessageHandler;
  validationErrorMessage: { [key: string]: string } = {};
  validationMessages: { [key: string]: { [key: string]: string } };
  formGroup: UntypedFormGroup;
  formRadioGroupInfo: FormRadioGroupInfo;
  formRadioGroupInfoCategory: FormRadioGroupInfo;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.formRadioGroupInfo = {
      key: 'allocationYears',
      radioGroupHeadingCaption: 'Request allocation of UK allowances',
      radioGroupHeading: 'Choose the allocation details',
      radioGroupSubtitle: 'Allocation year',
      options: this.getAllocationYearsRadioOptions(),
    };
    this.formRadioGroupInfoCategory = {
      key: 'allocationCategory',
      radioGroupHeadingCaption: '',
      radioGroupHeading: '',
      radioGroupSubtitle: 'Allocation category',
      options: this.getAllocationCategoriesRadioOptions(),
    };
  }

  protected getFormModel(): any {
    return {
      allocationYears: [
        this.selectedAllocationYear,
        { validators: [Validators.required], updateOn: 'submit' },
      ],
      allocationCategory: [
        this.selectedAllocationCategory,
        { validators: [Validators.required], updateOn: 'submit' },
      ],
    };
  }

  protected getValidationMessages(): { [p: string]: { [p: string]: string } } {
    return {
      allocationYears: {
        required: 'Please select a year for the allocation.',
      },
      allocationCategory: {
        required: 'Please select a category for the allocation.',
      },
    };
  }

  doSubmit() {
    this.selectAllocationYear.emit(this.formGroup.value.allocationYears);
    this.selectAllocationCategory.emit(this.formGroup.value.allocationCategory);
  }

  private getAllocationYearsRadioOptions() {
    return this.allocationYears?.map((y) => ({
      label: y.toString(),
      value: y,
      enabled: true,
    }));
  }

  private getAllocationCategoriesRadioOptions() {
    return [
      {
        label: AllocationCategoryLabel[AllocationCategory.Installation],
        value: AllocationCategory.Installation,
        enabled: true,
      },
      {
        label: AllocationCategoryLabel[AllocationCategory.AircraftOperator],
        value: AllocationCategory.AircraftOperator,
        enabled: true,
      },
    ];
  }
}
