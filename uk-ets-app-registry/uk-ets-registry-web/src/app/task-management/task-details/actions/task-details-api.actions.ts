import { createAction, props } from '@ngrx/store';
import {
  PaymentCompleteResponse,
  TaskCompleteResponse,
} from '@task-management/model';

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
    amountPaid?: number;
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

export const fetchPaymentCompleteResponseWithExternalService = createAction(
  '[Task API] Fetch Payment Complete Response With External Service',
  props<{ requestId: string }>()
);

export const fetchPaymentCompleteResponseWithExternalServiceSuccess =
  createAction(
    '[Task API] Fetch Payment Complete Response With External Service Success',
    props<{ taskCompleteResponse: PaymentCompleteResponse }>()
  );

export const fetchPaymentViaWebLinkCompleteResponseWithExternalService =
  createAction(
    '[Task API] Fetch Payment via WebLink Complete Response With External Service',
    props<{ uuid: string }>()
  );

export const fetchPaymentViaWebLinkCompleteResponseWithExternalServiceSuccess =
  createAction(
    '[Task API] Fetch Payment via WebLink  Complete Response With External Service Success',
    props<{ taskCompleteResponse: PaymentCompleteResponse }>()
  );
