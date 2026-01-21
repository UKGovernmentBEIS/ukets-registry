import { MultiSelectedItem } from '@registry-web/shared/components/multi-select-table';
import {
  MetsContact,
  RegistryContact,
} from '@registry-web/shared/model/account';
import { SendInvitationWizardState } from '@send-invitation-wizard/store/send-invitation-wizard.reducer';

export const SEND_INVITATION_BASE_PATH = 'send-invitation';

export enum SendInvitationWizardPaths {
  SELECT_CONTACTS = 'select-contacts',
  OVERVIEW = 'overview',
  CANCEL_REQUEST = 'cancel',
  REQUEST_SUBMITTED = 'request-submitted',
}

export const hasSelectedContacts = (
  metsContactsWithSelectState: MultiSelectedItem<MetsContact>[],
  registryContactsWithSelectState: MultiSelectedItem<RegistryContact>[]
): boolean =>
  !!metsContactsWithSelectState.find((contact) => contact.isSelected) ||
  !!registryContactsWithSelectState.find((contact) => contact.isSelected);

export const isWizardCompleted = (state: SendInvitationWizardState): boolean =>
  hasSelectedContacts(
    state.metsContactsWithSelectState,
    state.registryContactsWithSelectState
  );
