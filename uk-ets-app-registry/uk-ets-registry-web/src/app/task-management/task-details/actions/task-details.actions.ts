import { createAction, props } from '@ngrx/store';
import {
  CompleteTaskFormInfo,
  TaskDetails,
  TaskFileDownloadInfo,
  TaskOutcome,
  TaskUpdateDetails,
} from '@task-management/model';
import { DomainEvent } from '@shared/model/event';
import { Account } from '@shared/model/account';

export const prepareNavigationToTask = createAction(
  `
  [Task Header Guard]
  Prepare the navigation to task details by pre-fetching the task details
  and by setting up the 'can go back' url`,
  props<{ taskId: string }>()
);

export const fetchTask = createAction(
  `
  [Task Details Effect]
  Fetch task details`,
  props<{ taskId: string }>()
);

export const loadTask = createAction(
  `
  [Task Details Effect]
  Load task details to the store
`,
  props<{ taskDetails: TaskDetails }>()
);

export const updateTask = createAction(
  '[Task Details Effect] Update task details',
  props<TaskUpdateDetails>()
);

export const updateTaskSuccess = createAction(
  '[Task Details Effect] Update task details success',
  props<{ result: TaskDetails }>()
);

export const fetchAccount = createAction(
  '[Task Account Details] - fetch account',
  props<{ accountId: number }>()
);

export const populateAccount = createAction(
  '[Task Account Details] - Populate Account',
  props<{ account: Account }>()
);

export const fetchTaskHistory = createAction(
  '[Task History] Fetch task history',
  props<{ requestId: string }>()
);

export const fetchTaskHistorySuccess = createAction(
  '[Task History] Fetch task history success',
  props<{ results: DomainEvent[] }>()
);

export const fetchTaskHistoryError = createAction(
  '[Task History] Fetch task history error',
  props<{ error?: any }>()
);

export const taskHistoryAddComment = createAction(
  '[Task History] Add comment',
  props<{ requestId: string; comment: string }>()
);

export const taskHistoryAddCommentSuccess = createAction(
  '[Task History] Add comment success',
  props<{ requestId: string }>()
);

export const taskHistoryAddCommentError = createAction(
  '[Task History] Add comment error',
  props<{ error?: any }>()
);

export const approveTaskDecision = createAction(
  '[Task Details] Approve/Complete Task Decision and Continue To complete page',
  props<{ userDecision: TaskOutcome.APPROVED }>()
);

export const approveTaskDecisionForCompleteOnlyTask = createAction(
  '[Task Details] Approve  a complete only task and stay on the task details screen',
  props<{ userDecision: TaskOutcome.APPROVED }>()
);

export const rejectTaskDecision = createAction(
  '[Task Details] Reject Task Decision and Continue To complete page',
  props<{ userDecision: TaskOutcome.REJECTED }>()
);

export const setCompleteTask = createAction(
  '[Task Details] Set Complete Task Info',
  props<{ completeTaskFormInfo: CompleteTaskFormInfo }>()
);

export const clearUploadedDocuments = createAction(
  '[Task Details] Clear uploaded documents'
);

export const fetchTaskRelatedFile = createAction(
  '[Task Details] Fetch task related file',
  props<{
    fileId: number;
  }>()
);

export const fetchTaskUserFile = createAction(
  '[Task Details] Fetch task user file',
  props<{
    taskFileDownloadInfo: TaskFileDownloadInfo;
  }>()
);

export const fetchAccountOpeningSummaryFile = createAction(
  '[Task Details] Fetch account opening summary file',
  props<{
    taskFileDownloadInfo: TaskFileDownloadInfo;
  }>()
);

export const uploadSelectedFileSuccess = createAction(
  '[Task Details File Upload API] Success',
  props<{ fileId: number; fileUploadIndex: number }>()
);

export const loadTaskFromList = createAction(
  '[Task List] Load Task triggered from list',
  props<{ taskId: string }>()
);

export const loadTaskFromListSuccess = createAction(
  '[Task List] Load Task triggered from list success',
  props<{ taskDetails: TaskDetails }>()
);

export const sendCommentForNotUploadingDocument = createAction(
  '[Task Details] Comment for not uploading documents',
  props<{ comment: string }>()
);

export const deleteSelectedFile = createAction(
  '[Task Details] Delete selected file',
  props<{ fileId: number }>()
);

export const deleteSelectedFileSuccess = createAction(
  '[Task Details] Delete selected file success',
  props<{ fileId: number }>()
);
