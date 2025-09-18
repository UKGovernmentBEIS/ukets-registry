import { Pipe, PipeTransform } from '@angular/core';
import { convertDateToUkDate, emptyProp } from '../shared.util';
import { UkDate } from '../model/uk-date';
import { GdsDatePipe } from '@shared/pipes/gds-date-pipes';

@Pipe({
  name: 'formatUkDate',
  pure: true,
})
export class FormatUkDatePipe extends GdsDatePipe implements PipeTransform {
  transform(ukDate: UkDate): string {
    const date = this.convertToDate(ukDate);
    if (!date) {
      return null;
    }
    return super.transform(date);
  }

  convertToDate(ukDate: UkDate): Date {
    if (!ukDate || emptyProp(ukDate)) {
      return null;
    }
    const year = Number(ukDate.year);
    const month = Number(ukDate.month);
    const day = Number(ukDate.day);
    if (!year || !month || !day) {
      return null;
    }

    return new Date(year, month - 1, day);
  }
}

@Pipe({
  name: 'nativeDateToUkDate',
})
export class NativeDateToUkDate implements PipeTransform {
  transform(date: Date): UkDate {
    if (!date) {
      return null;
    }
    return convertDateToUkDate(date);
  }
}
