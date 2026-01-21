import { NavigationExtras } from '@angular/router';
import { createAction, props } from '@ngrx/store';
import { MultiSelectedItem } from '@registry-web/shared/components/multi-select-table';
import {
  MetsContact,
  RegistryContact,
} from '@registry-web/shared/model/account';
import { SendInvitationWizardPaths } from '@send-invitation-wizard/send-invitation-wizard.helpers';

export const SendInvitationActions = {
  NAVIGATE_TO: createAction(
    '[Send Invitation Wizard] Navigate to',
    props<{ step: SendInvitationWizardPaths; extras?: NavigationExtras }>()
  ),

  CANCEL: createAction(
    '[Send Invitation Wizard] Cancel clicked',
    props<{ route: string }>()
  ),
  CANCEL_REQUEST: createAction('[Send Invitation Wizard] Cancel request'),

  SET_SELECTED_CONTACTS: createAction(
    '[Send Invitation Wizard] Set selected contacts',
    props<{
      metsContactsWithSelectState: MultiSelectedItem<MetsContact>[];
      registryContactsWithSelectState: MultiSelectedItem<RegistryContact>[];
    }>()
  ),
  COMPLETE_SELECTED_CONTACTS: createAction(
    '[Send Invitation Wizard] Complete selected contacts'
  ),

  INIT_OVERVIEW: createAction(
    '[Send Invitation Wizard] Initialized Overview page'
  ),

  CLEAR_REQUEST: createAction('[Send Invitation Wizard] Clear request'),
  SUBMIT_REQUEST: createAction('[Send Invitation Wizard] Submit request'),
  SUBMIT_REQUEST_SUCCESS: createAction(
    '[Send Invitation Wizard] Submit request success'
  ),
};
