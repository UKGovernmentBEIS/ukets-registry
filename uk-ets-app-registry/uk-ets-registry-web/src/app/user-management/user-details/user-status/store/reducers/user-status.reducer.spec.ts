import {
  loadAllowedUserStatusActions,
  setSelectedUserStatusAction,
  clearUserStatus
} from '../actions/user-status.actions';
import { reducer, initialState, UserStatusState } from './index';
import { UserStatusActionOption } from '@user-management/model';
import {setComment} from "@user-management/user-details/user-status/store/actions/user-status.actions";

describe('User Status reducer', () => {
  const userStatusActions: UserStatusActionOption[] = [
    {
      label: 'Validate user',
      value: 'VALIDATE',
      enabled: true,
      newStatus: 'VALIDATED',
      message:
        'A print letter with registry activation code task will be created'
    },
    {
      label: 'Suspend user',
      value: 'SUSPEND',
      enabled: true,
      newStatus: 'SUSPENDED'
    }
  ];

  it('sets the allowed user status actions', () => {
    const beforeFetchLoadAndShowState = reducer(initialState, {} as any);
    expect(beforeFetchLoadAndShowState.allowedUserStatusActions).toBeNull();

    const loadAction = loadAllowedUserStatusActions({
      changeUserStatusActionTypes: userStatusActions
    });
    const afterLoadState = reducer(initialState, loadAction);
    expect(afterLoadState.allowedUserStatusActions).toEqual(userStatusActions);
  });

  it('sets the selected user status action', () => {
    const beforeSetSelectedAccountStatusActionState = reducer(
      initialState,
      {} as any
    );
    expect(
      beforeSetSelectedAccountStatusActionState.userStatusAction
    ).toBeNull();

    const selectedUserStatusAction = setSelectedUserStatusAction({
      userStatusAction: {
        label: 'Validate user',
        value: 'VALIDATE',
        newStatus: 'VALIDATED',
        message:
          'A print letter with registry activation code task will be created'
      }
    });
    const afterSetSelectedUserStatusActionState = reducer(
      initialState,
      selectedUserStatusAction
    );
    expect(afterSetSelectedUserStatusActionState.userStatusAction).toEqual({
      label: 'Validate user',
      value: 'VALIDATE',
      newStatus: 'VALIDATED',
      message:
        'A print letter with registry activation code task will be created'
    });
  });

  it('sets the comment', () => {
    const beforeSetCommentActionState = reducer(initialState, {} as any);
    expect(beforeSetCommentActionState.comment).toBeNull();

    const setCommentAction = setComment({
      comment: 'A reason for blocking'
    });
    const afterSetCommentActionState = reducer(initialState, setCommentAction);
    expect(afterSetCommentActionState.comment).toEqual('A reason for blocking');
  });

  it('clears the state', () => {
    const nonEmptyState: UserStatusState = {
      allowedUserStatusActions: userStatusActions,
      userStatusAction: {
        label: 'Suspend user',
        value: 'SUSPEND',
        newStatus: 'SUSPENDED'
      },
      comment: 'a reason for suspending'
    };
    const beforeClearUserStatusState = reducer(nonEmptyState, {} as any);
    expect(beforeClearUserStatusState.userStatusAction).toBeTruthy();
    expect(beforeClearUserStatusState.allowedUserStatusActions).toBeTruthy();

    const clearUserStatusAction = clearUserStatus();
    const afterClearUserStatusState = reducer(
      initialState,
      clearUserStatusAction
    );
    expect(afterClearUserStatusState.allowedUserStatusActions).toBeNull();
    expect(afterClearUserStatusState.userStatusAction).toBeNull();
  });
});
