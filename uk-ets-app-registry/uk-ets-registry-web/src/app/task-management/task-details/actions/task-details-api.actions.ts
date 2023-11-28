import { createAction, props } from '@ngrx/store';
import { TaskCompleteResponse } from '@task-management/model';

export const otpVerificationForTaskRequest = createAction(
  `
  [Task Details]
  2FA verification with OTP Code `,
  props<{ otp: string }>()
);

export const optVerificationForTaskSuccess = createAction(
  `
   [Task Details]
  2FA verification with OTP Code Success`
);

export const completeTaskWithApproval = createAction(
  '[Task API] Approve task',
  props<{
    comment: string;
    taskId: string;
  }>()
);

export const completeTaskWithApprovalSuccess = createAction(
  '[Task API] Approve task success',
  props<{
    taskCompleteResponse: TaskCompleteResponse;
  }>()
);

export const completeTaskWithRejection = createAction(
  '[Task API] Reject task',
  props<{ comment: string; taskId: string }>()
);

export const completeTaskWithRejectionSuccess = createAction(
  '[Task API] Reject task success',
  props<{ taskCompleteResponse: TaskCompleteResponse }>()
);
