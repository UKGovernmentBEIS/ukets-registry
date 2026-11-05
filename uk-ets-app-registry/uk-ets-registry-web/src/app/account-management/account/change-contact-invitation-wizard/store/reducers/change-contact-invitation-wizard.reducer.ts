import { createReducer } from '@ngrx/store';
import { ChangeContactInvitationWizardActions } from '@change-contact-invitation-wizard/store';
import { MultiSelectedItem } from '@shared/components/multi-select-table';
import { MetsContact, RegistryContact } from '@shared/model/account';
import { mutableOn } from '@shared/mutable-on';

export const changeContactInvitationWizardFeatureKey =
  'changeContactInvitationWizard';

export interface ChangeContactInvitationWizardState {
  metsContactsWithSelectState: MultiSelectedItem<MetsContact>[];
  registryContactsWithSelectState: MultiSelectedItem<RegistryContact>[];
  backlinkToOverview: boolean;
}

export const initialState: ChangeContactInvitationWizardState = {
  metsContactsWithSelectState: [],
  registryContactsWithSelectState: [],
  backlinkToOverview: false,
};

export const reducer = createReducer(
  initialState,
  mutableOn(
    ChangeContactInvitationWizardActions.setSelectedContacts,
    (
      state,
      { metsContactsWithSelectState, registryContactsWithSelectState }
    ) => ({
      ...state,
      metsContactsWithSelectState,
      registryContactsWithSelectState,
    })
  ),
  mutableOn(
    ChangeContactInvitationWizardActions.completeSelectedContacts,
    (state) => {
      state.backlinkToOverview = false;
    }
  ),
  mutableOn(ChangeContactInvitationWizardActions.initOverview, (state) => {
    state.backlinkToOverview = true;
  }),
  mutableOn(ChangeContactInvitationWizardActions.clearRequest, (state) =>
    resetState(state)
  )
);

function resetState(state: ChangeContactInvitationWizardState) {
  state.metsContactsWithSelectState = initialState.metsContactsWithSelectState;
  state.registryContactsWithSelectState =
    initialState.registryContactsWithSelectState;
  state.backlinkToOverview = initialState.backlinkToOverview;
}
