import { selectAccount } from '@account-management/account/account-details/account.selector';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  changeAccountHolderFeatureKey,
  ChangeAccountHolderWizardState,
} from '@change-account-holder-wizard/store/reducers';
import { ChangeAccountHolderActionType } from '@registry-web/account-management/account/change-account-holder-wizard/model';
import {
  isExistingAccountHolder,
  isWizardCompleted,
} from '@change-account-holder-wizard/change-account-holder.helpers';

export const selectChangeAccountHolderWizardState =
  createFeatureSelector<ChangeAccountHolderWizardState>(
    changeAccountHolderFeatureKey
  );

export const selectCurrentAccountHolder = createSelector(
  selectAccount,
  (account) => account.accountHolder
);

export const selectChangeAccountHolderSelectionType = createSelector(
  selectChangeAccountHolderWizardState,
  (state) => state.accountHolderSelectionType
);

export const selectAccountHolderList = createSelector(
  selectChangeAccountHolderWizardState,
  (state) => state.accountHolderList
);

export const selectAcquiringAccountHolder = createSelector(
  selectChangeAccountHolderWizardState,
  (state) => state.acquiringAccountHolder
);

export const selectAcquiringAccountHolderType = createSelector(
  selectAcquiringAccountHolder,
  (acquiringAccountHolder) => acquiringAccountHolder.type
);

export const selectAcquiringAccountHolderAddress = createSelector(
  selectAcquiringAccountHolder,
  (acquiringAccountHolder) => acquiringAccountHolder.address
);

export const selectAcquiringAccountHolderContact = createSelector(
  selectChangeAccountHolderWizardState,
  (state) => state.acquiringAccountHolderContact
);

export const selectSamePrimaryContactAddress = createSelector(
  selectChangeAccountHolderWizardState,
  selectAccount,
  (state, info): boolean =>
    state.isPrimaryAddressSameAsHolder
      ? state.isPrimaryAddressSameAsHolder
      : info?.accountHolderContactInfo?.isPrimaryAddressSameAsHolder
);

export const selectIsExistingAccountHolder = createSelector(
  selectChangeAccountHolderSelectionType,
  (accountHolderSelectionType): boolean =>
    isExistingAccountHolder(accountHolderSelectionType)
);

export const selectAccountHolderChangeActionType = createSelector(
  selectIsExistingAccountHolder,
  (isExistingAccountHolder): ChangeAccountHolderActionType =>
    isExistingAccountHolder
      ? ChangeAccountHolderActionType.ACCOUNT_HOLDER_CHANGE_TO_EXISTING_HOLDER
      : ChangeAccountHolderActionType.ACCOUNT_HOLDER_CHANGE_TO_CREATED_HOLDER
);

export const selectIsAccountHolderOrphan = createSelector(
  selectChangeAccountHolderWizardState,
  (state): boolean => state.isAccountHolderOrphan
);

export const selectAccountHolderDelete = createSelector(
  selectChangeAccountHolderWizardState,
  (state): boolean => state.accountHolderDelete
);

export const selectReturnToOverview = createSelector(
  selectChangeAccountHolderWizardState,
  (state): boolean => state.backlinkToOverview && isWizardCompleted(state)
);

export const selectSubmittedRequestIdentifier = createSelector(
  selectChangeAccountHolderWizardState,
  (state): string => state.submittedRequestIdentifier
);
