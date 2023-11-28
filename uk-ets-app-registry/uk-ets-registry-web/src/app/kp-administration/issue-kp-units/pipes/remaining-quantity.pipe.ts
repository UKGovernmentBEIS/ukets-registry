import { Pipe, PipeTransform } from '@angular/core';
import { RegistryLevelInfo } from '@shared/model/transaction';

/**
 * A pipe to render The registry level info in select the unit type issuance screen.
 */
@Pipe({
  name: 'remainingQuantity',
})
export class RemainingQuantityPipe implements PipeTransform {
  transform(value: RegistryLevelInfo, args?: any): any {
    if (
      value &&
      value.initialQuantity >= 0 &&
      value.consumedQuantity >= 0 &&
      value.pendingQuantity >= 0
    ) {
      return (
        value.initialQuantity - value.consumedQuantity - value.pendingQuantity
      );
    }
    return '';
  }
}
