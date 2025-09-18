import { Action, createReducer } from '@ngrx/store';
import { mutableOn } from '@shared/mutable-on';
import * as SharedActions from './shared.action';
import { IUkOfficialCountry } from './countries/country.interface';
import { Configuration } from './configuration/configuration.interface';
import { IErrorSummary } from '@shared/error-summary/error-summary';
import { HeaderItem } from '@shared/header/header-item.enum';
import { NavMenu } from '@shared/model/navigation-menu';
import { UserStatus } from '@shared/user';
import { AccountActions } from '@account-management/account/account-details';
import { TransactionDetailsActions } from '@transaction-management/transaction-details/actions';
import * as ReportPublicationActions from '@reports/report-publication/actions/report-publication.actions';
import { GoBackNavigationExtras } from './back-button';

export const sharedFeatureKey = 'shared';

export interface SharedState {
  routeSnapshotUrl: string;
  goBackRoute: string;
  goBackNavigationExtras: GoBackNavigationExtras;
  goBackToListRoute: string;
  goBackToListNavigationExtras: GoBackNavigationExtras;
  errorSummary: IErrorSummary;
  countries: IUkOfficialCountry[];
  configurationRegistry: Configuration[];
  configurationRegistration: Configuration[];
  subMenu: Record<string, string>;
  subMenuItems: { label: string; scopeName?: string }[];
  subMenuActive: string;
  acceptCookies: boolean;
  browserCookiesEnabled: boolean;
  activeMenuItem: HeaderItem;
  navMenu: NavMenu;
  menuScopes: string[];
  isMenuRoute: boolean;
  isTimeoutDialogVisible: boolean;
  userStatus: UserStatus;
  setBackToErrorBasedPath: string;
  allocationYears: number[];
}

export const initialState: SharedState = {
  routeSnapshotUrl: null,
  goBackRoute: null,
  goBackNavigationExtras: { skipLocationChange: false },
  goBackToListRoute: null,
  goBackToListNavigationExtras: { skipLocationChange: false },
  errorSummary: { errors: [] },
  countries: [],
  configurationRegistry: [],
  configurationRegistration: [],
  subMenu: null,
  subMenuItems: null,
  subMenuActive: null,
  acceptCookies: null,
  browserCookiesEnabled: false,
  activeMenuItem: null,
  navMenu: null,
  menuScopes: [],
  isMenuRoute: false,
  isTimeoutDialogVisible: false,
  userStatus: null,
  setBackToErrorBasedPath: null,
  allocationYears: [],
};

const sharedReducer = createReducer(
  initialState,
  mutableOn(SharedActions.canGoBack, (state, { goBackRoute, extras }) => {
    state.goBackRoute = goBackRoute;
    state.goBackNavigationExtras = extras;
  }),
  mutableOn(
    SharedActions.canGoBackToList,
    (state, { goBackToListRoute, extras }) => {
      state.goBackToListRoute = goBackToListRoute;
      state.goBackToListNavigationExtras = extras;
    }
  ),
  mutableOn(SharedActions.errors, (state, { errorSummary }) => {
    if (errorSummary) {
      state.errorSummary = {
        errors: errorSummary.errors,
      };
      window.scrollTo(0, 0);
    }
  }),
  mutableOn(SharedActions.clearErrors, (state) => {
    state.errorSummary = initialState.errorSummary;
  }),
  mutableOn(
    SharedActions.cookiesAccepted,
    (state, { acceptCookies, browserCookiesEnabled }) => {
      state.acceptCookies = acceptCookies;
      state.browserCookiesEnabled = browserCookiesEnabled;
    }
  ),
  mutableOn(SharedActions.loadCountries, (state, { countries }) => {
    state.countries = countries;
  }),
  mutableOn(
    SharedActions.loadRegistryConfiguration,
    (state, { configurationRegistry }) => {
      state.configurationRegistry = configurationRegistry;
    }
  ),
  mutableOn(
    SharedActions.loadRegistrationConfiguration,
    (state, { configurationRegistration }) => {
      state.configurationRegistration = configurationRegistration;
    }
  ),
  mutableOn(
    SharedActions.setSubMenu,
    (state, { subMenu, subMenuItems, subMenuActive }) => {
      state.subMenu = subMenu;
      state.subMenuItems = subMenuItems;
      state.subMenuActive = subMenuActive;
    }
  ),
  mutableOn(SharedActions.setSubMenuActive, (state, { subMenuActive }) => {
    state.subMenuActive = subMenuActive;
  }),
  mutableOn(SharedActions.clearSubMenu, (state) => {
    state.subMenu = null;
    state.subMenuItems = null;
    state.subMenuActive = null;
    state.isMenuRoute = false;
  }),
  mutableOn(SharedActions.clearGoBackRoute, (state) => {
    state.goBackRoute = null;
  }),
  mutableOn(SharedActions.clearGoBackToListRoute, (state) => {
    state.goBackToListRoute = null;
    state.goBackToListNavigationExtras = { skipLocationChange: false };
  }),
  mutableOn(
    SharedActions.setGoBackToErrorBasedPath,
    (state, { goBackToErrorBasedPath }) => {
      state.setBackToErrorBasedPath = goBackToErrorBasedPath;
    }
  ),
  mutableOn(SharedActions.clearGoBackToErrorBasedPath, (state) => {
    state.setBackToErrorBasedPath = initialState.setBackToErrorBasedPath;
  }),
  mutableOn(
    SharedActions.loadNavMenuPermissionsSuccess,
    (state, { scopes }) => {
      state.menuScopes = scopes;
    }
  ),
  mutableOn(
    SharedActions.setActiveMenus,
    (state, { topMenuActive, subMenuActive }) => {
      state.activeMenuItem = topMenuActive;
      state.subMenuActive = subMenuActive;
      state.isMenuRoute = true;
    }
  ),
  mutableOn(
    SharedActions.navigateToTransactionProposal,
    (state, { routeSnapshotUrl }) => {
      state.routeSnapshotUrl = routeSnapshotUrl;
    }
  ),
  mutableOn(
    AccountActions.prepareTransactionStateForReturnOfExcess,
    (state, { routeSnapshotUrl }) => {
      state.routeSnapshotUrl = routeSnapshotUrl;
    }
  ),
  mutableOn(
    TransactionDetailsActions.prepareTransactionProposalStateForReversal,
    (state, { routeSnapshotUrl }) => {
      state.routeSnapshotUrl = routeSnapshotUrl;
    }
  ),
  mutableOn(
    ReportPublicationActions.prepareNavigationLinksForWizards,
    (state, { routeSnapshotUrl }) => {
      state.routeSnapshotUrl = routeSnapshotUrl;
    }
  ),
  mutableOn(
    SharedActions.loadRequestAllocationDataSuccess,
    (state, { allocationYears }) => {
      state.allocationYears = allocationYears;
    }
  )
);

export function reducer(state: SharedState | undefined, action: Action) {
  return sharedReducer(state, action);
}
