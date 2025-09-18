import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'isPastDate',
})
export class IsPastDatePipe implements PipeTransform {
  transform(date: Date): boolean {
    date = new Date(date);
    const now = new Date();
    now.setHours(0);
    now.setMinutes(0);
    now.setSeconds(0);
    now.setMilliseconds(0);

    date.setHours(0);
    date.setMinutes(0);
    date.setSeconds(0);
    date.setMilliseconds(0);

    return date.getTime() < now.getTime();
  }
}
