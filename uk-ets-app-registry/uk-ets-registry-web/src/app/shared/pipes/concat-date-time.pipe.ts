import { Pipe, PipeTransform } from '@angular/core';
import { concatDateTime } from '../shared.util';

/***
 * Pipe for concatenating a date in 'DD MMM YYYY' format and a time in 'h:mma' format and returning a date object in UTC
 */
@Pipe({
  name: 'concatDateTime',
})
export class ConcatDateTimePipe implements PipeTransform {
  transform(value: string, date: string, time: string): string {
    if (!date || !time) {
      return null;
    }

    return concatDateTime(date, time);
  }
}
