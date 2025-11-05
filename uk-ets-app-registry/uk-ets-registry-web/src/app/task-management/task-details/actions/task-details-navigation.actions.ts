import { createAction, props } from '@ngrx/store';
import { NavigationExtras } from '@angular/router';

export const navigateToTaskDetails = createAction(
  '[Task Details] Navigate To Task Details',
  props<{ extras?: NavigationExtras }>()
);

export const navigateToTaskHistory = createAction(
  '[Task Details] Navigate To Task History'
);
export const navigateToCompleteTask = createAction(
  '[Task Details] Navigate To Complete Task'
);

export const navigateToTaskApproved = createAction(
  '[Task Details] Navigate To Task Approved'
);

export const navigateToTaskCompletionPending = createAction(
  '[Task Details] Navigate To Task CompletionPending'
);

export const navigateToChangeTaskDeadline = createAction(
  '[Task Details] Navigate To Change Task Deadline'
);

export const navigateToCancelChangeTaskDeadline = createAction(
  '[Task Details] Navigate To Cancel Change Task Deadline',
  props<{ currentRoute: string }>()
);

export const navigateToCheckChangeTaskDeadline = createAction(
  '[Task Details] Navigate To Check Change Task Deadline'
);

export const navigateToChangeTaskDeadlineSuccess = createAction(
  '[Task Details] Navigate To Change Task Deadline Success'
);

export const navigationAwayTargetURL = createAction(
  '[Task Details] Navigation away Target URL',
  props<{ url: string }>()
);

export const navigateToSelectPaymentMethod = createAction(
  '[Task Details] Navigate To Select Payment Method'
);

export const navigateToGovUKPayService = createAction(
  '[Task Details] Navigate To GOV UK Pay Service',
  props<{ nextUrl: string }>()
);

export const navigateToBACSDetailsPaymentMethod = createAction(
  '[Task Details] Navigate To BACS Payment Method'
);

export const navigateToBACSConfirmPaymentMethod = createAction(
  '[Task Details] Navigate To BACS Confirm'
);

export const navigateToBACSCancelPaymentMethod = createAction(
  '[Task Details] Navigate To BACS Cancel'
);

export const navigateToBACSAwaitingPayment = createAction(
  '[Task Details] Navigate To BACS Awaiting Payment'
);
