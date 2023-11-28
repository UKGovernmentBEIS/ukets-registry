import { createFeatureSelector, createSelector } from '@ngrx/store';
import { State } from 'src/app/reducers';
import { RegistrationState } from './registration.reducer';

const selectRegistration = createFeatureSelector<State, RegistrationState>(
  'registration'
);

export const selectSameAddress = createSelector(
  selectRegistration,
  registrationState => registrationState.sameAddress
);

export const selectSameEmail = createSelector(
  selectRegistration,
  registrationState => registrationState.sameEmail
);

export const selectUser = createSelector(
  selectRegistration,
  registrationState => registrationState.user
);

export const selectVerificationNextStepMessage = createSelector(
  selectRegistration,
  registrationState => registrationState.emailVerificationNextStep
);
