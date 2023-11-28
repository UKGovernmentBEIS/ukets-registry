import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'govukTime',
})
export class GovukTimePipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case '12:00am':
        return 'midnight';
      case '12:00pm':
        return 'midday';
      case null:
      case undefined:
        return '';
      default:
        return value;
    }
  }
}
