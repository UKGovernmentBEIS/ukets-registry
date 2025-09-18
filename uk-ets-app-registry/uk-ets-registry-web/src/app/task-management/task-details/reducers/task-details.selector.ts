import { createFeatureSelector, createSelector } from '@ngrx/store';
import {
  taskDetailsFeatureKey,
  TaskDetailsState,
} from './task-details.reducer';
import { selectLoggedInUser } from '@registry-web/auth/auth.selector';
import {
  AccountOpeningTaskDetails,
  PaymentCompleteResponse,
  RequestPaymentTaskDetails,
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
import { convertDateForDatepicker } from '@registry-web/shared/shared.util';

const selectTaskDetailsState = createFeatureSelector<TaskDetailsState>(
  taskDetailsFeatureKey
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

export const selectFileUploadErrors = createSelector(
  selectTaskDetailsState,
  (state) => state.fileUploadErrors
);

export const selectFileUploadErrorMessages = createSelector(
  selectTask,
  selectFileUploadErrors,
  (task, fileUploadErrors) => {
    return (task as RequestedDocumentUploadTaskDetails).documentNames.map(
      (_, index) =>
        fileUploadErrors.find(
          ({ fileUploadIndex }) => fileUploadIndex === index
        )?.errorMessage || null
    );
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

export const selectAmountPaid = createSelector(
  selectTaskDetailsState,
  (state) => {
    if (state.taskDetails.taskType === 'PAYMENT_REQUEST') {
      const taskDetails = state.taskDetails as RequestPaymentTaskDetails;
      if (taskDetails.paymentMethod === 'BACS') {
        return (state.taskDetails as RequestPaymentTaskDetails).amountPaid;
      }
    }
  }
);

export const selectAmountRequested = createSelector(
  selectTaskDetailsState,
  (state) => {
    if (state.taskDetails.taskType === 'PAYMENT_REQUEST') {
      const taskDetails = state.taskDetails as RequestPaymentTaskDetails;
      return (state.taskDetails as RequestPaymentTaskDetails).amountRequested;
    }
  }
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

export const selectTaskDeadline = createSelector(
  selectTaskDetailsState,
  (state) =>
    convertDateForDatepicker(
      new Date(state.deadline || state.taskDetails?.deadline)
    )
);

export const selectTaskDeadlineAsDate = createSelector(
  selectTaskDetailsState,
  (state) => new Date(state.deadline || state.taskDetails?.deadline)
);

export const selectSubmittedApproveTask = createSelector(
  selectTaskDetailsState,
  (state) => state.submittedApproveTask
);

export const selectPaymentUUID = createSelector(
  selectTask,
  (task: TaskDetails) => {
    {
      if (task.taskType === RequestType.PAYMENT_REQUEST) {
        return task.uuid;
      } else {
        return null;
      }
    }
  }
);

export const selectPaymentMethod = createSelector(
  selectTask,
  (task: TaskDetails) => {
    {
      if (task.taskType === RequestType.PAYMENT_REQUEST) {
        return task.paymentMethod;
      } else {
        return null;
      }
    }
  }
);
