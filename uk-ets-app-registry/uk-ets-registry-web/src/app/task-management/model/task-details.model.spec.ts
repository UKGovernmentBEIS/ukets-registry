import { TaskDetailsBase } from '@task-management/model/task-details.model';

export const taskDetailsBase: TaskDetailsBase = {
  requestId: null,
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
  accountNumber: '',
  accountFullIdentifier: null,
  accountName: null,
  referredUserFirstName: null,
  referredUserLastName: null,
  referredUserURID: null,
  history: null,
  subTasks: [],
  parentTask: null,
};

describe('Task Detail Base object initialization', () => {
  test('checks if the taskDetailsBase has been initialized with the default values', () => {
    expect(taskDetailsBase.requestId).toBe(null);
    expect(taskDetailsBase.initiatorName).toBe(null);
    expect(taskDetailsBase.initiatorUrid).toBe(null);
    expect(taskDetailsBase.claimantName).toBe(null);
    expect(taskDetailsBase.subTasks).toEqual([]);
    expect(taskDetailsBase.claimantURID).toBe(null);
    expect(taskDetailsBase.parentTask).toBe(null);
    expect(taskDetailsBase.taskStatus).toBe(null);
    expect(taskDetailsBase.requestStatus).toBe(null);
    expect(taskDetailsBase.initiatedDate).toBe(null);
    expect(taskDetailsBase.claimedDate).toBe(null);
    expect(taskDetailsBase.currentUserClaimant).toBe(null);
    expect(taskDetailsBase.accountName).toBe(null);
  });
});
