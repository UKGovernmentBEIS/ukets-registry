import {
  Component,
  EventEmitter,
  OnDestroy,
  OnInit,
  Output,
} from '@angular/core';
import { UntypedFormBuilder, ValidatorFn } from '@angular/forms';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { take, tap } from 'rxjs/operators';

import { UkFormComponent } from '@shared/form-controls/uk-form.component';

import { Option } from '@registry-web/shared/form-controls/uk-select-input/uk-select.model';
import {
  ReportCreationRequest,
  reportTypeMap,
  StandardReport,
  DatePeriodOptions,
  ReportType,
  Filters,
  generate050YearOptions,
} from '@reports/model';

import { selectReportType } from '@registry-web/reports/selectors';
import { updateSelectedReport } from '@registry-web/reports/actions';
import { selectRegistryConfigurationProperty } from '@shared/shared.selector';

@Component({
  selector: 'app-standard-reports-filters',
  templateUrl: './standard-reports-filters.component.html',
  styleUrls: ['./standard-reports-filters.component.scss'],
})
export class StandardReportsFiltersComponent
  extends UkFormComponent
  implements OnInit, OnDestroy
{
  @Output() reportCreation = new EventEmitter<ReportCreationRequest>();

  selectedReport$: Observable<StandardReport>;
  params = {};
  summaryDescription = null;
  hasPeriod: boolean;
  showForm: boolean;
  datePeriodOptions: Option[] = DatePeriodOptions;
  dateFormat = 'YYYY-MM-DD';
  reportType: ReportType;
  errorMessage: any;

  constructor(
    protected formBuilder: UntypedFormBuilder,
    private store: Store
  ) {
    super();
  }

  protected getFormModel() {
    return {};
  }

  protected getValidationMessages(): {
    [key: string]: { [key: string]: string };
  } {
    return {
      from: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      to: {
        pattern: 'The date you have entered is invalid.',
        ngbDate: ' ',
      },
      year: {
        required: 'Report year is required',
      },
    };
  }

  ngOnInit(): void {
    this.selectedReport$ = this.store.select(selectReportType).pipe(
      tap((selected) => {
        this.showForm = false;
        if (selected) {
          setTimeout(() => this.generateFormGroup(selected));
        }
      })
    );

    this.store
      .select(selectRegistryConfigurationProperty, {
        property: 'transactions.public.report.startingDay',
      })
      .pipe(take(1))
      .subscribe((startingDay: string) => {
        generate050YearOptions(startingDay);
      });
  }

  generateFormGroup(report: StandardReport) {
    this.hasPeriod = false;
    this.reportType = report.type;
    const reportTypeValue = reportTypeMap[report.type];
    this.params[this.reportType] = [];
    if (reportTypeValue?.filters) {
      this.createFormGroup(
        this.iterateReportFilters(reportTypeValue.filters),
        reportTypeValue?.groupValidators
      );
    } else {
      this.showForm = false;
      this.removeFormControls();
    }
    this.summaryDescription = reportTypeValue?.summary;
    if (this.validationErrorMessage) {
      this.validationErrorMessage = {};
    }
  }

  private iterateReportFilters(filters: Filters[]) {
    let formGroupObj = null;
    filters.forEach((filter, index) => {
      this.params[this.reportType].push({
        inputType: filter['inputType'],
        period: filter['period'],
        model: filter['model'],
        label: filter['label'] ? filter['label'] : null,
        hint: filter['hint'],
        isHorizontal: filter['isHorizontal'],
        optionsValues: filter['optionsValues'],
      });
      if (this.hasPeriod !== true) {
        this.hasPeriod = this.params[this.reportType][index].period;
      }

      if (this.params[this.reportType][index].inputType === 'select') {
        this.params[this.reportType][index].options =
          this.params[this.reportType][index].optionsValues;
      }

      formGroupObj = { ...formGroupObj, ...filter['model'] };
      this.showForm = true;
    });
    return formGroupObj;
  }

  private createFormGroup(formGroupObj, groupValidators: ValidatorFn[]) {
    if (formGroupObj !== null) {
      this.formGroup = this.formBuilder.group(formGroupObj);
      if (groupValidators) {
        this.formGroup.setValidators(groupValidators);
      }
    }
  }

  returnZero() {
    return 0;
  }

  removeFormControls() {
    if (this.formGroup !== undefined) {
      for (let control in this.formGroup.controls) {
        this.formGroup.removeControl(control);
        this.formGroup.clearValidators();
        this.formGroup.reset();
      }
      this.formGroup.updateValueAndValidity();
    }
  }

  trackBy(index) {
    return index;
  }

  formHasErrors(): boolean {
    if (
      this.formGroup &&
      this.formGroup.dirty &&
      this.formGroup.touched &&
      this.formGroup.invalid &&
      !this.isEmpty(this.validationErrorMessage)
    ) {
      return true;
    } else {
      this.validationErrorMessage = {};
      return false;
    }
  }

  isEmpty(obj) {
    for (const prop in obj) {
      if (obj.hasOwnProperty(prop)) return false;
    }
    return true;
  }

  doSubmit() {
    const criteria = this.formGroup?.value ? this.formGroup?.value : null;
    const reportCreationBody: ReportCreationRequest = {
      type: this.reportType,
      queryInfo: { ...criteria },
    };
    this.reportCreation.emit(reportCreationBody);
  }

  generateReport() {
    if (this.formGroup?.controls) {
      this.onSubmit();
      if (this.formGroup?.errors?.invalidDateRange) {
        this.errorMessage = {
          invalidDateRange: "The 'to' date cannot be before the 'from' date",
        };
      } else if (this.validationErrorMessage) {
        this.errorMessage = this.validationErrorMessage;
      }
    } else {
      this.doSubmit();
    }
  }

  ngOnDestroy(): void {
    this.store.dispatch(updateSelectedReport({ selectedReport: null }));
  }
}
