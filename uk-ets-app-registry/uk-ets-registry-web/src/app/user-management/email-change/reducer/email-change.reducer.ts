import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  loadConfirmation,
  navigateToEmailChangeWizard,
  navigateToVerificationPage
} from '@email-change/action/email-change.actions';
import { NavigationExtras } from '@angular/router';
import { EmailChangeConfirmation } from '@email-change/model/email-change.model';

export const emailChangeFeatureKey = 'email-change';

export interface EmailChangeState {
  urid: string;
  newEmail: string;
  caller: {
    route: string;
    extras?: NavigationExtras;
  };
  confirmation: EmailChangeConfirmation;
  confirmationLoaded: boolean;
}

export const initialState: EmailChangeState = {
  urid: null,
  newEmail: null,
  caller: {
    route: null
  },
  confirmation: {
    requestId: null,
    tokenExpired: null
  },
  confirmationLoaded: false
};

const emailChangeReducer = createReducer(
  initialState,
  mutableOn(navigateToEmailChangeWizard, (state, { urid, caller }) => {
    state.newEmail = null;
    state.urid = urid;
    state.caller = caller;
  }),
  mutableOn(navigateToVerificationPage, (state, { newEmail }) => {
    state.newEmail = newEmail;
  }),
  mutableOn(loadConfirmation, (state, { urid, expired, requestId }) => {
    state.urid = urid;
    state.confirmation = {
      tokenExpired: expired,
      requestId
    };
    state.confirmationLoaded = true;
  })
);

export function reducer(state: EmailChangeState | undefined, action: Action) {
  return emailChangeReducer(state, action);
}
