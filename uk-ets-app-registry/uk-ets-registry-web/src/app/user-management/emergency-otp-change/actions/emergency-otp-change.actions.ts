import { createAction, props } from '@ngrx/store';
import { EmergencyOtpChangeTaskResponse } from '@user-management/emergency-otp-change/model/emergency-otp-change.model';

export const submitEmail = createAction(
  '[EmergencyOtpChange API] Submit email',
  props<{ email: string }>()
);

export const submitEmailSuccess = createAction(
  '[EmergencyOtpChange API] Submit email Success'
);

export const createEmergencyOtpChangeTask = createAction(
  '[EmergencyOtpChange API] Create Emergency Otp Change Task',
  props<{ token: string }>()
);

export const createEmergencyOtpChangeTaskSuccess = createAction(
  '[EmergencyOtpChange API] Create Emergency Otp Change Task Success',
  props<{ taskResponse: EmergencyOtpChangeTaskResponse }>()
);
