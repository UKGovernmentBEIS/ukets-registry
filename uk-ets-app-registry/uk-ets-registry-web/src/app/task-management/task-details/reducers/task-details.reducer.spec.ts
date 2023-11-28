import {
  reducer,
  TaskDetailsState,
} from '@task-details/reducers/task-details.reducer';
import { TaskDetailsActions } from '@task-details/actions';
import { RequestType, TaskDetails } from '@task-management/model';
import { verifyBeforeAndAfterActionDispatched } from '../../../../testing/helpers/reducer.test.helper';
import { UploadStatus } from '@shared/model/file';

describe('Task Details reducer', () => {
  const taskDetails: TaskDetails = {
    taskType: RequestType.ACCOUNT_OPENING_REQUEST,
    account: null,
    requestId: 'test-request-id',
    initiatorName: null,
    initiatorUrid: null,
    claimantName: null,
    claimantURID: null,
    taskStatus: null,
    requestStatus: null,
    initiatedDate: null,
    claimedDate: null,
    currentUserClaimant: null,
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
  };

  const fetchedTaskDetails: TaskDetails = {
    taskType: RequestType.ACCOUNT_OPENING_REQUEST,
    account: null,
    requestId: 'fetched-task-details-test-request-id',
    initiatorName: null,
    initiatorUrid: null,
    claimantName: null,
    claimantURID: null,
    taskStatus: null,
    requestStatus: null,
    initiatedDate: null,
    claimedDate: null,
    currentUserClaimant: null,
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
  };

  it('After fetch task details action have been dispatched, the task details loaded flag should be set to false', () => {
    verifyBeforeAndAfterActionDispatched<TaskDetailsState>(
      reducer,
      {
        taskDetails,
        taskResponse: null,
        userComment: null,
        userDecision: null,
        taskDetailsLoaded: true,
        taskHistory: [],
        loading: false,
        status: UploadStatus.Ready,
        progress: null,
        fileId: null,
        fileUploadIndex: null,
        totalFileUploads: [],
      },
      (state) => {
        expect(state.taskDetailsLoaded).toBeTruthy();
      },
      TaskDetailsActions.fetchTask({ taskId: 'a task id' }),
      (state) => {
        expect(state.taskDetailsLoaded).toBeFalsy();
      }
    );
  });

  it(`After load task details action have been dispatched the task details loaded flag should be set to true
  and the fetched task should be loaded to store.`, () => {
    verifyBeforeAndAfterActionDispatched<TaskDetailsState>(
      reducer,
      {
        taskDetails,
        userDecision: null,
        userComment: null,
        taskResponse: null,
        taskDetailsLoaded: false,
        taskHistory: [],
        loading: false,
        status: UploadStatus.Ready,
        progress: null,
        fileId: null,
        fileUploadIndex: null,
        totalFileUploads: [],
      },
      (state) => {
        expect(state.taskDetailsLoaded).toBeFalsy();
        expect(state.taskDetails).toBe(taskDetails);
      },
      TaskDetailsActions.loadTask({ taskDetails: fetchedTaskDetails }),
      (state) => {
        expect(state.taskDetailsLoaded).toBeTruthy();
        expect(state.taskDetails).toBe(fetchedTaskDetails);
      }
    );
  });
});
