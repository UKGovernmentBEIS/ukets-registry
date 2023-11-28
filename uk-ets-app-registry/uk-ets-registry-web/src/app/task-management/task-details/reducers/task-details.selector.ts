import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  taskDetailsFeatureKey,
  TaskDetailsState,
} from './task-details.reducer';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import {
  AccountOpeningTaskDetails,
  RequestType,
  RequestedDocumentUploadTaskDetails,
  TaskDetails,
} from '@task-management/model';
import {
  TRANSACTION_TYPES_VALUES,
  TransactionType,
} from '@shared/model/transaction';
import { UploadStatus } from '@shared/model/file';
import { RequestedDocumentsModel } from '@task-management/model/requested-documents.model';
import { selectUrl } from '@registry-web/shared/router.selector';

const selectTaskDetailsState = createFeatureSelector<TaskDetailsState>(
  taskDetailsFeatureKey
);

export const areTaskDetailsLoaded = createSelector(
  selectTaskDetailsState,
  (state) => state.taskDetailsLoaded
);

export const selectAccountHolder = createSelector(
  selectTaskDetailsState,
  (state) => {
    if (state.taskDetails.taskType === RequestType.ACCOUNT_OPENING_REQUEST) {
      return (state.taskDetails as AccountOpeningTaskDetails).account
        .accountHolder;
    }
    return null;
  }
);

export const selectTask = createSelector(
  selectTaskDetailsState,
  (state) => state.taskDetails
);

export const selectTaskCompleteResponse = createSelector(
  selectTaskDetailsState,
  (state) => state?.taskResponse
);

export const selectIsETSTransaction = createSelector(
  selectTask,
  (task: TaskDetails) => {
    {
      if (task.taskType === RequestType.TRANSACTION_REQUEST) {
        return TRANSACTION_TYPES_VALUES[task.trType].isETSTransaction;
      } else {
        return null;
      }
    }
  }
);

export const selectIsTransactionReversal = createSelector(
  selectTask,
  (task: TaskDetails) => {
    {
      if (task.taskType === RequestType.TRANSACTION_REQUEST) {
        return (
          task.trType === TransactionType.ReverseAllocateAllowances ||
          task.trType === TransactionType.ReverseSurrenderAllowances ||
          task.trType === TransactionType.ReverseDeletionOfAllowances
        );
      } else {
        return null;
      }
    }
  }
);

export const selectTaskHistory = createSelector(
  selectTaskDetailsState,
  (state) => state.taskHistory
);

export const selectTaskDetailsLoading = createSelector(
  selectTaskDetailsState,
  (state) => state.loading
);

export const selectTaskDetailsLoadingError = createSelector(
  selectTaskDetailsState,
  (state) => state.loadingError
);

export const selectLoggedUser = createSelector(
  selectLoggedInUser,
  (authModel) => authModel
);

export const selectUploadFileProgress = createSelector(
  selectTaskDetailsState,
  (state) => state.progress
);

export const selectUploadFileIsInProgress = createSelector(
  selectTaskDetailsState,
  (state) => state.status === UploadStatus.Started && state.progress >= 0
);

export const selectUploadedFileDetails = createSelector(
  selectTaskDetailsState,
  (state) => {
    const uploadFileDetails: RequestedDocumentsModel = {
      id: state.fileId,
      index: state.fileUploadIndex,
      totalFileUploads: state.totalFileUploads,
    };
    return uploadFileDetails;
  }
);

export const selectUserDecisionForTask = createSelector(
  selectTaskDetailsState,
  (state) => state.userDecision
);

export const selectUserComment = createSelector(
  selectTaskDetailsState,
  (state) => state.userComment
);

export const selectHasUploadedFiles = createSelector(
  selectTaskDetailsState,
  (state) =>
    state.totalFileUploads.length > 0 ||
    (state.taskDetails as RequestedDocumentUploadTaskDetails)?.uploadedFiles
      ?.length > 0
);

export const selectNavigationAwayTargetUrl = createSelector(
  selectTaskDetailsState,
  (state) => state.navigationAwayTargetUrl
);
