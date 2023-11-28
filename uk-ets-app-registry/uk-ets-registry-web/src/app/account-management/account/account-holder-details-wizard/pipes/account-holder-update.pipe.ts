import { Pipe, PipeTransform } from '@angular/core';
import {
  AccountHolderDetailsType,
  AccountHolderDetailsTypeMap,
} from '@account-management/account/account-holder-details-wizard/model';

@Pipe({
  name: 'ahUpdateType',
  pure: true,
})
export class AccountHolderUpdatePipe implements PipeTransform {
  transform(value: AccountHolderDetailsType): string {
    return AccountHolderDetailsTypeMap.get(value);
  }
}
