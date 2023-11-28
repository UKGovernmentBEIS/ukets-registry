import { reducer, initialState } from '.';
import { SystemAdministrationState } from './system-administration.reducer';
import { ResetDatabaseResult } from '../../model';
import {
  clearDatabaseResetResult,
  submitResetDatabaseAction,
  submitResetDatabaseSuccess
} from '../actions/system-administration.actions';

describe('System Administration reducer', () => {
  const resetDatabaseResult: ResetDatabaseResult = {
    accountsResult: {
      governmentAccountsCreated: 7,
      sampleAccountsCreated: 4
    },
    usersResult: { usersDeleted: 3, usersCreated: 5 }
  };

  it('sets the DB reset operation result', () => {
    const beforeResetDatabaseState = reducer(initialState, {} as any);
    expect(beforeResetDatabaseState.resetDatabaseResult).toBeNull();

    const submitResetActionSuccess = submitResetDatabaseSuccess({
      result: resetDatabaseResult
    });
    const aftersubmitResetActionSuccessState = reducer(
      initialState,
      submitResetActionSuccess
    );
    expect(aftersubmitResetActionSuccessState.resetDatabaseResult).toEqual(
      resetDatabaseResult
    );
  });

  it('clears the state', () => {
    const nonEmptyState: SystemAdministrationState = {
      resetDatabaseResult
    };

    const beforeClearResetDatabaseState = reducer(nonEmptyState, {} as any);
    expect(beforeClearResetDatabaseState.resetDatabaseResult).toBeTruthy();
    expect(
      beforeClearResetDatabaseState.resetDatabaseResult.accountsResult
    ).toBeTruthy();
    expect(
      beforeClearResetDatabaseState.resetDatabaseResult.usersResult
    ).toBeTruthy();

    const clearDatabaseResetResultAction = clearDatabaseResetResult();
    const afterClearDatabaseResetResultState = reducer(
      initialState,
      clearDatabaseResetResultAction
    );
    expect(afterClearDatabaseResetResultState.resetDatabaseResult).toBeNull();
  });
});
