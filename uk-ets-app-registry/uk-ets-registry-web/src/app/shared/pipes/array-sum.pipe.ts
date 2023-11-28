import { Pipe, PipeTransform } from '@angular/core';

/**
 * <span>{{ balances | arraySum:'balances' }}</span>
 */
@Pipe({
  name: 'arraySum'
})
export class ArraySumPipe implements PipeTransform {
  transform(items: any[], attr: string): any {
    return items !== undefined ? items.reduce((a, b) => a + b[attr], 0) : '0';
  }
}
