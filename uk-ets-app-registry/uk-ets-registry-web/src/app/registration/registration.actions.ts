import { createAction, props } from '@ngrx/store';
import { IUser, User } from '@shared/user';
import { ErrorDetail } from '@shared/error-summary';
import { UserRepresentation } from './registration.service';

export enum RegistrationActionTypes {
  CHECK_PASSWORD_REQUESTED = '[Blacklist API] Check Password Requested',
  SET_PASSWORD_SUCCESS = '[Registration] Set Password Success',
  SUBMIT = '[Registration] Submit',
  VERIFY_USER_EMAIL = '[Registration] Verify User Email',
  SET_EMAIL_VERIFICATION_NEXT_STEP = '[Registration] Set email verification next step',
  CLEAN_UP_REGISTRATION = '[Registration] Clean Up',
  COMPLETED = '[Registration] Completed',
  DELETED = '[Registration] Deleted',
  FAILED = '[Registration] Failed due to error in Registry',
  SAME_EMAIL = '[Registration] Same Email',
  SAME_ADDRESS = '[Registration] Same Address',
  PERSIST = '[Registration] Persist',
}

export const updateUserWorkDetails = createAction(
  '[Registration] Update User Work Details',
  props<{
    user: IUser;
  }>()
);

export const updateUserHomeDetails = createAction(
  '[Registration] Update User Home Details',
  props<{
    user: IUser;
  }>()
);

export const updateUserMemorablePhrase = createAction(
  '[Registration] Update User Memorable Phrase',
  props<{ user: IUser }>()
);

export const updateUserPassword = createAction(
  '[Registration] Update User Password',
  props<{
    password: string;
  }>()
);

export const updateUserRepresentation = createAction(
  '[Registration] Update User Representation ',
  props<{ userRepresentation: UserRepresentation }>()
);

export const cleanUser = createAction('[Registration] Cleanup User');

export const checkPasswordRequested = createAction(
  RegistrationActionTypes.CHECK_PASSWORD_REQUESTED,
  props<{ password: string }>()
);

export const setPasswordSuccess = createAction(
  RegistrationActionTypes.SET_PASSWORD_SUCCESS
);

export const submitRegistration = createAction(
  RegistrationActionTypes.SUBMIT,
  props<{ user: User }>()
);

export const persistRegistration = createAction(
  RegistrationActionTypes.PERSIST,
  props<{ user: UserRepresentation }>()
);

export const deleteRegistration = createAction(
  RegistrationActionTypes.DELETED,
  props<{ user: UserRepresentation }>()
);

export const failedRegistration = createAction(RegistrationActionTypes.FAILED);

export const completedRegistration = createAction(
  RegistrationActionTypes.COMPLETED
);

export const verifyUserEmail = createAction(
  RegistrationActionTypes.VERIFY_USER_EMAIL,
  props<{
    token: string;
    potentialErrors: Map<number, ErrorDetail>;
    nextStepMessages: Map<number, string>;
  }>()
);

export const verificationNextStep = createAction(
  RegistrationActionTypes.SET_EMAIL_VERIFICATION_NEXT_STEP,
  props<{ message: string }>()
);

export const cleanUpRegistration = createAction(
  RegistrationActionTypes.CLEAN_UP_REGISTRATION
);

export const sameEmail = createAction(
  RegistrationActionTypes.SAME_EMAIL,
  props<{ sameEmail: boolean }>()
);

export const sameAddress = createAction(
  RegistrationActionTypes.SAME_ADDRESS,
  props<{ sameAddress: boolean }>()
);
