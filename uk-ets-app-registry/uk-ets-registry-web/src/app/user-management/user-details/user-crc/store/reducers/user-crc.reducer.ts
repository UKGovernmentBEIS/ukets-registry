import { createReducer } from '@ngrx/store';
import { NavigationExtras } from '@angular/router';
import { UserAgentActions } from '@user-agent/store/actions';
import { mutableOn } from '@shared/mutable-on';
import { UserCrcActions } from '@user-crc/store/actions';
import { CrcDetails } from '@user-crc/model';

export const userCrcFeatureKey = 'user-crc';

export interface UserCrcState {
  urid: string;
  crcSubmitted: boolean;
  details: CrcDetails;
  caller: {
    route: string;
    extras?: NavigationExtras;
  };
  loaded: boolean;
}

export const initialState: UserCrcState = {
  urid: '',
  crcSubmitted: false,
  details: null,
  caller: {
    route: '',
  },
  loaded: false,
};

export const reducer = createReducer(
  initialState,
  mutableOn(UserCrcActions.loadUserCrcDetails, (state, { crcInfo, caller }) => {
    state.urid = crcInfo.urid;
    state.crcSubmitted = crcInfo.crc;
    state.details = crcInfo.details;
    state.caller = caller;
    state.loaded = true;
  }),
  mutableOn(UserCrcActions.clearUserCrcDetails, (state) => {
    resetState(state);
  })
);

function resetState(state: UserCrcState) {
  state.urid = initialState.urid;
  state.crcSubmitted = initialState.crcSubmitted;
  state.details = initialState.details;
  state.caller = initialState.caller;
  state.loaded = initialState.loaded;
}
