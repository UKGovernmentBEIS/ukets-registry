import {
  OnInit,
  ChangeDetectorRef,
  Component,
  OnDestroy,
  Input,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import {
  NgbDateParserFormatter,
  NgbDateAdapter,
  NgbDateStruct,
  NgbCalendar,
  NgbDatepickerConfig,
} from '@ng-bootstrap/ng-bootstrap';
import {
  ControlContainer,
  UntypedFormGroup,
  FormGroupDirective,
  Validators,
} from '@angular/forms';
import { UkProtoFormComponent } from '../../uk-proto-form.component';
import { CustomDateParserFormatter } from './custom-date-parser-formatter';
import { CustomAdapter } from './custom-adapter';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { DatePipe } from '@angular/common';
import { UkDate } from '@registry-web/shared/model/uk-date';

@Component({
  selector: 'app-form-control-datepicker',
  templateUrl: './uk-proto-form-datepicker.component.html',
  styleUrls: ['./uk-proto-form-datepicker.component.scss'],
  viewProviders: [
    { provide: NgbDateAdapter, useClass: CustomAdapter },
    { provide: ControlContainer, useExisting: FormGroupDirective },
    { provide: NgbDateParserFormatter, useClass: CustomDateParserFormatter },
  ],
})
export class UkProtoFormDatePickerComponent
  extends UkProtoFormComponent
  implements OnInit, OnDestroy, OnChanges
{
  private readonly unsubscribe$: Subject<void> = new Subject();
  @Input() dateFormatHint: boolean;
  @Input() isHorizontal?: boolean;
  @Input() isReadonly = false;
  @Input() isDisabled = false;
  @Input() useDefaultAriaLabelledBy = false;
  @Input() attrDefaultAriaLabelledBy: string = undefined;
  @Input() attrRole: string = undefined;
  @Input() hintStyleClass: string;
  @Input() disablePastSelection: boolean;

  minDate = undefined;

  constructor(
    private ref: ChangeDetectorRef,
    private parentform: FormGroupDirective,
    private calendar: NgbCalendar,
    private datePipe: DatePipe
  ) {
    super(parentform);
  }

  setToToday() {
    const today = this.calendar.getToday();
    this.formControl.setValue(this._parseNgbDate(today, 'yyyy-MM-dd'));
  }

  private _parseNgbDate(ngbDate: NgbDateStruct, format: string): string {
    const date = new Date(ngbDate.year, ngbDate.month - 1, ngbDate.day);
    return this.datePipe.transform(date, format);
  }

  createDefaultAria() {
    return this.label ? this.id + '-label' : this.hint ? this.hintId : '';
  }

  ngOnInit(): void {
    super.ngOnInit();
    // Check to detect changes in order to avoid lifecycle errors
    (this.parentform.form.get(this.id) as UntypedFormGroup).valueChanges
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(() => this.ref.detectChanges());

    if (this.isDisabled) {
      (this.parentform.form.get(this.id) as UntypedFormGroup).disable();
    }

    this.formControl.addValidators(
      Validators.pattern(
        /^(\d{4}(-)(0[1-9]|1[0-2])(-)(0[1-9]|[1-2][0-9]|3[0-1]))$/
      )
    );

    if (this.disablePastSelection) {
      const today = this.calendar.getToday();
      this.minDate = {
        year: today.year,
        month: today.month,
        day: today.day,
      };
    }
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (
      this.useDefaultAriaLabelledBy &&
      (changes['hint'] || changes['label'])
    ) {
      this.attrDefaultAriaLabelledBy = this.createDefaultAria();
    }
  }

  isPast(_date: UkDate) {
    const date = new Date(
      Number(_date.year),
      Number(_date.month) - 1,
      Number(_date.day)
    );
    return new Date(date.toDateString()) < new Date(new Date().toDateString());
  }
}
