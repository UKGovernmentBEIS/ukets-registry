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

export const navigationAwayTargetURL = createAction(
  '[Task Details] Navigation away Target URL',
  props<{ url: string }>()
);
