import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  changeContactInvitationWizardFeatureKey,
  ChangeContactInvitationWizardState,
} from '@change-contact-invitation-wizard/store';
import { selectAccount } from '@account-management/account/account-details/account.selector';
import {
  ContactInvitationStatusDTO,
  MetsContact,
  RegistryContact,
  RegistryContactType,
  RegistryContactInvitationStatusDTO,
} from '@shared/model/account';
import { MultiSelectedItem } from '@shared/components/multi-select-table';

export const selectChangeContactInvitationWizardState =
  createFeatureSelector<ChangeContactInvitationWizardState>(
    changeContactInvitationWizardFeatureKey
  );

export const selectReturnToOverview = createSelector(
  selectChangeContactInvitationWizardState,
  (state): boolean => state.backlinkToOverview //&& isWizardCompleted(state)
);

export const selectMetsContactsWithSelectState = createSelector(
  selectChangeContactInvitationWizardState,
  (state): MultiSelectedItem<MetsContact>[] => state.metsContactsWithSelectState
);

export const selectRegistryContactsWithSelectState = createSelector(
  selectChangeContactInvitationWizardState,
  (state): MultiSelectedItem<RegistryContact>[] =>
    state.registryContactsWithSelectState
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

export const selectSelectedMetsContactsForDisplay = createSelector(
  selectMetsContactsWithSelectState,
  (metsContactsWithSelectState): MetsContact[] =>
    metsContactsWithSelectState
      .filter((contact) => contact.isSelected)
      .map(({ isSelected, ...contact }) => ({
        ...contact,
        invitationStatus: !contact.invitationStatus,
      }))
);

export const selectSelectedRegistryContactsForDisplay = createSelector(
  selectRegistryContactsWithSelectState,
  (registryContactsWithSelectState): RegistryContact[] =>
    registryContactsWithSelectState
      .filter((contact) => contact.isSelected)
      .map(({ isSelected, ...contact }) => ({
        ...contact,
        invitationStatus: !contact.invitationStatus,
      }))
);

export const selectSelectedMetsContacts = createSelector(
  selectMetsContactsWithSelectState,
  (metsContactsWithSelectState): ContactInvitationStatusDTO[] =>
    metsContactsWithSelectState
      .filter((contact) => contact.isSelected)
      .map(({ isSelected, ...contact }) => ({
        email: contact.email,
        invitationStatus: !contact.invitationStatus,
      }))
);

export const selectSelectedRegistryContacts = createSelector(
  selectRegistryContactsWithSelectState,
  (registryContactsWithSelectState): RegistryContactInvitationStatusDTO[] =>
    registryContactsWithSelectState
      .filter((contact) => contact.isSelected)
      .map(({ isSelected, ...contact }) => ({
        email: contact.email,
        invitationStatus: !contact.invitationStatus,
        contactType: contact.contactType,
      }))
);
