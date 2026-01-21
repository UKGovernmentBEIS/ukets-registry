import { createFeature, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  MetsContact,
  RegistryContact,
} from '@registry-web/shared/model/account';
import { SendInvitationActions } from '@send-invitation-wizard/store/send-invitation-wizard.actions';
import { MultiSelectedItem } from '@registry-web/shared/components/multi-select-table';

const featureName = 'sendInvitationWizard';

export interface SendInvitationWizardState {
  metsContactsWithSelectState: MultiSelectedItem<MetsContact>[];
  registryContactsWithSelectState: MultiSelectedItem<RegistryContact>[];
  backlinkToOverview: boolean;
}

export const initialState: SendInvitationWizardState = {
  metsContactsWithSelectState: [],
  registryContactsWithSelectState: [],
  backlinkToOverview: false,
};

export const sendInvitationWizardFeature = createFeature({
  name: featureName,
  reducer: createReducer(
    initialState,

    mutableOn(
      SendInvitationActions.SET_SELECTED_CONTACTS,
      (
        state,
        { metsContactsWithSelectState, registryContactsWithSelectState }
      ) => {
        state.metsContactsWithSelectState = metsContactsWithSelectState;
        state.registryContactsWithSelectState = registryContactsWithSelectState;
      }
    ),

    mutableOn(SendInvitationActions.COMPLETE_SELECTED_CONTACTS, (state) => {
      state.backlinkToOverview = false;
    }),

    mutableOn(SendInvitationActions.CLEAR_REQUEST, (state) =>
      resetState(state)
    ),

    mutableOn(SendInvitationActions.INIT_OVERVIEW, (state) => {
      state.backlinkToOverview = true;
    })
  ),
});

function resetState(state: SendInvitationWizardState) {
  state.metsContactsWithSelectState = initialState.metsContactsWithSelectState;
  state.registryContactsWithSelectState =
    initialState.registryContactsWithSelectState;
  state.backlinkToOverview = initialState.backlinkToOverview;
}
