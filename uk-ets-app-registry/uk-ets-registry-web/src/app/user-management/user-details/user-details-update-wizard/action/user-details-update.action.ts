import { createAction, props } from '@ngrx/store';
import { NavigationExtras, Params } from '@angular/router';
import { UserUpdateDetailsType } from '@user-update/model';
import { IUser } from '@shared/user';

export const navigateTo = createAction(
  '[User Details Update Wizard] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const setRequestUpdateType = createAction(
  '[User Details Update Wizard] Set request update type',
  props<{ updateType: UserUpdateDetailsType }>()
);

export const setPersonalDetailsRequest = createAction(
  '[User Details Update Wizard] Set personal details',
  props<{ userDetails: IUser }>()
);

export const setWorkDetailsRequest = createAction(
  '[User Details Update Wizard] Set work details',
  props<{ userDetails: IUser }>()
);

export const setDeactivationComment = createAction(
  '[User Details Update Wizard] Set deactivation comment',
  props<{ comment: string }>()
);

export const clearUserDetailsUpdateRequest = createAction(
  '[User Details Update Wizard] Clear User Details update request'
);

export const cancelClicked = createAction(
  '[User Details Update Wizard] Cancel clicked',
  props<{ route: string }>()
);

export const cancelUserUpdateRequest = createAction(
  '[User Details Update Wizard] Cancel request update'
);

export const submitUpdateRequest = createAction(
  '[User Details Update Wizard] Submit request update',
  props<{
    userDetails: IUser;
  }>()
);
export const submitDeactivationRequest = createAction(
  '[User Details Update Wizard] Submit deactivation request',
  props<{
    deactivationComment: string;
  }>()
);

export const submitUpdateRequestSuccess = createAction(
  '[User Details Update Wizard] Submit request update success',
  props<{ requestId: string }>()
);

export const fetchCurrentUserDetailsInfo = createAction(
  '[User Details Update Wizard] Fetch current user details info',
  props<{ urid: string }>()
);

export const setCurrentUserDetailsInfoSuccess = createAction(
  '[User Details Update Wizard] Fetch current user details info success',
  props<{ userDetails: IUser }>()
);

export const setNewUserDetailsInfoSuccess = createAction(
  '[User Details Update Wizard] Fetch new user details info success',
  props<{ userDetails: IUser }>()
);

export const loadedFromMyProfilePage = createAction(
  '[User Details Update Wizard] Is loaded from my profile page',
  props<{ isMyProfilePage: boolean }>()
);
