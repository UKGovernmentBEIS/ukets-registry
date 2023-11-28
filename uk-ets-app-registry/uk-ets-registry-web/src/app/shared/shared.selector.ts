import { createFeatureSelector, createSelector } from '@ngrx/store';
import { SharedState } from './shared.reducer';
import { IErrorSummary } from '@shared/error-summary/error-summary';
import { MENU_ITEMS, MenuItem } from '@shared/model/navigation-menu';
import { isAdmin, isAuthenticated } from '@registry-web/auth/auth.selector';
import { Option } from './form-controls/uk-select-input/uk-select.model';

const DEFAULT_EMERGENCY_URL_EXPIRATION = 60;

const selectShared = createFeatureSelector<SharedState>('shared');

export const selectCurrentActivatedRoute = createSelector(
  selectShared,
  (state) => state.routeSnapshotUrl
);

export const selectCookiesAccepted = createSelector(
  selectShared,
  (state) => state.acceptCookies
);

export const selectBrowserCookiesEnabled = createSelector(
  selectShared,
  (state) => state.browserCookiesEnabled
);

export const selectAllCountries = createSelector(
  selectShared,
  (state) => state.countries
);

export const selectConfigurationRegistry = createSelector(
  selectShared,
  (state) => state.configurationRegistry
);

export const selectRegistryConfigurationProperty = createSelector(
  selectShared,
  (state, props) => {
    const configuration = state.configurationRegistry.filter(
      (c) => c[props.property] !== undefined
    )[0];
    return configuration !== undefined ? configuration[props.property] : '';
  }
);

export const selectRegistrationConfigurationProperty = createSelector(
  selectShared,
  (state, props) => {
    const configuration = state.configurationRegistration.filter(
      (c) => c[props.property] !== undefined
    )[0];
    return configuration !== undefined ? configuration[props.property] : '';
  }
);

export const selectBooleanConfigurationProperty = createSelector(
  selectShared,
  (state, props) => {
    const configuration = state.configurationRegistry.filter(
      (c) => c[props.property] !== undefined
    )[0];
    return configuration !== undefined &&
      typeof configuration[props.property] == 'string'
      ? (JSON.parse(configuration[props.property]) as boolean)
      : false;
  }
);

// TODO can be replaced with the parameterized selectConfigurationProperty
export const selectEtrAddress = createSelector(selectShared, (state) => {
  const configuration = state.configurationRegistry.filter(
    (c) => c['mail.etrAddress'] !== undefined
  )[0];
  return configuration !== undefined
    ? (configuration['mail.etrAddress'] as string)
    : '';
});

// TODO can be replaced with the parameterized selectConfigurationProperty

/* eslint-disable  */
export const selectResetPasswordUrlExpirationConfiguration = createSelector(
  selectShared,
  (state) => {
    const configuration = state.configurationRegistry.filter(
      (c) => c['business.property.reset.password.url.expiration'] !== undefined
    )[0];
    return configuration !== undefined
      ? (configuration[
          'business.property.reset.password.url.expiration'
        ] as number)
      : Number.NaN;
  }
);

export const selectSessionExpirationNotificationOffset = createSelector(
  selectShared,
  (state) =>
    state.configurationRegistry
      .filter((c) => c['session.expiration.notification.offset'])
      .map((c) => c['session.expiration.notification.offset'])[0] || Number.NaN
);

/* eslint-enable */

export const selectCountryCodes = createSelector(
  selectAllCountries,
  (countries) =>
    countries
      .map((country) => ({
        region: country.key,
        code: country.callingCode,
      }))
      .sort((a, b) => (a.region > b.region ? 1 : -1))
);

export const selectErrorSummary = createSelector(
  selectShared,
  (state) => state.errorSummary
);

export const selectErrorDetailByErrorId = createSelector(
  selectErrorSummary,
  (summary, props: { errorId: string }) => {
    return summary.errors.filter((e) => e.errorId === props.errorId);
  }
);

export const selectErrorDetail = createSelector(
  selectErrorSummary,
  (
    errorSummary: IErrorSummary,
    props: { componentIds: string[]; errorId?: string }
  ) => {
    let found = false;
    if (props.componentIds && errorSummary && errorSummary.errors) {
      let errors = errorSummary.errors;
      if (props.errorId) {
        errors = errors.filter((error) => error.errorId);
      }
      props.componentIds.forEach(
        (componentId) =>
          (found =
            found ||
            errors
              .map((errorDetail) => errorDetail.componentId)
              .includes(componentId))
      );
    }
    return found;
  }
);

export const selectSubMenu = createSelector(
  selectShared,
  (state) => state.subMenu
);
export const selectSubMenuItems = createSelector(
  selectShared,
  (state) => state.subMenuItems
);
export const selectSubMenuActive = createSelector(
  selectShared,
  (state) => state.subMenuActive
);

export const selectGoBackRoute = createSelector(
  selectShared,
  (state) => state.goBackRoute
);

export const selectGoBackToListRoute = createSelector(
  selectShared,
  (state) => state.goBackToListRoute
);

export const selectGoBackNavigationExtras = createSelector(
  selectShared,
  (state) => state.goBackNavigationExtras
);

export const selectGoBackToListNavigationExtras = createSelector(
  selectShared,
  (state) => state.goBackToListNavigationExtras
);

export const selectActiveMenutem = createSelector(
  selectShared,
  (state) => state.activeMenuItem
);

export const selectMenuScopes = createSelector(
  selectShared,
  (state) => state.menuScopes
);

function isMenuItemVisible(menuItem: MenuItem, menuScopes: string[]) {
  // default menu item is visible
  if (!menuItem.protectedScopes) {
    return true;
  }
  return menuItem.protectedScopes?.some((scope) =>
    menuScopes.includes(scope.name)
  );
}

export const selectUserStatus = createSelector(
  selectShared,
  (state) => state.userStatus
);

export const selectSetBackToErrorBasedPath = createSelector(
  selectShared,
  (state) => state.setBackToErrorBasedPath
);

export const selectNavMenu = createSelector(
  selectShared,
  selectMenuScopes,
  isAuthenticated,
  selectUserStatus,
  isAdmin,
  (state, menuScopes, isLoggedIn, userStatus, isAdmin) => {
    const menuItems = MENU_ITEMS(
      selectBooleanConfigurationProperty.projector(state, {
        property: 'account.opening.only.for.registry.administration',
      }),
      userStatus === 'ENROLLED',
      isAdmin
    )
      .filter((menuItem) => (menuItem.checkAuthenticated ? isLoggedIn : true))
      .filter((menuItem) => isMenuItemVisible(menuItem, menuScopes))
      .map((menuItem) => toItemWithDynamicRouterLink(menuItem, menuScopes))
      .map((menuItem) => toItemWithVisibleSubmenus(menuItem, menuScopes));
    return { menuItems };
  }
);

export const selectIsMenuRoute = createSelector(
  selectShared,
  (state) => state.isMenuRoute
);
export const selectVisibleSubMenuItems = createSelector(
  selectNavMenu,
  selectActiveMenutem,
  selectIsMenuRoute,
  (menu, activeMenu, isMenuRoute) => {
    return !isMenuRoute
      ? []
      : menu.menuItems.find(
          (menuItem) => menuItem.activeMenuItem === activeMenu
        )?.subMenus;
  }
);

export const selectAllSubMenuItems = createSelector(
  selectShared,
  selectUserStatus,
  isAdmin,
  (state, userStatus, isAdmin) => {
    return ([] as MenuItem[]).concat(
      ...MENU_ITEMS(
        selectBooleanConfigurationProperty.projector(state, {
          property: 'account.opening.only.for.registry.administration',
        }),
        userStatus === 'ENROLLED',
        isAdmin
      )
        .filter((menuItem) => menuItem.subMenus?.length > 0)
        .map((menuItem) => menuItem.subMenus)
    );
  }
);

export const selectAllMenuItems = createSelector(
  selectShared,
  selectNavMenu,
  selectUserStatus,
  isAdmin,
  (state, menu, userStatus, isAdmin) => {
    return MENU_ITEMS(
      selectBooleanConfigurationProperty.projector(state, {
        property: 'account.opening.only.for.registry.administration',
      }),
      userStatus === 'ENROLLED',
      isAdmin
    );
  }
);

export const allocationYears = createSelector(
  selectShared,
  (state) => state.allocationYears
);

export const allocationYearOptions = createSelector(selectShared, (state) => {
  const options = state.allocationYears.map(
    (year) => ({ label: String(year), value: year } as Option)
  );
  options.unshift({ label: '', value: null });
  return options;
});

function toItemWithDynamicRouterLink(
  menuItem: MenuItem,
  menuScopes: string[]
): MenuItem {
  if (menuItem.routerLink) {
    return menuItem;
  }
  if (!menuItem.routerLinkForScope) {
    console.error(
      `Menu Item ${menuItem.label} is not setup correctly. Please set the routerLinkForScope property or the routerLink property`
    );
    return menuItem;
  }
  const routerLink = menuScopes.includes(menuItem.routerLinkForScope.scope)
    ? menuItem.routerLinkForScope.routerLink
    : menuItem.defaultRouterLink;
  return { ...menuItem, routerLink };
}

/**
 * Keeps submenus that have protected scopes which are available to the user
 * Then removes submenus that should not be visible if certain scopes are available to the user (hideOnScopes).
 */
function toItemWithVisibleSubmenus(
  menuItem: MenuItem,
  menuScopes: string[]
): MenuItem {
  if (!menuItem.subMenus) {
    return menuItem;
  }
  const subMenus = menuItem.subMenus
    .filter((subMenu) =>
      subMenu.protectedScopes?.some((scope) => menuScopes.includes(scope.name))
    )
    .filter(
      (subMenu) =>
        !subMenu.hideOnScopes?.some((scope) => menuScopes.includes(scope))
    );
  return { ...menuItem, subMenus };
}
