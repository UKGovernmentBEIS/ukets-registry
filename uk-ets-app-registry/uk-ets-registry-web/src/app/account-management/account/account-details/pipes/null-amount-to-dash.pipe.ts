import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'nullAmountToDash',
})
export class NullAmountToDashPipe implements PipeTransform {
  transform(value: number): string | number {
    if (value == null) return '-';
    return value;
  }
}
