import { HeaderItem } from '@shared/header/header-item.enum';
import { SearchMode } from '@shared/resolvers/search.resolver';

export enum MENU_ROUTES {
  DASHBOARD = '/dashboard',
  RECOVERY = '/dashboard/recovery',
  UNDER_CONSTRUCTION = '/under-construction',

  ACCOUNT_SEARCH = '/account-list',
  ACCOUNT_OPENING = '/account-opening',

  BULK_AR = '/bulk-ar',

  TRANSACTION_SEARCH = '/transaction-list',

  KP_ADMINISTRATION = '/kpadministration',
  KP_ADMINISTRATION_ISSUE_UNITS = '/kpadministration/issuekpunits',
  KP_ADMINISTRATION_ITL_MESSAGES = '/kpadministration/itl-message-list',
  KP_ADMINISTRATION_ITL_NOTIFICATIONS = '/kpadministration/itl-notices',
  KP_ADMINISTRATION_ITL_RECONCILIATION = '/kpadministration/itl-reconciliation',

  TASK_LIST = '/task-list',

  USER_ADMINISTRATION = '/user-list',

  ETS_ADMINISTRATION = '/ets-administration',
  ETS_ADMINISTRATION_ISSUE_ALLOWANCES = '/ets-administration/issue-allowances',
  ETS_ADMINISTRATION_ISSUANCE_ALLOCATION_STATUS = '/ets-administration/issuance-allocation-status',
  ETS_ADMINISTRATION_REQUEST_ALLOCATION = '/ets-administration/request-allocation',
  ETS_ADMINISTRATION_UPLOAD_ALLOCATION_TABLE = '/ets-administration/allocation-table',
  ETS_ADMINISTRATION_UPLOAD_EMISSIONS_TABLE = '/ets-administration/emissions-table',
  ETS_ADMINISTRATION_RECONCILIATION = '/ets-administration/reconciliation',
  ETS_ADMINISTRATION_RECALCULATE_COMPLIANCE_STATUS = '/ets-administration/recalculate-compliance-status',
  ETS_ADMINISTRATION_VIEW_ALLOCATION_JOB_STATUS = '/ets-administration/view-allocation-job-status',

  REGISTRY_ADMINISTRATION = '/registry-administration',
  NOTIFICATIONS = '/notifications',

  REPORTS = '/reports',
  REPORTS_DOWNLOADS = '/reports/downloads',
  REPORTS_STANDARD = '/reports/standard',
  ETS_REPORT_PUBLICATION = '/reports/ets-report-publication',
  KP_REPORT_PUBLICATION = '/reports/kp-report-publication',
  ABOUT = '/about',
  DOCUMENTS = '/documents',
}

export enum MENU_SCOPES {
  TRANSACTION_MENU_ITEM_VIEW = 'urn:uk-ets-registry-api:page:dashboard:transactionMenuItem:view',
  ADMIN_MENU_ITEM_VIEW = 'urn:uk-ets-registry-api:page:dashboard:adminMenuItem:view',
  ISSUE_ALLOWANCES_READ = 'urn:uk-ets-registry-api:issue-allowances:read',
  SEARCH_ACCOUNT_MENU_ITEM_VIEW = 'urn:uk-ets-registry-api:page:dashboard:searchAccountMenuItem:view',
  ACCOUNT_CREATE = 'urn:uk-ets-registry-api:account:create',
  ACTION_FOR_SENIOR_AND_JUNIOR_ADMIN_ONLY = 'urn:uk-ets-registry-api:actionForSeniorAndJuniorAdmin',
  ISSUE_KP_UNITS_READ = 'urn:uk-ets-registry-api:page:issuekpunits:read',
  ITL_MESSAGE_READ = 'urn:uk-ets-registry-api:page:itl-message:read',
  ITL_NOTICE_READ = 'urn:uk-ets-registry-api:page:itl-notice:read',
  REQUEST_ALLOCATION_WIZARD_RESOURCE = 'Request Allocation Wizard Resource',
  RECONCILIATION_ADMINISTRATION = 'Reconciliation administration',
  NOTIFICATION_ADMINISTRATION = 'Notifications administrator',
  REGISTRY_ADMIN_MENU_ITEM_VIEW = 'urn:uk-ets-registry-api:page:dashboard:registryAdminMenuItem:view',
  ITL_RECONCILIATION_READ = 'urn:uk-ets-registry-api:page:itl-reconciliation:read',
  BULK_AR_UPLOAD_WRITE = 'urn:uk-ets-registry-api:bulk-ar-upload:write',
  ACTION_FOR_READ_ONLY_ADMIN = 'urn:uk-ets-registry-api:actionForReadOnlyAdmins',
  REPORTS_USER = 'urn:uk-ets-reports-api:anyReportingAction',
  EMISSIONS_TABLE_WRITE = 'urn:uk-ets-registry-api:emissions-table:write',
  STANDARD_REPORTS_MENU_ITEM_VIEW = 'urn:uk-ets-registry-api:page:dashboard:standardReportsMenuItem:view',
  RECALCULATE_COMPLIANCE_STATUS = 'urn:uk-ets-registry-api:compliance-status:write',
}

export interface NavMenu {
  menuItems?: MenuItem[];
}

export type MenuItem = {
  label: string;
  routerLink?: string;
  checkAuthenticated?: boolean; // if this is set, the menu is only visible for authenticated users
  protectedScopes?: MenuScope[]; // if this is set, the menu is only visible for users that have AT LEAST one of the scopes/permissions
  activeMenuItem?: HeaderItem;
  queryParams?: { [k: string]: any };
  subMenus?: MenuItem[];
} & MenuItemWithConditionalRouterLink &
  SubMenuItemHiddenOnScopes;
/**
 * For specific cases the menu item's router link is dynamically set depending on a specific scope.
 */
interface MenuItemWithConditionalRouterLink {
  defaultRouterLink?: string;
  routerLinkForScope?: RouterLinkScope;
}
/**
 * If the user has the 'scope', the correct link should be 'routerLink'
 */
interface RouterLinkScope {
  scope: string;
  routerLink: string;
}
/**
 * For specific cases the submenu item must be hidden dynamically depending on specific scopes being present.
 * This is the opposite of the protectedScopes property and takes precedence over it.
 *
 */
interface SubMenuItemHiddenOnScopes {
  hideOnScopes?: string[];
}
/**
 * Used to differentiate between scopes and permissions, when making the backend call for scope.
 * The scope/permission can be linked to a different keycloak client (the default being 'uk-ets-registry-api').
 * In that case the associated clientId can be defined here.
 */
export interface MenuScope {
  name: string;
  isPermission?: boolean;
  clientId?: string;
}

/**
 * Gets the permissions whether accounts central menu item is going to be visible to specific types of users
 *
 * If the user is an admin, we allow him always to open a new account independent to allowAccountOpeningOnlyToRAs
 * (in one case allowAccountOpeningOnlyToRAs and isAdmin are true. In another case isAdmin is true and allowAccountOpeningOnlyToRAs
 * is not, in which case the null permissions are returned)
 *
 * Null permissions mean that everyone has access to Accounts menu
 *
 * in case of a non admin unenrolled user and if allowAccountOpeningOnlyToRAs is true, account opening is not allowed
 * if allowAccountOpeningOnlyToRAs is false, we assign null permissions
 *
 * Only in the case when allowAccountOpeningOnlyToRAs is true for a non-admin enrolled user we allow the view of account items in a list
 * if allowAccountOpeningOnlyToRAs is false then null permissions are assigned
 *
 *
 * @param isAdmin checks whether the current connected user is an admin
 * @param isEnrolled checks whether the current connected user is ENROLLED
 * @param allowAccountOpeningOnlyToRAs the value of the env variable which decides whether only RAs are going to have the ability
 *      of opening an account or also everyone else
 * @return whether the accounts menu is going to be visible to users or not
 */
function getProtectedScopesForAccountOperations(
  isAdmin: boolean,
  isEnrolled: boolean,
  allowAccountOpeningOnlyToRAs: boolean
) {
  if ((isAdmin || (!isAdmin && !isEnrolled)) && allowAccountOpeningOnlyToRAs) {
    return [
      {
        name: MENU_SCOPES.ACTION_FOR_SENIOR_AND_JUNIOR_ADMIN_ONLY,
      },
    ];
  } else {
    if (!isAdmin && isEnrolled && allowAccountOpeningOnlyToRAs) {
      return [{ name: MENU_SCOPES.SEARCH_ACCOUNT_MENU_ITEM_VIEW }];
    } else {
      return null;
    }
  }
}

/**
 * Default navigation menu. The actual menu to be rendered is constructed dynamically in the store
 * (See shared.selector --> selectNavMenu)
 */
export const MENU_ITEMS = function (
  allowAccountOpeningOnlyToRAs: boolean,
  isEnrolled: boolean,
  isAdmin: boolean
): MenuItem[] {
  return [
    {
      label: 'Home',
      routerLink: MENU_ROUTES.DASHBOARD,
      checkAuthenticated: true,
      activeMenuItem: HeaderItem.HOME,
    },
    {
      label: 'Tasks',
      routerLink: MENU_ROUTES.TASK_LIST,
      checkAuthenticated: true,
      activeMenuItem: HeaderItem.TASKS,
      queryParams: { mode: SearchMode.INITIAL_LOAD },
    },
    {
      label: 'Accounts',
      checkAuthenticated: true,
      activeMenuItem: HeaderItem.ACCOUNTS,
      defaultRouterLink: MENU_ROUTES.ACCOUNT_OPENING,
      queryParams: { mode: SearchMode.INITIAL_LOAD },
      routerLinkForScope: {
        scope: MENU_SCOPES.SEARCH_ACCOUNT_MENU_ITEM_VIEW,
        routerLink: MENU_ROUTES.ACCOUNT_SEARCH,
      },
      protectedScopes: getProtectedScopesForAccountOperations(
        isAdmin,
        isEnrolled,
        allowAccountOpeningOnlyToRAs
      ),
      subMenus: [
        {
          label: 'Search accounts',
          routerLink: MENU_ROUTES.ACCOUNT_SEARCH,
          protectedScopes: [
            { name: MENU_SCOPES.SEARCH_ACCOUNT_MENU_ITEM_VIEW },
          ],
        },
        {
          label: 'Request account',
          routerLink: MENU_ROUTES.ACCOUNT_OPENING,
          protectedScopes: [
            allowAccountOpeningOnlyToRAs
              ? { name: MENU_SCOPES.ACTION_FOR_SENIOR_AND_JUNIOR_ADMIN_ONLY }
              : { name: MENU_SCOPES.ACCOUNT_CREATE },
          ],
        },
        {
          label: 'Bulk AR upload',
          routerLink: MENU_ROUTES.BULK_AR,
          protectedScopes: [{ name: MENU_SCOPES.BULK_AR_UPLOAD_WRITE }],
        },
      ],
    },
    {
      label: 'Transactions',
      routerLink: MENU_ROUTES.TRANSACTION_SEARCH,
      protectedScopes: [{ name: MENU_SCOPES.TRANSACTION_MENU_ITEM_VIEW }],
      activeMenuItem: HeaderItem.TRANSACTIONS,
      queryParams: { mode: SearchMode.INITIAL_LOAD },
    },
    {
      label: 'User Administration',
      routerLink: MENU_ROUTES.USER_ADMINISTRATION,
      protectedScopes: [{ name: MENU_SCOPES.ADMIN_MENU_ITEM_VIEW }],
      activeMenuItem: HeaderItem.USER_ADMIN,
      queryParams: { mode: SearchMode.INITIAL_LOAD },
    },
    {
      label: 'ETS Administration',
      routerLink: MENU_ROUTES.ETS_ADMINISTRATION,
      protectedScopes: [
        { name: MENU_SCOPES.ISSUE_ALLOWANCES_READ },
        { name: MENU_SCOPES.ACTION_FOR_READ_ONLY_ADMIN },
        {
          name: MENU_SCOPES.REQUEST_ALLOCATION_WIZARD_RESOURCE,
          isPermission: true,
        },
        { name: MENU_SCOPES.RECONCILIATION_ADMINISTRATION, isPermission: true },
      ],
      activeMenuItem: HeaderItem.ETS_ADMIN,
      subMenus: [
        {
          label: 'Propose to issue UK allowances',
          routerLink: MENU_ROUTES.ETS_ADMINISTRATION_ISSUE_ALLOWANCES,
          protectedScopes: [{ name: MENU_SCOPES.ISSUE_ALLOWANCES_READ }],
        },
        {
          label: 'Request allocation of UK allowances',
          routerLink: MENU_ROUTES.ETS_ADMINISTRATION_REQUEST_ALLOCATION,
          protectedScopes: [
            {
              name: MENU_SCOPES.REQUEST_ALLOCATION_WIZARD_RESOURCE,
              isPermission: true,
            },
          ],
          hideOnScopes: [MENU_SCOPES.ISSUE_ALLOWANCES_READ],
        },
        {
          label: 'View issuance and allocation status',
          routerLink: MENU_ROUTES.ETS_ADMINISTRATION_ISSUANCE_ALLOCATION_STATUS,
          protectedScopes: [
            { name: MENU_SCOPES.ISSUE_ALLOWANCES_READ },
            { name: MENU_SCOPES.ACTION_FOR_READ_ONLY_ADMIN },
            {
              name: MENU_SCOPES.REQUEST_ALLOCATION_WIZARD_RESOURCE,
              isPermission: true,
            },
          ],
        },
        {
          label: 'Upload allocation table',
          routerLink: MENU_ROUTES.ETS_ADMINISTRATION_UPLOAD_ALLOCATION_TABLE,
          protectedScopes: [{ name: MENU_SCOPES.ISSUE_ALLOWANCES_READ }],
        },
        {
          label: 'Reconciliation Administration',
          routerLink: MENU_ROUTES.ETS_ADMINISTRATION_RECONCILIATION,
          protectedScopes: [
            {
              name: MENU_SCOPES.RECONCILIATION_ADMINISTRATION,
              isPermission: true,
            },
          ],
        },
        {
          label: 'Upload emissions table',
          routerLink: MENU_ROUTES.ETS_ADMINISTRATION_UPLOAD_EMISSIONS_TABLE,
          protectedScopes: [{ name: MENU_SCOPES.EMISSIONS_TABLE_WRITE }],
        },
        {
          label: 'Recalculate dynamic surrender status',
          routerLink:
            MENU_ROUTES.ETS_ADMINISTRATION_RECALCULATE_COMPLIANCE_STATUS,
          protectedScopes: [
            { name: MENU_SCOPES.RECALCULATE_COMPLIANCE_STATUS },
          ],
        },
        {
          label: 'View allocation job status',
          routerLink: MENU_ROUTES.ETS_ADMINISTRATION_VIEW_ALLOCATION_JOB_STATUS,
          protectedScopes: [
            { name: MENU_SCOPES.RECALCULATE_COMPLIANCE_STATUS },
          ],
          queryParams: { mode: SearchMode.INITIAL_LOAD },
        },
      ],
    },
    {
      label: 'KP Administration',
      routerLink: MENU_ROUTES.KP_ADMINISTRATION,
      protectedScopes: [
        { name: MENU_SCOPES.ISSUE_KP_UNITS_READ },
        { name: MENU_SCOPES.ITL_MESSAGE_READ },
        { name: MENU_SCOPES.ITL_NOTICE_READ },
      ],
      activeMenuItem: HeaderItem.KP_ADMIN,
      subMenus: [
        {
          label: 'Issue KP Units',
          routerLink: MENU_ROUTES.KP_ADMINISTRATION_ISSUE_UNITS,
          protectedScopes: [{ name: MENU_SCOPES.ISSUE_KP_UNITS_READ }],
        },
        {
          label: 'ITL Messages',
          routerLink: MENU_ROUTES.KP_ADMINISTRATION_ITL_MESSAGES,
          protectedScopes: [{ name: MENU_SCOPES.ITL_MESSAGE_READ }],
        },
        {
          label: 'ITL Notifications',
          routerLink: MENU_ROUTES.KP_ADMINISTRATION_ITL_NOTIFICATIONS,
          protectedScopes: [{ name: MENU_SCOPES.ITL_NOTICE_READ }],
        },
        {
          label: 'KP reconciliation administration',
          routerLink: MENU_ROUTES.KP_ADMINISTRATION_ITL_RECONCILIATION,
          protectedScopes: [{ name: MENU_SCOPES.ITL_RECONCILIATION_READ }],
        },
      ],
    },
    {
      label: 'Registry Administration',
      routerLink: MENU_ROUTES.REGISTRY_ADMINISTRATION,
      activeMenuItem: HeaderItem.REG_ADMIN,
      protectedScopes: [{ name: MENU_SCOPES.REGISTRY_ADMIN_MENU_ITEM_VIEW }],
    },
    {
      label: 'Reports',
      routerLink: MENU_ROUTES.REPORTS,
      protectedScopes: [
        { name: MENU_SCOPES.REPORTS_USER, clientId: 'uk-ets-reports-api' },
      ],
      activeMenuItem: HeaderItem.REPORTS,
      subMenus: [
        {
          label: 'Download files',
          routerLink: MENU_ROUTES.REPORTS_DOWNLOADS,
          protectedScopes: [
            { name: MENU_SCOPES.REPORTS_USER, clientId: 'uk-ets-reports-api' },
          ],
        },
        {
          label: 'Standard reports',
          routerLink: MENU_ROUTES.REPORTS_STANDARD,
          protectedScopes: [
            { name: MENU_SCOPES.STANDARD_REPORTS_MENU_ITEM_VIEW },
          ],
        },
        {
          label: 'ETS Report Publication',
          routerLink: MENU_ROUTES.ETS_REPORT_PUBLICATION,
          protectedScopes: [{ name: MENU_SCOPES.ISSUE_ALLOWANCES_READ }],
        },
        {
          label: 'KP Report Publication',
          routerLink: MENU_ROUTES.KP_REPORT_PUBLICATION,
          protectedScopes: [{ name: MENU_SCOPES.ADMIN_MENU_ITEM_VIEW }],
        },
      ],
    },
    {
      label: 'Notifications',
      routerLink: MENU_ROUTES.NOTIFICATIONS,
      activeMenuItem: HeaderItem.NOTIFICATIONS,
      protectedScopes: [
        { name: MENU_SCOPES.NOTIFICATION_ADMINISTRATION, isPermission: true },
      ],
    },
    {
      label: 'Documents',
      routerLink: MENU_ROUTES.DOCUMENTS,
      activeMenuItem: HeaderItem.DOCUMENTS,
      checkAuthenticated: true,
    },
    {
      label: 'About',
      routerLink: MENU_ROUTES.ABOUT,
      activeMenuItem: HeaderItem.ABOUT,
      checkAuthenticated: false,
    },
  ];
};
