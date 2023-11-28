import { Pipe, PipeTransform } from '@angular/core';
import { AccountType } from '@shared/model/account';

@Pipe({
  name: 'isBillable',
  pure: true,
})
export class IsBillablePipe implements PipeTransform {
  transform(accountType): boolean {
    return (
      accountType === AccountType.TRADING_ACCOUNT ||
      accountType === AccountType.PERSON_HOLDING_ACCOUNT
    );
  }
}
