import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  ReactiveFormsModule,
  UntypedFormBuilder,
  Validators,
} from '@angular/forms';
import { UkFormComponent } from '@registry-web/shared/form-controls/uk-form.component';
import { SharedModule } from '@registry-web/shared/shared.module';
import {
  ALLOCATION_JOB_STATUS_OPTIONS,
  AllocationJobSearchCriteria,
} from '../../models/allocation-job-search-criteria.model';

@Component({
  standalone: true,
  selector: 'app-allocation-job-status-filters',
  templateUrl: './allocation-job-status-filters.component.html',
  imports: [SharedModule, ReactiveFormsModule],
})
export class AllocationJobStatusFiltersComponent
  extends UkFormComponent
  implements OnInit
{
  @Input() hideCriteria: boolean;
  @Input() storedCriteria: AllocationJobSearchCriteria;

  @Output() readonly toggleCriteria = new EventEmitter<boolean>();
  @Output() readonly search = new EventEmitter<AllocationJobSearchCriteria>();
  @Output() readonly submitClick = new EventEmitter<null>();

  allocationJobStatusOptions = ALLOCATION_JOB_STATUS_OPTIONS;

  constructor(protected formBuilder: UntypedFormBuilder) {
    super();
  }

  ngOnInit() {
    super.ngOnInit();
    this.formGroup.reset();
    if (this.storedCriteria) {
      this.formGroup.patchValue(this.storedCriteria);
    }
    this.search.emit(this.formGroup.value);
  }

  protected getFormModel() {
    return {
      requestIdentifier: ['', Validators.minLength(3)],
      id: [''],
      status: [''],
      executionDateFrom: [''],
      executionDateTo: [''],
    };
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      requestIdentifier: {
        minlength: 'Enter at least 3 characters in the "Task ID"',
      },
      id: {
        minlength: 'Enter at least 3 characters in the "Job ID"',
      },
      executionDateFrom: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      executionDateTo: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
    };
  }

  toggleCriteriaClick() {
    this.toggleCriteria.emit();
  }

  doSubmit() {
    this.submitClick.emit();
    this.search.emit(this.formGroup.value);
  }

  onClear() {
    this.formGroup.reset();
  }
}
