import { createAction, props } from '@ngrx/store';
import {
  UserStatusActionOption,
  UserStatusActionState,
} from '@user-management/model';
import { KeycloakUser, UserStatus } from '@shared/user';

export enum ChangeUserStatusActionTypes {
  FETCH_LOAD_AND_SHOW_ALLOWED_USER_STATUS_ACTIONS = '[User Status] Fetch, load and show allowed change User status actions',
  FETCH_ALLOWED_USER_STATUS_ACTIONS_SUCCESS = '[User Status] Successful fetch of allowed change User status actions',
  LOAD_ALLOWED_USER_STATUS_ACTIONS = '[User Status] Load allowed change User status actions',
  SET_SELECTED_USER_STATUS_ACTION_AND_NAVIGATE_TO_CONFIRM = '[User Status] Select User status action & navigate to confirm',
  SET_SELECTED_USER_STATUS_ACTION = '[User Status] Set selected User status action',
  SET_COMMENT = '[User Status] Set comment',
  SUBMIT_USER_STATUS_ACTION = '[User Status] Submit Change User Status',
  SUBMIT_USER_STATUS_ACTION_SUCCESS = '[User Status] Submit Change User Status Success',
  NAVIGATE_TO = '[User Status] Navigate to',
  CANCEL_USER_STATUS = '[User Status] Cancel change User status',
  CLEAR_USER_STATUS = '[User Status] Clear change User status',
  CHECK_USER_SUSPENDED_BY_THE_SYSTEM_ACTION = '[User Status] Check if User suspended by the system',
  CHECK_USER_SUSPENDED_BY_THE_SYSTEM_ACTION_SUCCESS = '[User Status] Successful fetch of the flag that indicates if the user was suspended by the system',
  CHECK_USER_SUSPENDED_BY_THE_SYSTEM_ACTION_ERROR = '[User Status] Failed to fetch the flag that indicates if the user was suspended by the system',
}

export const fetchLoadAndShowAllowedUserStatusActions = createAction(
  ChangeUserStatusActionTypes.FETCH_LOAD_AND_SHOW_ALLOWED_USER_STATUS_ACTIONS,
  props<{ urid: string }>()
);

export const fetchAllowedUserStatusActionsSuccess = createAction(
  ChangeUserStatusActionTypes.FETCH_ALLOWED_USER_STATUS_ACTIONS_SUCCESS,
  props<{ changeUserStatusActionTypes: UserStatusActionOption[] }>()
);

export const loadAllowedUserStatusActions = createAction(
  ChangeUserStatusActionTypes.LOAD_ALLOWED_USER_STATUS_ACTIONS,
  props<{ changeUserStatusActionTypes: UserStatusActionOption[] }>()
);

export const setSelectedUserStatusAndNavigateToConfirmAction = createAction(
  ChangeUserStatusActionTypes.SET_SELECTED_USER_STATUS_ACTION_AND_NAVIGATE_TO_CONFIRM,
  props<{ userStatusAction: UserStatusActionState }>()
);

export const setSelectedUserStatusAction = createAction(
  ChangeUserStatusActionTypes.SET_SELECTED_USER_STATUS_ACTION,
  props<{ userStatusAction: UserStatusActionState }>()
);

export const setComment = createAction(
  ChangeUserStatusActionTypes.SET_COMMENT,
  props<{ comment: string }>()
);

export const submitUserStatusAction = createAction(
  ChangeUserStatusActionTypes.SUBMIT_USER_STATUS_ACTION,
  props<{ comment: string }>()
);

export const submitUserStatusActionSuccess = createAction(
  ChangeUserStatusActionTypes.SUBMIT_USER_STATUS_ACTION_SUCCESS,
  props<{ newStatus: UserStatus; comment: string; user: KeycloakUser }>()
);

export const cancelUserStatus = createAction(
  ChangeUserStatusActionTypes.CANCEL_USER_STATUS
);

export const clearUserStatus = createAction(
  ChangeUserStatusActionTypes.CLEAR_USER_STATUS
);

export const navigateTo = createAction(
  ChangeUserStatusActionTypes.NAVIGATE_TO,
  props<{ route: string }>()
);

export const checkIfUserSuspendedByTheSystem = createAction(
  ChangeUserStatusActionTypes.CHECK_USER_SUSPENDED_BY_THE_SYSTEM_ACTION
);

export const checkIfUserSuspendedByTheSystemSuccess = createAction(
  ChangeUserStatusActionTypes.CHECK_USER_SUSPENDED_BY_THE_SYSTEM_ACTION_SUCCESS,
  props<{ userSuspendedByTheSystem: boolean }>()
);

export const checkIfUserSuspendedByTheSystemError = createAction(
  ChangeUserStatusActionTypes.CHECK_USER_SUSPENDED_BY_THE_SYSTEM_ACTION_ERROR,
  props<{ error: any }>()
);
