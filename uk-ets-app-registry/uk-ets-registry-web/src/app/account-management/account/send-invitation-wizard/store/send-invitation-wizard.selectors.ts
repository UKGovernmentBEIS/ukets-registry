import { createSelector } from '@ngrx/store';
import { sendInvitationWizardFeature } from '@send-invitation-wizard/store/send-invitation-wizard.reducer';
import { isWizardCompleted } from '@send-invitation-wizard/send-invitation-wizard.helpers';
import { selectAccount } from '@registry-web/account-management/account/account-details/account.selector';
import { MultiSelectedItem } from '@registry-web/shared/components/multi-select-table';
import {
  MetsContact,
  RegistryContact,
} from '@registry-web/shared/model/account';

export const {
  selectSendInvitationWizardState,
  selectMetsContactsWithSelectState,
  selectRegistryContactsWithSelectState,
} = sendInvitationWizardFeature;

export const selectReturnToOverview = createSelector(
  selectSendInvitationWizardState,
  (state): boolean => state.backlinkToOverview && isWizardCompleted(state)
);

export const selectMetsContacts = createSelector(
  selectMetsContactsWithSelectState,
  selectAccount,
  (metsContactsWithSelectState, account): MultiSelectedItem<MetsContact>[] =>
    metsContactsWithSelectState.length
      ? metsContactsWithSelectState
      : account?.metsContacts || []
);

export const selectRegistryContacts = createSelector(
  selectRegistryContactsWithSelectState,
  selectAccount,
  (
    registryContactsWithSelectState,
    account
  ): MultiSelectedItem<RegistryContact>[] =>
    registryContactsWithSelectState.length
      ? registryContactsWithSelectState
      : account?.registryContacts || []
);

export const selectSelectedMetsContacts = createSelector(
  selectMetsContactsWithSelectState,
  (metsContactsWithSelectState): MetsContact[] =>
    metsContactsWithSelectState
      .filter((contact) => contact.isSelected)
      .map(({ isSelected, ...contact }) => contact)
);

export const selectSelectedRegistryContacts = createSelector(
  selectRegistryContactsWithSelectState,
  (registryContactsWithSelectState): RegistryContact[] =>
    registryContactsWithSelectState
      .filter((contact) => contact.isSelected)
      .map(({ isSelected, ...contact }) => contact)
);
