import { Component, inject, Input, OnInit } from '@angular/core';
import {
  AbstractControl,
  ControlContainer,
  UntypedFormBuilder,
  UntypedFormControl,
  UntypedFormGroup,
  FormGroupDirective,
  NG_VALIDATORS,
  NG_VALUE_ACCESSOR,
  ValidationErrors,
  Validator,
} from '@angular/forms';
import { Option } from '../../uk-select-input/uk-select.model';
import { UkProtoFormCompositeComponent } from '@shared/form-controls/uk-proto-form-composite.component';
import { daysInMonth, empty, range } from '@shared/shared.util';
import { MonthProperties, MonthPropertiesMap } from '@report-publication/model';
import { GdsDatePipe } from '@shared/pipes';

@Component({
  selector: 'app-form-control-date-select',
  templateUrl: './uk-proto-form-date-select.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: UkProtoFormDateSelectComponent,
      multi: true,
    },
    {
      provide: NG_VALIDATORS,
      useExisting: UkProtoFormDateSelectComponent,
      multi: true,
    },
  ],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkProtoFormDateSelectComponent
  extends UkProtoFormCompositeComponent
  implements Validator
{
  @Input() label: string;
  @Input() hint: string;
  @Input() dateInput: string;
  @Input() placeHolder: string;
  @Input() showYear: boolean;
  @Input() required: boolean;

  selectedDay: number;
  selectedMonth: number;
  selectedMonthLabel: string;
  selectedYear: number;

  private fb = inject(UntypedFormBuilder);
  private gdsDatePipe = inject(GdsDatePipe);

  get dayControl(): UntypedFormControl {
    return this.nestedForm.get('day') as UntypedFormControl;
  }

  get monthControl(): UntypedFormControl {
    return this.nestedForm.get('month') as UntypedFormControl;
  }

  get yearControl(): UntypedFormControl {
    return this.nestedForm.get('year') as UntypedFormControl;
  }

  protected buildForm(): UntypedFormGroup {
    this.selectedMonth = !empty(this.dateInput)
      ? this.transformMonthToLiteral(this.dateInput)
      : 1;
    return this.fb.group({
      day: [this.selectedDay ? this.selectedDay : ''],
      month: [this.selectedMonthLabel ? this.selectedMonthLabel : ''],
      year: [this.selectedYear ? this.selectedYear : ''],
    });
  }

  get dayOptions(): Option[] {
    return [...Array(daysInMonth(this.selectedMonth)).keys()].map((days) => ({
      label: (days + 1).toString(),
      value: days + 1,
    }));
  }

  get monthOptions(): Option[] {
    return Object.entries(MonthPropertiesMap).map(
      ([label, value]: [string, MonthProperties]) => ({
        label: label,
        value: value.monthLabel,
      })
    );
  }

  get yearOptions(): Option[] {
    return range(1970, new Date().getFullYear() + 10).map((year) => ({
      label: year.toString(),
      value: year,
    }));
  }

  transformMonthToLiteral(date: string) {
    if (empty(date)) {
      return null;
    }
    this.selectedDay = parseInt(
      this.gdsDatePipe.transform(date.replace('-', ' ')).split(' ')[0]
    );
    this.selectedMonthLabel = this.gdsDatePipe.transform(date).split(' ')[1];
    return MonthPropertiesMap[this.selectedMonthLabel].literal;
  }

  checkMonthChange(value) {
    const optionValue = value.split(': ')[1];
    this.selectedMonth = MonthPropertiesMap[optionValue].literal;
    this.dayOptions;
  }

  protected getDefaultErrorMessageMap(): { [p: string]: string } {
    return {
      invalidDayOfMonth: 'Please enter a valid day of month',
      monthRequired: 'Please enter month',
    };
  }

  validate(control: AbstractControl): ValidationErrors | null {
    Object.keys(this.nestedForm.controls).forEach((key) => {
      this.nestedForm.controls[key].setErrors(null);
    });
    if (
      !this.required &&
      empty(this.dayControl.value) &&
      empty(this.monthControl.value) &&
      empty(this.yearControl.value)
    ) {
      return null;
    }
    if (empty(this.monthControl.value)) {
      return { monthRequired: this.getDefaultErrorMessageMap().monthRequired };
    }
    if (
      !this.isValidDaysOfMonth(
        this.dayControl.value,
        MonthPropertiesMap[this.monthControl.value].literal
      )
    ) {
      return {
        invalidDayOfMonth: this.getDefaultErrorMessageMap().invalidDayOfMonth,
      };
    }

    return null;
  }

  isValidDaysOfMonth(d: number, m: number, y?: number) {
    return m > 0 && m <= 12 && d > 0 && d <= daysInMonth(m, y);
  }
}
