import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ErrorDetail } from '@shared/error-summary';
import {
  AccountHolder,
  AccountHolderContact,
  AccountHolderType,
} from '@shared/model/account';
import {
  AccountTransferPathsMap,
  AccountTransferPathsModel,
  AcquiringAccountHolderInfo,
} from '@account-transfer/model';

@Component({
  selector: 'app-check-account-transfer',
  templateUrl: './check-account-transfer.component.html',
  styles: [],
})
export class CheckAccountTransferComponent {
  @Input()
  acquiringAccountHolder: AccountHolder;
  @Input()
  acquiringAccountHolderPrimaryContact: AccountHolderContact;
  @Input()
  transferringAccountHolder: AccountHolder;
  @Output()
  readonly clickChange = new EventEmitter<AccountTransferPathsModel>();
  @Output()
  readonly submitRequest = new EventEmitter<AcquiringAccountHolderInfo>();
  @Output()
  readonly errorDetails = new EventEmitter<ErrorDetail>();

  accountHolderTypes = AccountHolderType;

  onSubmit(): void {
    const request: AcquiringAccountHolderInfo = {
      existingAcquiringAccountHolderIdentifier: this.acquiringAccountHolder.id,
      acquiringAccountHolder: this.acquiringAccountHolder,
      acquiringAccountHolderContactInfo: this
        .acquiringAccountHolderPrimaryContact,
    };
    this.submitRequest.emit(request);
  }

  navigateToSelectUpdateType(): void {
    this.clickChange.emit(
      AccountTransferPathsMap.get(AccountTransferPathsModel.SELECT_UPDATE_TYPE)
    );
  }
}
