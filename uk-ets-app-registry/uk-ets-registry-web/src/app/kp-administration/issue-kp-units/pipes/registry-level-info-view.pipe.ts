import { Pipe, PipeTransform } from '@angular/core';
import {
  EnvironmentalActivityLabelMap,
  RegistryLevelInfo
} from '@shared/model/transaction';
import { KpIssuanceTaskDetailsDto } from '@task-management/model';

/**
 * A pipe to render The registry level info in select the unit type issuance screen.
 */
@Pipe({
  name: 'unitTypeAndActivity'
})
export class UnitTypeAndActivityPipe implements PipeTransform {
  transform(
    value: RegistryLevelInfo | KpIssuanceTaskDetailsDto,
    args?: any
  ): any {
    if (value && value.unitType) {
      return (
        value.unitType +
        (value.environmentalActivity
          ? ' - ' +
            EnvironmentalActivityLabelMap.get(value.environmentalActivity)
          : '')
      );
    }
    return '';
  }
}
