import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import {
  TaskDetailsActions,
  TaskDetailsApiActions,
} from '@task-details/actions';
import {
  AccountOpeningTaskDetails,
  RequestedDocumentsModel,
  RequestedDocumentUploadTaskDetails,
  RequestType,
  TaskCompleteResponse,
  TaskDetails,
  TaskOutcome,
} from '@task-management/model';
import { DomainEvent } from '@shared/model/event';
import { UploadStatus } from '@shared/model/file';
import {
  processSelectedFileError,
  uploadSelectedFileHasStarted,
  uploadSelectedFileInProgress,
} from '@shared/file/actions/file-upload-api.actions';
import {
  clearUploadedDocuments,
  deleteSelectedFileSuccess,
} from '@task-details/actions/task-details.actions';
import { AccountRepresentation } from '@account-opening/account-opening.service';
import { navigationAwayTargetURL } from '../actions/task-details-navigation.actions';

export const taskDetailsFeatureKey = 'taskDetails';

export interface TaskDetailsState {
  taskDetails: TaskDetails;
  // TODO: this is most probably not needed most probably if we refactor
  taskResponse: TaskCompleteResponse;
  taskDetailsLoaded: boolean;
  userDecision: TaskOutcome;
  userComment: string;
  taskHistory: Array<DomainEvent>;
  loading: boolean;
  loadingError?: string;
  status: UploadStatus;
  progress: number | null;
  fileId: number;
  fileUploadIndex: number;
  totalFileUploads: RequestedDocumentsModel[];
  navigationAwayTargetUrl: string;
}

export const initialState: TaskDetailsState = {
  taskResponse: null,
  taskDetails: {
    requestId: null,
    account: null,
    taskType: null,
    initiatorName: null,
    claimantName: null,
    initiatorUrid: null,
    claimantURID: null,
    taskStatus: null,
    requestStatus: null,
    initiatedDate: null,
    currentUserClaimant: null,
    claimedDate: null,
    completedByName: null,
    accountNumber: null,
    accountFullIdentifier: null,
    accountName: null,
    referredUserFirstName: null,
    referredUserLastName: null,
    referredUserURID: null,
    history: null,
    subTasks: null,
    parentTask: null,
    reference: null,
  },
  userDecision: null,
  userComment: null,
  taskDetailsLoaded: false,
  taskHistory: [],
  loading: false,
  status: UploadStatus.Ready,
  progress: null,
  fileId: null,
  fileUploadIndex: null,
  totalFileUploads: [],
  navigationAwayTargetUrl: null,
};

const taskDetailsReducer = createReducer(
  initialState,
  mutableOn(TaskDetailsActions.loadTask, (state, { taskDetails }) => {
    state.taskDetails = taskDetails;
    state.taskDetailsLoaded = true;
    state.fileId = null;
    state.fileUploadIndex = null;
    state.totalFileUploads = [];
    state.userComment = null;
  }),
  mutableOn(
    TaskDetailsActions.loadTaskFromListSuccess,
    (state, { taskDetails }) => {
      state.taskDetails = taskDetails;
    }
  ),
  mutableOn(TaskDetailsActions.fetchTask, (state) => {
    state.taskDetailsLoaded = false;
  }),
  mutableOn(TaskDetailsActions.fetchTaskHistory, (state) => {
    state.loading = true;
  }),
  mutableOn(TaskDetailsActions.fetchTaskHistoryError, (state, { error }) => {
    state.loadingError = error;
    state.loading = false;
  }),
  mutableOn(
    TaskDetailsActions.approveTaskDecision,
    (state, { userDecision }) => {
      state.userDecision = userDecision;
    }
  ),
  mutableOn(TaskDetailsActions.populateAccount, (state, { account }) => {
    if (
      state.taskDetails.taskType === RequestType.ACCOUNT_OPENING_REQUEST &&
      account
    ) {
      let accountRepresentation = (
        state.taskDetails as AccountOpeningTaskDetails
      ).account;
      if (!accountRepresentation) {
        accountRepresentation = new AccountRepresentation();
      }

      accountRepresentation.accountType = account.accountType;
      accountRepresentation.accountHolder = account.accountHolder;
      accountRepresentation.accountHolderContactInfo =
        account.accountHolderContactInfo;
      accountRepresentation.accountDetails = account.accountDetails;
      accountRepresentation.trustedAccountListRules =
        account.trustedAccountListRules;
      accountRepresentation.operator = account.operator;
      accountRepresentation.authorisedRepresentatives =
        account.authorisedRepresentatives;
    }
  }),
  mutableOn(
    TaskDetailsActions.rejectTaskDecision,
    (state, { userDecision }) => {
      state.userDecision = userDecision;
    }
  ),
  mutableOn(
    TaskDetailsActions.sendCommentForNotUploadingDocument,
    (state, { comment }) => {
      state.userComment = comment;
    }
  ),
  mutableOn(
    TaskDetailsActions.setCompleteTask,
    (state, { completeTaskFormInfo }) => {
      state.userComment = completeTaskFormInfo.comment;
    }
  ),
  mutableOn(
    TaskDetailsApiActions.completeTaskWithRejectionSuccess,
    (state, { taskCompleteResponse }) => {
      state.taskResponse = taskCompleteResponse;
      state.taskDetails = taskCompleteResponse.taskDetailsDTO;
      state.userDecision = null;
      state.userComment = null;
    }
  ),
  mutableOn(
    TaskDetailsApiActions.completeTaskWithApprovalSuccess,
    (state, { taskCompleteResponse }) => {
      state.taskResponse = taskCompleteResponse;
      state.taskDetails = taskCompleteResponse.taskDetailsDTO;
      state.userDecision = null;
      state.userComment = null;
    }
  ),
  mutableOn(
    TaskDetailsActions.fetchTaskHistorySuccess,
    (state, { results }) => {
      if (state.taskDetails) {
        state.taskDetails.history = results;
      }
      state.taskHistory = results;
      state.loading = false;
    }
  ),
  mutableOn(TaskDetailsActions.updateTaskSuccess, (state, { result }) => {
    state.taskDetails = result;
  }),
  mutableOn(uploadSelectedFileHasStarted, (state, { status }) => {
    state.progress = 0;
    state.status = status;
  }),
  mutableOn(uploadSelectedFileInProgress, (state, { progress }) => {
    state.progress = progress;
  }),
  mutableOn(
    TaskDetailsActions.uploadSelectedFileSuccess,
    (state, { fileId, fileUploadIndex }) => {
      state.fileId = fileId;
      state.fileUploadIndex = fileUploadIndex;
      state.totalFileUploads.push({
        id: fileId,
        index: fileUploadIndex,
      });
      state.progress = 100;
      state.status = UploadStatus.Completed;
    }
  ),
  mutableOn(processSelectedFileError, (state) => {
    state.status = UploadStatus.Failed;
  }),
  mutableOn(clearUploadedDocuments, (state) => {
    state.fileId = null;
    state.fileUploadIndex = null;
    state.totalFileUploads = [];
    state.status = UploadStatus.Ready;
  }),
  mutableOn(navigationAwayTargetURL, (state, { url }) => {
    state.navigationAwayTargetUrl = url;
  }),
  mutableOn(deleteSelectedFileSuccess, (state, { fileId }) => {
    state.totalFileUploads = state.totalFileUploads.filter(
      (t) => t.id !== fileId
    );
  })
);

export function reducer(state: TaskDetailsState | undefined, action: Action) {
  return taskDetailsReducer(state, action);
}
