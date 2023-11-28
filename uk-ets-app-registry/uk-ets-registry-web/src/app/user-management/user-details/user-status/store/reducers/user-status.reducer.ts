import {
  UserStatusActionOption,
  UserStatusActionState,
} from '@user-management/model';
import { UserStatusActions } from '../actions';
import { mutableOn } from '@shared/mutable-on';
import { createReducer, Action } from '@ngrx/store';

export const userStatusFeatureKey = 'user-status';

export interface UserStatusState {
  allowedUserStatusActions: UserStatusActionOption[];
  userStatusAction: UserStatusActionState;
  comment: string;
  userSuspendedByTheSystem: boolean;
}

export const initialState: UserStatusState = {
  allowedUserStatusActions: null,
  userStatusAction: null,
  comment: null,
  userSuspendedByTheSystem: false,
};

const userStatusReducer = createReducer(
  initialState,
  mutableOn(
    UserStatusActions.loadAllowedUserStatusActions,
    (state, { changeUserStatusActionTypes }) => {
      state.allowedUserStatusActions = changeUserStatusActionTypes;
    }
  ),
  mutableOn(
    UserStatusActions.setSelectedUserStatusAction,
    (state, { userStatusAction }) => {
      state.userStatusAction = userStatusAction;
    }
  ),
  mutableOn(UserStatusActions.setComment, (state, { comment }) => {
    state.comment = comment;
  }),
  mutableOn(
    UserStatusActions.checkIfUserSuspendedByTheSystemSuccess,
    (state, { userSuspendedByTheSystem }) => {
      state.userSuspendedByTheSystem = userSuspendedByTheSystem;
    }
  ),
  mutableOn(UserStatusActions.clearUserStatus, (state) => {
    resetState(state);
  })
);

export function reducer(state: UserStatusState | undefined, action: Action) {
  return userStatusReducer(state, action);
}

function resetState(state) {
  state.allowedUserStatusActions = initialState.allowedUserStatusActions;
  state.userStatusAction = initialState.userStatusAction;
  state.comment = initialState.comment;
  state.userSuspendedByTheSystem = initialState.userSuspendedByTheSystem;
}
