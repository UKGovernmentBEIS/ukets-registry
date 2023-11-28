import { createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  createEmergencyOtpChangeTaskSuccess,
  submitEmail
} from '@user-management/emergency-otp-change/actions/emergency-otp-change.actions';
import { EmergencyOtpChangeTaskResponse } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';

export const emergencyOtpChangeFeatureKey = 'emergencyOtpChange';

export interface EmergencyOtpChangeState {
  email: string;
  taskResponse: EmergencyOtpChangeTaskResponse;
}

export const initialState: EmergencyOtpChangeState = {
  email: null,
  taskResponse: null
};

export const reducer = createReducer(
  initialState,
  mutableOn(submitEmail, (state, { email }) => {
    state.email = email;
  }),
  mutableOn(createEmergencyOtpChangeTaskSuccess, (state, { taskResponse }) => {
    state.taskResponse = taskResponse;
  })
);
