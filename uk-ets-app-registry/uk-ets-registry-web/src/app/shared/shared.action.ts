import { createAction, props } from '@ngrx/store';
import { ErrorSummary } from '@shared/error-summary';
import { IUkOfficialCountry } from './countries/country.interface';
import { NavigationExtras, Params } from '@angular/router';
import { Configuration } from './configuration/configuration.interface';
import { HeaderItem } from '@shared/header/header-item.enum';
import { UserStatus } from '@shared/user';
import { GoBackNavigationExtras } from './back-button';

export enum SharedActionTypes {
  CAN_GO_BACK = '[Shared] Can Go Back',
  CLEAR_GO_BACK_ROUTE = '[Shared] Clear Go Back Route',
  CAN_GO_BACK_TO_LIST = '[Shared] Can Go Back to List',
  CLEAR_GO_BACK_TO_LIST_ROUTE = '[Shared] Clear Go Back to List Route',
  SET_GO_BACK_TO_ERROR_BASED_PATH = '[Shared] Set Go Back To Error Route',
  CLEAR_GO_BACK_TO_ERROR_BASED_PATH = '[Shared] Clear Set Go Back To Error Route',
  ERROR_SUMMARY = '[Shared] Error Summary',
  CLEAR_ERROR_SUMMARY = '[Shared] Empty Error Summary',
  LOAD_COUNTRIES_REQUESTED = '[Shared] Load Countries Requested',
  COUNTRIES_LOADED = '[Shared] Countries Loaded',
  LOAD_REGISTRY_CONFIGURATION_REQUESTED = '[Shared] Load Registry Configuration Requested',
  LOAD_REGISTRATION_CONFIGURATION_REQUESTED = '[Shared] Load Registration Configuration Requested',
  LOAD_REGISTRY_CONFIGURATION = '[Shared] Load Registry Configuration',
  LOAD_REGISTRATION_CONFIGURATION = '[Shared] Load Registration Configuration',
  SET_SUBMENU = '[Shared] Set sub-menu',
  SET_SUBMENU_ACTIVE = '[Shared] Set sub-menu active',
  CLEAR_SUBMENU = '[Shared] Clear sub-menu',
  SET_COOKIES_ACCEPTED = '[Shared] Set cookies accepted',
  SET_COOKIES_EXIST = '[Shared] Check cookies',
  SET_ACCEPT_ALL_COOKIES = '[Shared] Accept all cookies',
  RETRIEVE_USER_STATUS = "[Shared] Retrieve a user's status",
  RETRIEVE_USER_STATUS_SUCCESS = "[Shared] Retrieve a user's status success",
  RETRIEVE_USER_STATUS_ERROR = "[Shared] Retrieve a user's status error",
}

export const cookiesExist = createAction(SharedActionTypes.SET_COOKIES_EXIST);

export const acceptAllCookies = createAction(
  SharedActionTypes.SET_ACCEPT_ALL_COOKIES,
  props<{ expirationTime?: string }>()
);

export const navigateToTransactionProposal = createAction(
  '[Shared] Load current activated route',
  props<{ routeSnapshotUrl: string }>()
);

export const cookiesAccepted = createAction(
  SharedActionTypes.SET_COOKIES_ACCEPTED,
  props<{ acceptCookies: boolean; browserCookiesEnabled: boolean }>()
);

export const canGoBack = createAction(
  SharedActionTypes.CAN_GO_BACK,
  props<{ goBackRoute: string; extras?: GoBackNavigationExtras }>()
);

export const clearGoBackRoute = createAction(
  SharedActionTypes.CLEAR_GO_BACK_ROUTE
);

export const canGoBackToList = createAction(
  SharedActionTypes.CAN_GO_BACK_TO_LIST,
  props<{ goBackToListRoute: string; extras?: GoBackNavigationExtras }>()
);

export const clearGoBackToListRoute = createAction(
  SharedActionTypes.CLEAR_GO_BACK_TO_LIST_ROUTE
);

export const setGoBackToErrorBasedPath = createAction(
  SharedActionTypes.SET_GO_BACK_TO_ERROR_BASED_PATH,
  props<{ goBackToErrorBasedPath: string }>()
);

export const clearGoBackToErrorBasedPath = createAction(
  SharedActionTypes.CLEAR_GO_BACK_TO_ERROR_BASED_PATH
);

export const errors = createAction(
  SharedActionTypes.ERROR_SUMMARY,
  props<{ errorSummary: ErrorSummary }>()
);

export const clearErrors = createAction(SharedActionTypes.CLEAR_ERROR_SUMMARY);

export const loadCountriesRequested = createAction(
  SharedActionTypes.LOAD_COUNTRIES_REQUESTED
);
export const loadCountries = createAction(
  SharedActionTypes.COUNTRIES_LOADED,
  props<{ countries: IUkOfficialCountry[] }>()
);
export const loadRegistryConfigurationRequested = createAction(
  SharedActionTypes.LOAD_REGISTRY_CONFIGURATION_REQUESTED
);
export const loadRegistrationConfigurationRequested = createAction(
  SharedActionTypes.LOAD_REGISTRATION_CONFIGURATION_REQUESTED
);
export const loadRegistryConfiguration = createAction(
  SharedActionTypes.LOAD_REGISTRY_CONFIGURATION,
  props<{ configurationRegistry: Configuration[] }>()
);
export const loadRegistrationConfiguration = createAction(
  SharedActionTypes.LOAD_REGISTRATION_CONFIGURATION,
  props<{ configurationRegistration: Configuration[] }>()
);

export interface SetSubMenuAction {
  subMenu: Record<string, string>;
  subMenuItems: { label: string; scopeName?: string }[];
  subMenuActive: string;
}

export const setSubMenu = createAction(
  SharedActionTypes.SET_SUBMENU,
  props<SetSubMenuAction>()
);

export const setSubMenuActive = createAction(
  SharedActionTypes.SET_SUBMENU_ACTIVE,
  props<{
    subMenuActive: string;
  }>()
);
export const clearSubMenu = createAction(SharedActionTypes.CLEAR_SUBMENU);

export const retrieveUserStatus = createAction(
  SharedActionTypes.RETRIEVE_USER_STATUS
);

export const retrieveUserStatusSuccess = createAction(
  SharedActionTypes.RETRIEVE_USER_STATUS_SUCCESS,
  props<{ userStatus: UserStatus }>()
);

export const retrieveUserStatusError = createAction(
  SharedActionTypes.RETRIEVE_USER_STATUS_ERROR,
  props<{ error?: any }>()
);

export const navigateTo = createAction(
  '[Shared] Navigate to',
  props<{ route: string; extras?: NavigationExtras; queryParams?: Params }>()
);

export const loadNavMenuPermissions = createAction(
  '[Shared] Load Navigation Menu Permissions'
);

export const loadNavMenuPermissionsSuccess = createAction(
  '[Shared] Load Navigation Menu Permissions Success',
  props<{ scopes: string[] }>()
);

export const setActiveMenus = createAction(
  '[Shared] Set Active Menus ',
  props<{ topMenuActive: HeaderItem; subMenuActive: string }>()
);

export const navigateToUserProfile = createAction(
  '[Shared] Navigate to user profile with back route',
  props<{ goBackRoute: string; userProfileRoute: string }>()
);

export const loadRequestAllocationData = createAction(
  '[Request Allocation] Load Request Allocation Data'
);

export const loadRequestAllocationDataSuccess = createAction(
  '[Request Allocation] Load Request Allocation Data Success',
  props<{ allocationYears: number[] }>()
);

export const loadRequestAllocationDataFailure = createAction(
  '[Request Allocation] Load Request Allocation Data Failure',
  props<{ error: any }>()
);
