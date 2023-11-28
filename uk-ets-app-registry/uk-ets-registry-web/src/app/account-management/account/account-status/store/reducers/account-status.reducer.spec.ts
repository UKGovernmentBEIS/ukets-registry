import {
  loadAllowedAccountStatusActions,
  setSelectedAccountStatusAction,
  setComment,
  clearAccountStatus,
} from '../actions/account-status.actions';
import { reducer, initialState, AccountStatusState } from '.';
import { AccountStatusActionOption } from '@shared/model/account';

describe('Account Status reducer', () => {
  const accountStatusActions: AccountStatusActionOption[] = [
    {
      label: 'Unblock account',
      hint: 'Allow unrestricted transactions for the account.',
      value: 'REMOVE_RESTRICTIONS',
      enabled: true,
      newStatus: 'OPEN',
    },
    {
      label: 'Block account',
      value: 'RESTRICT_SOME_TRANSACTIONS',
      hint:
        'This will not restrict Surrender, Reverse Allocation or Return Excess Allocation transactions for the account.',
      enabled: true,
      newStatus: 'SOME_TRANSACTIONS_RESTRICTED',
      message:
        'You are restricting some transactions of an account that already has all of its transactions restricted',
    },
  ];

  it('sets the allowed account status actions', () => {
    const beforeFetchLoadAndShowState = reducer(initialState, {} as any);
    expect(beforeFetchLoadAndShowState.allowedAccountStatusActions).toBeNull();

    const loadAction = loadAllowedAccountStatusActions({
      changeAccountStatusActionTypes: accountStatusActions,
    });
    const afterLoadState = reducer(initialState, loadAction);
    expect(afterLoadState.allowedAccountStatusActions).toEqual(
      accountStatusActions
    );
  });

  it('sets the selected account status action', () => {
    const beforeSetSelectedAccountStatusActionState = reducer(
      initialState,
      {} as any
    );
    expect(
      beforeSetSelectedAccountStatusActionState.accountStatusAction
    ).toBeNull();

    const selectedAccountStatusAction = setSelectedAccountStatusAction({
      accountStatusAction: {
        label: 'Block account',
        value: 'RESTRICT_SOME_TRANSACTIONS',
        hint:
          'This will not restrict Surrender, Reverse Allocation or Return Excess Allocation transactions for the account.',
        newStatus: 'SOME_TRANSACTIONS_RESTRICTED',
        message:
          'You are restricting some transactions of an account that already has all of its transactions restricted',
      },
    });
    const afterSetSelectedAccountStatusActionState = reducer(
      initialState,
      selectedAccountStatusAction
    );
    expect(
      afterSetSelectedAccountStatusActionState.accountStatusAction
    ).toEqual({
      label: 'Block account',
      hint: 'This will not restrict Surrender, Reverse Allocation or Return Excess Allocation transactions for the account.',
      value: 'RESTRICT_SOME_TRANSACTIONS',
      newStatus: 'SOME_TRANSACTIONS_RESTRICTED',
      message:
        'You are restricting some transactions of an account that already has all of its transactions restricted',
    });
  });

  it('sets the comment', () => {
    const beforeSetCommentActionState = reducer(initialState, {} as any);
    expect(beforeSetCommentActionState.comment).toBeNull();

    const setCommentAction = setComment({
      comment: 'A reason for blocking',
    });
    const afterSetCommentActionState = reducer(initialState, setCommentAction);
    expect(afterSetCommentActionState.comment).toEqual('A reason for blocking');
  });

  it('clears the state', () => {
    const nonEmptyState: AccountStatusState = {
      allowedAccountStatusActions: accountStatusActions,
      accountStatusAction: {
        label: 'Unblock account',
        hint: 'Allow unrestricted transactions for the account.',
        value: 'REMOVE_RESTRICTIONS',
        newStatus: 'OPEN',
      },
      comment: 'A reason for opening',
    };
    const beforeClearAccountStatusState = reducer(nonEmptyState, {} as any);
    expect(beforeClearAccountStatusState.accountStatusAction).toBeTruthy();
    expect(
      beforeClearAccountStatusState.allowedAccountStatusActions
    ).toBeTruthy();
    expect(beforeClearAccountStatusState.comment).toBeTruthy();

    const clearAccountStatusAction = clearAccountStatus();
    const afterClearAccountStatusState = reducer(
      initialState,
      clearAccountStatusAction
    );
    expect(afterClearAccountStatusState.allowedAccountStatusActions).toBeNull();
    expect(afterClearAccountStatusState.accountStatusAction).toBeNull();
    expect(afterClearAccountStatusState.comment).toBeNull();
  });
});
