import { createFeatureSelector, createSelector } from '@ngrx/store';
import { selectAccount } from '@registry-web/account-management/account/account-details/account.selector';
import { AccountTransferPathsModel } from '@account-transfer/model';
import {
  accountTransferFeatureKey,
  AccountTransferState,
} from './account-transfer.reducer';

export * from './account-transfer.reducer';

export const selectAccountTransferState = createFeatureSelector<AccountTransferState>(
  accountTransferFeatureKey
);

export const selectAccountTransferType = createSelector(
  selectAccountTransferState,
  (state) => state.updateType
);

export const selectAcquiringAccountHolder = createSelector(
  selectAccountTransferState,
  (state) => state.acquiringAccountHolder
);

export const selectAcquiringAccountHolderIdentifier = createSelector(
  selectAccountTransferState,
  (state) => state.acquiringAccountHolder?.id
);

export const selectAcquiringAccountHolderAddress = createSelector(
  selectAccountTransferState,
  (state) => state.acquiringAccountHolder.address
);

export const selectAcquiringAccountHolderPrimaryContact = createSelector(
  selectAccountTransferState,
  (state) => state.acquiringAccountHolderContactInfo?.primaryContact
);

export const selectTransferringAccountHolder = createSelector(
  selectAccount,
  (account) => (account != null ? account.accountHolder : null)
);

export const selectSubmittedAccountTransferRequestIdentifier = createSelector(
  selectAccountTransferState,
  (state) => state.submittedRequestIdentifier
);

export const calculateGoBackPathFromCheckAccountTransferRequest = createSelector(
  selectAccountTransferState,
  (state) => {
    switch (state.updateType) {
      case 'ACCOUNT_TRANSFER_TO_EXISTING_HOLDER':
        return AccountTransferPathsModel.SELECT_UPDATE_TYPE;
      case 'ACCOUNT_TRANSFER_TO_CREATED_HOLDER':
        return AccountTransferPathsModel.UPDATE_PRIMARY_CONTACT_WORK;
    }
  }
);

export const selectCurrentAccountHolderIdentifier = createSelector(
  selectAccount,
  (account) => account?.accountHolder?.id
);

export const selectPrimaryContactAddressSameAsAccountHolder = createSelector(
  selectAccountTransferState,
  (state) =>
    state.acquiringAccountHolderContactInfo?.isPrimaryAddressSameAsHolder
);
