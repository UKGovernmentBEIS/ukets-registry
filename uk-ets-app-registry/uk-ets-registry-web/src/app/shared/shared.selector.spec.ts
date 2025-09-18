import * as selectors from './shared.selector';
import { MENU_ITEMS, MENU_SCOPES } from '@shared/model/navigation-menu';
import { HeaderItem } from '@shared/header/header-item.enum';

describe('Shared Selectors', () => {
  let allMenuScopes: MENU_SCOPES[];
  let state;

  beforeEach(() => {
    allMenuScopes = Object.values(MENU_SCOPES);

    state = {
      navMenu: null,
      configurationRegistry: [
        {
          'account.opening.only.for.registry.administration': 'true',
        },
      ],
      menuScopes: ['urn:uk-ets-registry-api:account:create'],
      isMenuRoute: true,
      isTimeoutDialogVisible: false,
    };
  });
  describe('selectNavMenu', () => {
    it('should not show transactions menu when user does not have required scope ', () => {
      const filteredScopes = allMenuScopes.filter(
        (s) => s !== MENU_SCOPES.TRANSACTION_MENU_ITEM_VIEW
      );

      const actual = selectors.selectNavMenu.projector(
        state,
        filteredScopes,
        true,
        true
      );
      actual.menuItems.find((i) => i.label === 'Transactions');
      expect(
        actual.menuItems.find((i) => i.label === 'Transactions')
      ).toBeUndefined();
    });

    it('should hide all submenus for which the user does not have relevant scopes', () => {
      const filteredScopes = allMenuScopes.filter(
        (s) =>
          s !== MENU_SCOPES.ISSUE_ALLOWANCES_READ &&
          s !== MENU_SCOPES.REQUEST_ALLOCATION_WIZARD_RESOURCE
      );

      const actual = selectors.selectNavMenu.projector(
        state,
        filteredScopes,
        true,
        true
      );
      expect(
        actual.menuItems.find((i) => i.label === 'ETS Administration')?.subMenus
          .length
      ).toEqual(5);
    });

    it('should hide submenus which have hideOnScopes property & submenus which the user does not have required scope', () => {
      const actual = selectors.selectNavMenu.projector(
        state,
        allMenuScopes,
        true,
        true
      );

      const etsAdminSubMenus = actual.menuItems.find(
        (i) => i.label === 'ETS Administration'
      ).subMenus;
      const requestAllocationSubMenu = etsAdminSubMenus.find(
        (i) => i.label === 'Request allocation of UK allowances'
      );

      expect(etsAdminSubMenus.length).toEqual(7);
      expect(requestAllocationSubMenu).toBeUndefined();
    });
  });

  describe('selectVisibleSubMenuItems', () => {
    it('should select visible submenus', () => {
      expect(
        selectors.selectVisibleSubMenuItems.projector(
          { menuItems: MENU_ITEMS(false, true, true) },
          HeaderItem.ETS_ADMIN,
          true
        )
      ).toEqual(
        MENU_ITEMS(false, true, true).find(
          (i) => i.label === 'ETS Administration'
        ).subMenus
      );
    });

    it('should select no submenus when route is not menu-related', () => {
      expect(
        selectors.selectVisibleSubMenuItems.projector(
          { menuItems: MENU_ITEMS(false, true, true) },
          HeaderItem.ETS_ADMIN,
          false
        )
      ).toEqual([]);
    });
  });

  describe('selectVisibleSubMenuItems with environment variable to run for RAs only', () => {
    it('should select visible submenus', () => {
      expect(
        selectors.selectVisibleSubMenuItems.projector(
          { menuItems: MENU_ITEMS(true, true, true) },
          HeaderItem.ETS_ADMIN,
          true
        )
      ).toEqual(
        MENU_ITEMS(false, true, true).find(
          (i) => i.label === 'ETS Administration'
        ).subMenus
      );
    });

    it('should select no submenus when route is not menu-related', () => {
      expect(
        selectors.selectVisibleSubMenuItems.projector(
          { menuItems: MENU_ITEMS(true, true, true) },
          HeaderItem.ETS_ADMIN,
          false
        )
      ).toEqual([]);
    });
  });

  describe('selectErrorDetailByErrorId', () => {
    it('should select error details by the error ID', () => {
      expect(
        selectors.selectErrorDetailByErrorId.projector(
          {
            errors: [
              {
                componentId: 'A_COMPONENT_ID',
                errorMessage: 'ERROR : INVALID DATA',
                errorId: 'AN_ERROR_CODE',
              },
            ],
          },
          { errorId: 'AN_ERROR_CODE' },
          true
        )
      ).toEqual([
        {
          componentId: 'A_COMPONENT_ID',
          errorMessage: 'ERROR : INVALID DATA',
          errorId: 'AN_ERROR_CODE',
        },
      ]);
    });

    it('should not select error details by the error ID when errorID is missing', () => {
      expect(
        selectors.selectErrorDetailByErrorId.projector(
          {
            errors: [
              {
                componentId: 'A_COMPONENT_ID',
                errorMessage: 'ERROR : INVALID DATA',
                errorId: 'ANOTHER_ERROR_CODE',
              },
            ],
          },
          { errorId: 'AN_ERROR_CODE' },
          true
        )
      ).toEqual([]);
    });
  });
});
