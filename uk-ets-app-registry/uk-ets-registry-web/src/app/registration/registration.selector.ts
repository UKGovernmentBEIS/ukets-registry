import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { RegistrationState } from './registration.reducer';

const selectRegistration = createFeatureSelector<State, RegistrationState>(
  'registration'
);

export const selectHasWorkMobilePhone = createSelector(
  selectRegistration,
  (registrationState) => registrationState.hasWorkMobilePhone
);

export const selectMobileNumberVerificationStatus = createSelector(
  selectRegistration,
  (registrationState) => registrationState.mobileNumberVerificationStatus
);

export const selectUser = createSelector(
  selectRegistration,
  (registrationState) => registrationState.user
);

export const selectVerificationNextStepMessage = createSelector(
  selectRegistration,
  (registrationState) => registrationState.emailVerificationNextStep
);

export const selectDeclarationConfirmed = createSelector(
  selectRegistration,
  (registrationState) => registrationState.declarationConfirmed
);
