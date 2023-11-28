import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  emergencyOtpChangeFeatureKey,
  EmergencyOtpChangeState
} from '@user-management/emergency-otp-change/reducers/emergency-otp-change.reducer';

export const selectEmergencyOtpChangeState = createFeatureSelector<
  EmergencyOtpChangeState
>(emergencyOtpChangeFeatureKey);

export const selectEmail = createSelector(
  selectEmergencyOtpChangeState,
  state => state.email
);

export const selectTaskResponse = createSelector(
  selectEmergencyOtpChangeState,
  state => state.taskResponse
);
