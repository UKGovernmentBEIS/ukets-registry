import { Pipe, PipeTransform } from '@angular/core';
import { UkDate } from '@shared/model/uk-date';
import { convertToUkDate } from '../shared.util';

@Pipe({
  name: 'dateOfBirth',
})
export class DateOfBirthPipe implements PipeTransform {
  transform(formattedUkDate: string): UkDate {
    if (!formattedUkDate) {
      return null;
    }
    return convertToUkDate(formattedUkDate);
  }
}
