import { createSelector } from '@ngrx/store';
import { recoveryMethodsChangeFeature } from './recovery-methods-change.reducer';

export const {
  selectRecoveryMethodsChangeState,
  selectCaller,
  selectOriginCaller,
  selectRecoveryCountryCode,
  selectRecoveryPhoneNumber,
  selectRecoveryEmailAddress,
  selectNewRecoveryCountryCode,
  selectNewRecoveryPhoneNumber,
  selectNewRecoveryEmailAddress,
} = recoveryMethodsChangeFeature;

export const selectOriginRoute = createSelector(
  selectOriginCaller,
  (originCaller) => originCaller?.route
);
