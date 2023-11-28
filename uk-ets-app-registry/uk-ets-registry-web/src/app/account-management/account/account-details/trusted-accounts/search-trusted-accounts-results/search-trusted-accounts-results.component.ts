import { Component, EventEmitter, Input, Output } from '@angular/core';
import {
  ARAccessRights,
  TrustedAccount,
  trustedAccountActionMap,
  trustedAccountPendingMap,
  TrustedAccountStatus,
  trustedAccountStatusMap,
  TrustedAccountType,
} from '@registry-web/shared/model/account';
import { SortParameters } from '@shared/search/sort/SortParameters';

@Component({
  selector: 'app-search-trusted-accounts-results',
  templateUrl: './search-trusted-accounts-results.component.html',
})
export class SearchTrustedAccountsResultsComponent {
  @Input() results: TrustedAccount[];
  @Input() isAdmin: boolean;
  @Input() sortParameters: SortParameters;
  @Input() canRequestUpdate: boolean;
  @Input() accountFullId: string;
  @Output() trustedAccountFullIdentifierDescriptionUpdate =
    new EventEmitter<TrustedAccount>();

  @Output() readonly sort = new EventEmitter<SortParameters>();
  readonly TrustedAccountStatus = TrustedAccountStatus;
  readonly trustedAccountStatusMap = trustedAccountStatusMap;
  readonly trustedAccountActionMap = trustedAccountActionMap;
  readonly trustedAccountPendingMap = trustedAccountPendingMap;
  readonly accessRight = ARAccessRights;

  onSorting($event: SortParameters) {
    this.sort.emit($event);
  }

  getAccountType(underSameAccountHolder: boolean): string {
    return underSameAccountHolder
      ? TrustedAccountType.AUTOMATICALLY_TRUSTED
      : TrustedAccountType.MANUALLY_ADDED;
  }

  loadTrustedAccountToBeUpdated(trustedAccount: TrustedAccount): void {
    this.trustedAccountFullIdentifierDescriptionUpdate.emit(trustedAccount);
  }
}
