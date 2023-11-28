import { Pipe, PipeTransform } from '@angular/core';
import { AccessRightsLabelMap, ARAccessRights } from '@shared/model/account';

@Pipe({
  name: 'accessRights'
})
export class AccessRightsPipe implements PipeTransform {
  transform(value: ARAccessRights): string {
    return AccessRightsLabelMap.get(value);
  }
}
