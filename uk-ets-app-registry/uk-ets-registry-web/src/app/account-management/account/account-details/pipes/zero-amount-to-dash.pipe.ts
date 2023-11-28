import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'zeroAmountToDash',
})
export class ZeroAmountToDashPipe implements PipeTransform {
  transform(value: number): string | number {
    if (value === 0) return '-';
    return value;
  }
}
