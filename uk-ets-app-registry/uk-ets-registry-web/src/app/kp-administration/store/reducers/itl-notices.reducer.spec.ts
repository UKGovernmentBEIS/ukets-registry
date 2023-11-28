import { initialState, reducer } from './itl-notices.reducer';
import { Notice } from '@kp-administration/itl-notices/model';
import { RegistryDbEnvirnomentalActivityEnum } from '../../../../../e2e/src/util/test-data/registry-db/registry-level/model/types/registry-db-envirnomental-activity.enum';
import {
  getNoticeSuccess,
  noticesLoaded,
  resetState,
} from '@kp-administration/store';

const mockNotices: Notice[] = [
  {
    createdDate: new Date(),
    id: 1,
    receivedOn: new Date(),
    lastUpdateOn: new Date(),
    actionDueDate: new Date(),
    commitPeriod: 1,
    content: 'Test content',
    lulucfactivity: RegistryDbEnvirnomentalActivityEnum.REVEGETATION,
    messageDate: new Date(),
    status: 'TRANSACTION_PROPOSAL_PENDING',
    type: 'COMMITMENT_PERIOD_RESERVE',
    notificationIdentifier: 1,
    projectNumber: '1',
    targetDate: new Date(),
    targetValue: 1,
    unitBlockIdentifiers: [],
    unitType: 2,
  },
];

describe('ItlNotices Reducer', () => {
  describe('an unknown action', () => {
    it('should return the previous state', () => {
      const action = {} as any;
      const result = reducer(initialState, action);
      expect(result).toBe(initialState);
    });
  });

  describe('notificationsLoaded action', () => {
    it('should retrieve notifications and update state', () => {
      const newState = {
        notices: mockNotices,
        pagination: {
          currentPage: 1,
          totalResults: 12,
          pageSize: 10,
        },
        sortParameters: {
          sortField: 'test',
          sortDirection: 'ASC',
        },
      };
      const action = noticesLoaded(newState);
      const state = reducer(initialState, action);
      expect(state).toEqual({
        ...newState,
        pageParameters: {
          page: 0,
          pageSize: 10,
        },
      });
      expect(state).not.toBe({
        ...newState,
        pageParameters: {
          page: 0,
          pageSize: 10,
        },
      });
    });
  });

  describe('getNoticeSuccess action', () => {
    it('should retrieve notice and update state and then reset state', () => {
      const newState = {
        sortParameters: {
          sortField: 'notificationIdentifier',
          sortDirection: 'ASC',
        },
        notice: mockNotices,
      };
      const action = getNoticeSuccess(newState);
      const state = reducer(initialState, action);
      expect(state).toEqual({
        ...newState,
        pageParameters: {
          page: 0,
          pageSize: 10,
        },
      });
      expect(state).not.toBe({
        ...newState,
        pageParameters: {
          page: 0,
          pageSize: 10,
        },
      });
      const resetAction = resetState();
      const stateAfterReset = reducer(initialState, resetAction);
      expect(stateAfterReset).toEqual(initialState);
      expect(stateAfterReset).toBe(initialState);
    });
  });
});
