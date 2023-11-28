import { KeycloakUser, UserStatus } from '@shared/user';
import { createAction, props } from '@ngrx/store';
import { ChangeUserStatusActionTypes } from '@user-management/user-details/user-status/store/actions/user-status.actions';

export type UserStatusAction = 'VALIDATE' | 'SUSPEND' | 'RESTORE' | 'UNEROLL';

export interface UserStatusActionOption {
  label: string;
  value: UserStatusAction;
  enabled: true;
  newStatus: UserStatus;
  message?: string;
}

export type UserStatusActionState = Omit<UserStatusActionOption, 'enabled'>;

export interface UserStatusRequest {
  urid: string;
  status: UserStatus;
  comment: string;
}

export interface UserStatusChangedResult {
  userStatus: UserStatus;
  requestId?: string;
  iamIdentifier: string;
  user?: KeycloakUser;
}

export const setComment = createAction(
  ChangeUserStatusActionTypes.SET_COMMENT,
  props<{ comment: string }>()
);
