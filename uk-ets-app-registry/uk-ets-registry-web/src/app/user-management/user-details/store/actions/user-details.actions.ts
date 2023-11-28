import { createAction, props } from '@ngrx/store';
import { KeycloakUser } from '@shared/user/keycloak-user';
import { ArInAccount, EnrolmentKey } from '@user-management/user-details/model';
import { DomainEvent } from '@shared/model/event';
import { FileDetails } from '@shared/model/file/file-details.model';
import { UserUpdateDetailsType } from '@user-update/model';
import { NavigationExtras, Params } from '@angular/router';

export const prepareNavigationToUserDetails = createAction(
  `[User Header Guard]
  Prepare the navigation to user details by pre-fetching user data
  and by setting up the 'can go back' url`,
  props<{ urid: string; backRoute: string }>()
);

export const fetchAUserDetailsUpdatePendingApproval = createAction(
  '[User Details] Fetch User Details Update Pending Approval',
  props<{ urid: string; updateType: UserUpdateDetailsType }>()
);

export const fetchAUserDetailsUpdatePendingApprovalSuccess = createAction(
  '[User Details] Fetch User Details Update Pending Approval Success',
  props<{ hasUserDetailsUpdatePendingApproval: boolean }>()
);

export const retrieveUser = createAction(
  '[User Details Effect] Retrieve a user',
  props<{ urid: string }>()
);

export const retrieveUserSuccess = createAction(
  '[User Details] Retrieve a user success',
  props<{ user: KeycloakUser }>()
);

export const retrieveUserError = createAction(
  '[User Details] Retrieve a user error',
  props<{ error?: any }>()
);

export const retrieveUserDetailsOpenTaskError = createAction(
  '[User Details] Retrieve a user details open task error',
  props<{ error?: any }>()
);

export const retrieveARsInAccount = createAction(
  '[User Details] Retrieve all the accounts that the user is authorize representative',
  props<{ urid: string }>()
);

export const retrieveARsInAccountSuccess = createAction(
  '[User Details] Retrieve all the accounts that the user is authorize representative success',
  props<{ ARs: ArInAccount[] }>()
);

export const retrieveARsInAccountError = createAction(
  '[User Details] Retrieve all the accounts that the user is authorize representative error',
  props<{ error?: any }>()
);

export const retrieveUserHistory = createAction(
  '[User Details] Retrieve user history',
  props<{ urid: string }>()
);

export const retrieveUserHistorySuccess = createAction(
  '[User Details] Retrieve user history success',
  props<{ userHistory: DomainEvent[] }>()
);

export const retrieveUserHistoryError = createAction(
  '[User Details] Retrieve user history error',
  props<{ error?: any }>()
);

export const retrieveUserFiles = createAction(
  '[User Details] Retrieve user files',
  props<{ urid: string }>()
);

export const retrieveUserFilesSuccess = createAction(
  '[User Details] Retrieve user files success',
  props<{ userFiles: FileDetails[] }>()
);

export const retrieveUserFilesError = createAction(
  '[User Details] Retrieve user files error',
  props<{ error?: any }>()
);

export const fetchUserFile = createAction(
  '[User Details] Fetch user file',
  props<{
    fileId: number;
  }>()
);

export const retrieveEnrolmentKeyDetails = createAction(
  '[User Details] Retrieve enrolment key details',
  props<{ urid: string }>()
);

export const retrieveEnrolmentKeyDetailsSuccess = createAction(
  '[User Details] Retrieve enrolment key details success',
  props<{ enrolmentKeyDetails: EnrolmentKey }>()
);

export const retrieveEnrolmentKeyDetailsError = createAction(
  '[User Details] Retrieve enrolment key details error',
  props<{ error?: any }>()
);

export const navigateTo = createAction(
  '[User Details] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);
