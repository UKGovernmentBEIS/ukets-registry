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
  AcquiringAccountTransferInfo,
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
  @Input()
  pendingRegulatorNoticesTaskExists: boolean;
  @Input()
  transferringEmitterId!: string;
  @Input()
  acquiringEmitterId!: string;
  @Output()
  readonly clickChange = new EventEmitter<AccountTransferPathsModel>();
  @Output()
  readonly submitRequest = new EventEmitter<AcquiringAccountTransferInfo>();
  @Output()
  readonly errorDetails = new EventEmitter<ErrorDetail>();

  accountHolderTypes = AccountHolderType;

  onSubmit(): void {
    const request: AcquiringAccountTransferInfo = {
      existingAcquiringAccountHolderIdentifier: this.acquiringAccountHolder.id,
      acquiringAccountHolder: this.acquiringAccountHolder,
      acquiringAccountHolderContactInfo:
        this.acquiringAccountHolderPrimaryContact,
      acquiringEmitterId: this.acquiringEmitterId,
    };
    this.submitRequest.emit(request);
  }

  navigateToSelectUpdateType(): void {
    this.clickChange.emit(
      AccountTransferPathsMap.get(AccountTransferPathsModel.SELECT_UPDATE_TYPE)
    );
  }

  navigateToSetEmitterId(): void {
    this.clickChange.emit(
      AccountTransferPathsMap.get(AccountTransferPathsModel.SET_EMITTER_ID)
    );
  }
}
