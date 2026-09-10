import { createReducer } from '@ngrx/store';
import { NavigationExtras } from '@angular/router';
import { AgentType } from '@user-management/user-details/model/agent.model';
import { UserAgentActions } from '@user-agent/store/actions';
import { PublicAgentDetails } from '@user-agent/model';
import { mutableOn } from '@shared/mutable-on';

export const userAgentFeatureKey = 'user-agent';

export interface UserAgentState {
  urid: string;
  type: AgentType;
  details: PublicAgentDetails;
  caller: {
    route: string;
    extras?: NavigationExtras;
  };
  loaded: boolean;
}

export const initialState: UserAgentState = {
  urid: '',
  type: 'NO',
  details: null,
  caller: {
    route: '',
  },
  loaded: false,
};

export const reducer = createReducer(
  initialState,
  mutableOn(
    UserAgentActions.loadUserAgentDetails,
    (state, { agentInfo, caller }) => {
      state.urid = agentInfo.urid;
      state.type = agentInfo.type;
      state.details = agentInfo.details;
      state.caller = caller;
      state.loaded = true;
    }
  ),
  mutableOn(UserAgentActions.clearUserAgentDetails, (state) => {
    resetState(state);
  })
);

function resetState(state) {
  state.urid = initialState.urid;
  state.type = initialState.type;
  state.details = initialState.details;
  state.caller = initialState.caller;
  state.loaded = initialState.loaded;
}
