import { EnrolledUser } from '@registry-web/registry-administration/authority-setting/model';
import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  cancelAuthoritySetting,
  clearAuthoritySettingState,
  fetchEnrolledUserSuccess,
  setUserAsAuthoritySuccess
} from '@registry-web/registry-administration/authority-setting/action';

export const authoritySettingFeatureKey = 'authority-setting';

export interface AuthoritySettingState {
  enrolledUser: EnrolledUser;
}

export const initialState: AuthoritySettingState = {
  enrolledUser: null
};

const authoritySettingReducer = createReducer(
  initialState,
  mutableOn(fetchEnrolledUserSuccess, (state, { enrolledUser }) => {
    state.enrolledUser = enrolledUser;
  }),
  mutableOn(cancelAuthoritySetting, state => {
    state.enrolledUser = null;
  }),
  mutableOn(setUserAsAuthoritySuccess, state => {
    state.enrolledUser = null;
  }),
  mutableOn(clearAuthoritySettingState, state => {
    state.enrolledUser = null;
  })
);

export function reducer(
  state: AuthoritySettingState | undefined,
  action: Action
) {
  return authoritySettingReducer(state, action);
}
