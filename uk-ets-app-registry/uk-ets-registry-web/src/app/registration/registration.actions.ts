import { createAction, props } from '@ngrx/store';
import { IUser, User } from '@shared/user';
import { ErrorDetail } from '@shared/error-summary';
import { UserRepresentation } from './registration.service';
import { MobileNumberVerificationStatus } from '@registry-web/shared/form-controls/uk-select-phone';

export enum RegistrationActionTypes {
  CHECK_PASSWORD_REQUESTED = '[Blacklist API] Check Password Requested',
  SET_PASSWORD_SUCCESS = '[Registration] Set Password Success',
  CONFIRM_DECLARATION = '[Registration] Confirm Declaration',
  SUBMIT = '[Registration] Submit',
  VERIFY_USER_EMAIL = '[Registration] Verify User Email',
  SET_EMAIL_VERIFICATION_NEXT_STEP = '[Registration] Set email verification next step',
  CLEAN_UP_REGISTRATION = '[Registration] Clean Up',
  COMPLETED = '[Registration] Completed',
  DELETED = '[Registration] Deleted',
  FAILED = '[Registration] Failed due to error in Registry',
  SAME_EMAIL = '[Registration] Same Email',
  PERSIST = '[Registration] Persist',
  HAS_WORK_MOBILE_PHONE_CHANGE = '[Registration] Has Work Mobile Phone Change',
  MOBILE_NUMBER_VERIFICATION_STATUS_CHANGE = '[Registration] Work Mobile Number Verification Status Change',
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

export const confirmDeclaration = createAction(
  RegistrationActionTypes.CONFIRM_DECLARATION,
  props<{ confirmed: boolean }>()
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

export const hasWorkMobilePhoneChange = createAction(
  RegistrationActionTypes.HAS_WORK_MOBILE_PHONE_CHANGE,
  props<{ hasWorkMobilePhone: boolean }>()
);

export const mobileNumberVerificationStatusChange = createAction(
  RegistrationActionTypes.MOBILE_NUMBER_VERIFICATION_STATUS_CHANGE,
  props<{
    mobileNumberVerificationStatus: MobileNumberVerificationStatus;
  }>()
);
