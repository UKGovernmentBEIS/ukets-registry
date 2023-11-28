import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  emergencyPasswordOtpChangeFeatureKey,
  EmergencyPasswordOtpChangeState
} from '@user-management/emergency-password-otp-change/reducers';

export const selectEmergencyPasswordOtpChangeState = createFeatureSelector<
  EmergencyPasswordOtpChangeState
>(emergencyPasswordOtpChangeFeatureKey);

export const selectEmail = createSelector(
  selectEmergencyPasswordOtpChangeState,
  state => state.email
);

export const selectTaskResponse = createSelector(
  selectEmergencyPasswordOtpChangeState,
  state => state.taskResponse
);
