import {
  createFeature,
  createFeatureSelector,
  createReducer,
  createSelector,
} from '@ngrx/store';
import { mutableOn } from '@registry-web/shared/mutable-on';
import { recoveryMethodsActions } from './recovery-methods-change.actions';

const featureKey = 'recoveryMethodsChange';

export interface RecoveryMethodsChangeState {
  originCaller: {
    route: string;
  };
  caller: {
    route: string;
  };
  recoveryCountryCode: string;
  recoveryPhoneNumber: string;
  recoveryPhoneNumberSuccess: boolean; //verifies recovery phone has updated successfully
  workMobileCountryCode: string;
  workMobilePhoneNumber: string;
  recoveryEmailAddress: string;
  recoveryEmailAddressSuccess: boolean; //verifies recovery email has updated successfully
  newRecoveryCountryCode: string;
  newRecoveryPhoneNumber: string;
  newRecoveryEmailAddress: string;
  recoveryPhoneExpiredAt: number;
  recoveryPhoneResendSuccess: boolean; //verifies recovery phone resent successfully
  recoveryEmailExpiredAt: number;
  recoveryEmailResendSuccess: boolean; //verifies recovery email resent successfully
}

export const initialState: RecoveryMethodsChangeState = {
  originCaller: null,
  caller: {
    route: null,
  },
  recoveryCountryCode: null,
  recoveryPhoneNumber: null,
  recoveryPhoneNumberSuccess: false,
  workMobileCountryCode: null,
  workMobilePhoneNumber: null,
  recoveryEmailAddress: null,
  recoveryEmailAddressSuccess: false,
  newRecoveryCountryCode: null,
  newRecoveryPhoneNumber: null,
  newRecoveryEmailAddress: null,
  recoveryPhoneExpiredAt: null,
  recoveryPhoneResendSuccess: null,
  recoveryEmailExpiredAt: null,
  recoveryEmailResendSuccess: null,
};

export const recoveryMethodsChangeFeature = createFeature({
  name: featureKey,
  reducer: createReducer(
    initialState,

    mutableOn(
      recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_PHONE_WIZARD,
      (
        state,
        {
          caller,
          recoveryCountryCode,
          recoveryPhoneNumber,
          workMobileCountryCode,
          workMobilePhoneNumber,
        }
      ) => {
        state.originCaller = caller;
        state.caller = caller;
        state.recoveryCountryCode = recoveryCountryCode || null;
        state.recoveryPhoneNumber = recoveryPhoneNumber || null;
        state.recoveryPhoneNumberSuccess = false;
        state.workMobileCountryCode = workMobileCountryCode || null;
        state.workMobilePhoneNumber = workMobilePhoneNumber || null;
        state.recoveryEmailAddress = null;
      }
    ),

    mutableOn(
      recoveryMethodsActions.RESET_RECOVERY_PHONE_FROM_STATE,
      (state, { request }) => {
        state.newRecoveryCountryCode = request.newRecoveryCountryCode;
        state.newRecoveryPhoneNumber = request.newRecoveryPhoneNumber;
      }
    ),

    mutableOn(
      recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE_VERIFICATION,
      (state) => {
        state.recoveryPhoneNumberSuccess = true;
      }
    ),

    mutableOn(
      recoveryMethodsActions.REQUEST_RESEND_UPDATE_RECOVERY_PHONE_SECURITY_CODE_SUCCESS,
      (state) => {
        state.recoveryPhoneResendSuccess = true;
      }
    ),

    mutableOn(
      recoveryMethodsActions.REQUEST_PHONE_SET_EXPIRED_AT,
      (state, { expiredAt }) => {
        state.recoveryPhoneExpiredAt = expiredAt;
      }
    ),

    mutableOn(
      recoveryMethodsActions.REQUEST_EMAIL_SET_EXPIRED_AT,
      (state, { expiredAt }) => {
        state.recoveryEmailExpiredAt = expiredAt;
      }
    ),

    mutableOn(recoveryMethodsActions.REQUEST_REMOVE_RECOVERY_PHONE, (state) => {
      state.newRecoveryCountryCode = null;
      state.newRecoveryPhoneNumber = null;
    }),

    mutableOn(
      recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_PHONE,
      (state, { request, caller }) => {
        state.caller = caller;
        state.newRecoveryCountryCode = request.newRecoveryCountryCode;
        state.newRecoveryPhoneNumber = request.newRecoveryPhoneNumber;
      }
    ),

    mutableOn(
      recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_WIZARD,
      (state, { caller, recoveryEmailAddress }) => {
        state.originCaller = caller;
        state.caller = caller;
        state.recoveryCountryCode = null;
        state.recoveryPhoneNumber = null;
        state.workMobileCountryCode = null;
        state.workMobilePhoneNumber = null;
        state.recoveryEmailAddress = recoveryEmailAddress || null;
        state.recoveryEmailAddressSuccess = false;
      }
    ),

    mutableOn(
      recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL,
      (state, { request, caller }) => {
        state.caller = caller;
        state.newRecoveryEmailAddress = request.newRecoveryEmailAddress;
      }
    ),

    mutableOn(
      recoveryMethodsActions.RESET_RECOVERY_EMAIL_FROM_STATE,
      (state, { request }) => {
        state.newRecoveryEmailAddress = request.newRecoveryEmailAddress;
      }
    ),

    mutableOn(
      recoveryMethodsActions.REQUEST_UPDATE_RECOVERY_EMAIL_VERIFICATION,
      (state) => {
        state.recoveryEmailAddressSuccess = true;
      }
    ),

    mutableOn(recoveryMethodsActions.REQUEST_REMOVE_RECOVERY_EMAIL, (state) => {
      state.newRecoveryEmailAddress = null;
    }),

    mutableOn(
      recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_PHONE_CONFIRMATION,
      recoveryMethodsActions.NAVIGATE_TO_UPDATE_RECOVERY_EMAIL_CONFIRMATION,
      (state) => {
        state.caller = { route: null };
        state.newRecoveryEmailAddress = null;
        state.newRecoveryCountryCode = null;
        state.newRecoveryPhoneNumber = null;
      }
    ),

    mutableOn(
      recoveryMethodsActions.NAVIGATE_TO_REMOVE_RECOVERY_PHONE_WIZARD,
      recoveryMethodsActions.NAVIGATE_TO_REMOVE_RECOVERY_EMAIL_WIZARD,
      (state, { caller }) => {
        state.originCaller = caller;
        state.caller = caller;
        state.recoveryCountryCode = null;
        state.recoveryPhoneNumber = null;
        state.workMobileCountryCode = null;
        state.workMobilePhoneNumber = null;
        state.recoveryEmailAddress = null;
      }
    )
  ),
});

const selectRegistryActivation =
  createFeatureSelector<RecoveryMethodsChangeState>(featureKey);

export const selectFeatureReady = createSelector(
  selectRegistryActivation,
  (state) => !!state
);

export const selectPhoneExpiresAt = createSelector(
  selectRegistryActivation,
  (state) => state.recoveryPhoneExpiredAt
);

export const selectEmailExpiresAt = createSelector(
  selectRegistryActivation,
  (state) => state.recoveryEmailExpiredAt
);

export const selectRecoveryPhoneResendSuccess = createSelector(
  selectRegistryActivation,
  (state) => state.recoveryPhoneResendSuccess
);

export const selectRecoveryEmailResendSuccess = createSelector(
  selectRegistryActivation,
  (state) => state.recoveryEmailResendSuccess
);
