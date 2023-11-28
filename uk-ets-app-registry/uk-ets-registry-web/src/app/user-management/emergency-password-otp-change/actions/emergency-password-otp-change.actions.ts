import { createAction, props } from '@ngrx/store';
import { EmergencyPasswordOtpChangeTaskResponse } from '@user-management/emergency-password-otp-change/model';

export const submitEmail = createAction(
  '[EmergencyPasswordOtpChange API] Submit email',
  props<{ email: string }>()
);

export const submitEmailSuccess = createAction(
  '[EmergencyPasswordOtpChange API] Submit email Success'
);

export const createEmergencyPasswordOtpChangeTask = createAction(
  '[EmergencyPasswordOtpChange API] Create Emergency Otp Change Task',
  props<{ token: string }>()
);

export const createEmergencyPasswordOtpChangeTaskSuccess = createAction(
  '[EmergencyPasswordOtpChange API] Create Emergency Otp Change Task Success',
  props<{ taskResponse: EmergencyPasswordOtpChangeTaskResponse }>()
);
