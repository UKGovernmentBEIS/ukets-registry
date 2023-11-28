import { createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import { EmergencyPasswordOtpChangeTaskResponse } from '@user-management/emergency-password-otp-change/model';
import {
  createEmergencyPasswordOtpChangeTaskSuccess,
  submitEmail,
  submitEmailSuccess
} from '@user-management/emergency-password-otp-change/actions';

export const emergencyPasswordOtpChangeFeatureKey =
  'emergencyPasswordOtpChange';

export interface EmergencyPasswordOtpChangeState {
  email: string;
  taskResponse: EmergencyPasswordOtpChangeTaskResponse;
}

export const initialState: EmergencyPasswordOtpChangeState = {
  email: null,
  taskResponse: null
};

export const reducer = createReducer(
  initialState,
  mutableOn(submitEmail, (state, { email }) => {
    state.email = email;
  }),
  mutableOn(
    createEmergencyPasswordOtpChangeTaskSuccess,
    (state, { taskResponse }) => {
      state.taskResponse = taskResponse;
    }
  )
);
