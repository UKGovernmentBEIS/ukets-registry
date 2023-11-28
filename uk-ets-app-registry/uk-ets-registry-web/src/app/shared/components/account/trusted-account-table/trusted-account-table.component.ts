import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TrustedAccount, trustedAccountStatusMap } from '@shared/model/account';
import { TrustedAccountListType } from './trusted-account-list-type.enum';

@Component({
  selector: 'app-trusted-account-table',
  templateUrl: './trusted-account-table.component.html',
})
export class TrustedAccountTableComponent {
  @Input() trustedAccounts: TrustedAccount[];
  @Input() trustedAccountListType: TrustedAccountListType;
  @Input() hasChangeDescription: boolean;
  @Output()
  readonly trustedAccountFullIdentifierDescriptionUpdate = new EventEmitter<string>();

  readonly trustedAccountListTypes = TrustedAccountListType;
  readonly trustedAccountStatusMap = trustedAccountStatusMap;

  loadTrustedAccountUpdateDescription(accountFullIdentifier): void {
    this.trustedAccountFullIdentifierDescriptionUpdate.emit(accountFullIdentifier);
  }
}
