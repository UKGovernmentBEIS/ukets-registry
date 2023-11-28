import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import {
  ControlContainer,
  UntypedFormControl,
  FormGroupDirective,
} from '@angular/forms';
import { formatDate } from '@angular/common';
import { UkProtoFormComponent } from '../../uk-proto-form.component';
import { Subject } from 'rxjs';
import { debounceTime, map, takeUntil } from 'rxjs/operators';
import { dateIsValid } from '@registry-web/shared/shared.util';

// https://stackoverflow.com/questions/53692892/wrapping-angular-reactive-form-component-with-validator
// https://github.com/angular/angular/issues/22532  @Injectable in the parent
@Component({
  selector: 'app-form-control-date',
  templateUrl: './uk-proto-form-date.component.html',
  styleUrls: ['./uk-proto-form-date.component.scss'],
  viewProviders: [
    { provide: ControlContainer, useExisting: FormGroupDirective },
  ],
})
export class UkProtoFormDateComponent
  extends UkProtoFormComponent
  implements OnInit, OnDestroy
{
  // Input placeholder affects only safari browser because input[type=date] is rendered as type text
  @Input() placeholder: string;
  dateFormat = 'YYYY-MM-DD';

  private readonly unsubscribe$: Subject<void> = new Subject();

  constructor(public parentf: FormGroupDirective) {
    super(parentf);
  }

  ngOnInit(): void {
    super.ngOnInit();
    (this.parentf.form.get(this.id) as UntypedFormControl).valueChanges
      .pipe(
        debounceTime(1000),
        map((val) => {
          if (val) {
            (this.parentf.form.get(this.id) as UntypedFormControl).patchValue(
              this.checkAndTransformDate(val),
              {
                emitEvent: false,
              }
            );
          }
        }),
        takeUntil(this.unsubscribe$)
      )
      .subscribe();
  }

  // this transformation is needed for safari browser and it is applied
  // if the format of the date is different from the given type
  checkAndTransformDate(date: string): string {
    const validType = dateIsValid(date, this.dateFormat);
    // If date format is valid then return date - Apply when input is rendered as type date
    if (validType) {
      return date;
    }
    // This check applies only in safari where input type date is rendered as text
    // so the input value must meet the following format
    if (!dateIsValid(date, 'M/D/YYYY')) {
      return date;
    }
    const d = new Date(date);
    return formatDate(d, 'yyyy-MM-dd', 'en');
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
