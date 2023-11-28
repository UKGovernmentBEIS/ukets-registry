import { Pipe, PipeTransform } from '@angular/core';
import { AccountHolderType } from '@shared/model/account';
import { AccountHolderTypeAheadSearchResult } from '@account-shared/model';

@Pipe({
  name: 'formatTypeAheadAccountHolderResult',
})
export class FormatTypeAheadAccountHolderResultPipe implements PipeTransform {
  transform(item: AccountHolderTypeAheadSearchResult): string {
    if (item.type === AccountHolderType.INDIVIDUAL && !!item.lastName) {
      return `${item.firstName} ${item.lastName} (${item.identifier})`;
    }
    return item.name + ' (' + item.identifier + ')';
  }
}

@Pipe({
  name: 'formatAccountHolderResult',
})
export class FormatAccountHolderResultPipe implements PipeTransform {
  transform(item): string {
    if (item.type === AccountHolderType.INDIVIDUAL && !!item.details.lastName) {
      return `${item.details.firstName} ${item.details.lastName}`;
    }
    return item.details.name;
  }
}
