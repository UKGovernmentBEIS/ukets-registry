import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  authoritySettingFeatureKey,
  AuthoritySettingState
} from '@authority-setting/reducer/authority-setting.reducer';

const selectAuthoritySettingState = createFeatureSelector<
  AuthoritySettingState
>(authoritySettingFeatureKey);

export const selectState = createSelector(
  selectAuthoritySettingState,
  state => state
);

export const selectEnrolledUser = createSelector(
  selectAuthoritySettingState,
  state => state.enrolledUser
);
