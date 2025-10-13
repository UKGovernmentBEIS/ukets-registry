import { TestBed } from '@angular/core/testing';
import { NavMenuService } from '@shared/services/nav-menu.service';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { MENU_ITEMS, MENU_SCOPES } from '@shared/model/navigation-menu';
import { HeaderItem } from '@shared/header/header-item.enum';
import { UK_ETS_REGISTRY_API_BASE_URL } from '@registry-web/app.tokens';
import { KeycloakService } from 'keycloak-angular';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { cold } from 'jasmine-marbles';
import { map } from 'rxjs/operators';
import { provideHttpClient } from '@angular/common/http';

describe('NavMenuService', () => {
  let service: NavMenuService;
  let authApiService: AuthApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        NavMenuService,
        AuthApiService,
        {
          provide: UK_ETS_REGISTRY_API_BASE_URL,
          useValue: 'apiBaseUrl',
        },
        {
          provide: KeycloakService,
          useValue: {},
        },
      ],
    });
    service = TestBed.inject(NavMenuService);
    authApiService = TestBed.inject(AuthApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('Load menu permissions', () => {
    test('should retrieve all scopes', () => {
      const allMenuScopes = Object.values(MENU_SCOPES).sort();
      allMenuScopes.splice(5, 1);

      //Mock hasScope
      const authApiServiceSpy = jest.spyOn(authApiService, 'hasScope');
      authApiServiceSpy.mockImplementation((_, __) => {
        return of(true);
      });

      const observable = service
        .loadMenuPermissions(true, false, true, true)
        .pipe(map((x) => x.sort()));

      const expected = cold('(a|)', {
        a: allMenuScopes,
      });

      expect(observable).toBeObservable(expected);
    });

    it('should retrieve only scopes that user has access to', () => {
      const allMenuScopes = Object.values(MENU_SCOPES).sort();
      allMenuScopes.splice(5, 1);
      const authApiServiceSpy = jest.spyOn(authApiService, 'hasScope');
      authApiServiceSpy.mockImplementation((scope, _) => {
        if (scope === 'urn:uk-ets-registry-api:issue-allowances:read') {
          return of(false);
        }
        return of(true);
      });

      const observable = service
        .loadMenuPermissions(true, false, true, true)
        .pipe(map((x) => x.sort()));

      const expected = cold('(a|)', {
        a: allMenuScopes.filter((s) => s !== MENU_SCOPES.ISSUE_ALLOWANCES_READ),
      });

      expect(observable).toBeObservable(expected);
    });
  });

  describe('Get All unique scopes for menu', () => {
    it('should calculate unique scopes for navigation menu ', () => {
      const allMenuScopes = Object.values(MENU_SCOPES).sort();
      allMenuScopes.splice(5, 1);
      const scopes = service.getAllUniqueScopesForMenu(false, true, true);

      expect(scopes.length).toEqual(20);
      expect(scopes.map((s) => s.name).sort()).toEqual(allMenuScopes);
    });
  });

  describe('Load menu permissions with environment variable to run for RAs only', () => {
    it('should retrieve all scopes', () => {
      const allMenuScopes = Object.values(MENU_SCOPES).sort();
      allMenuScopes.splice(3, 1);
      const authApiServiceSpy = jest.spyOn(authApiService, 'hasScope');
      authApiServiceSpy.mockImplementation((_, __) => {
        return of(true);
      });

      const observable = service
        .loadMenuPermissions(true, true, true, true)
        .pipe(map((x) => x.sort()));

      const expected = cold('(a|)', {
        a: allMenuScopes,
      });

      expect(observable).toBeObservable(expected);
    });

    it('should retrieve only scopes that user has access to', () => {
      const allMenuScopes = Object.values(MENU_SCOPES).sort();
      allMenuScopes.splice(3, 1);
      const authApiServiceSpy = jest.spyOn(authApiService, 'hasScope');
      authApiServiceSpy.mockImplementation((scope, _) => {
        if (scope === 'urn:uk-ets-registry-api:issue-allowances:read') {
          return of(false);
        }
        return of(true);
      });

      const observable = service
        .loadMenuPermissions(true, true, true, true)
        .pipe(map((x) => x.sort()));

      const expected = cold('(a|)', {
        a: allMenuScopes.filter((s) => s !== MENU_SCOPES.ISSUE_ALLOWANCES_READ),
      });

      expect(observable).toBeObservable(expected);
    });
  });

  describe('Get All unique scopes for menu with environment variable to run for RAs only', () => {
    it('should calculate unique scopes for navigation menu ', () => {
      const allMenuScopes = Object.values(MENU_SCOPES).sort();
      allMenuScopes.splice(3, 1);
      const scopes = service.getAllUniqueScopesForMenu(true, true, true);

      expect(scopes.length).toEqual(20);
      expect(scopes.map((s) => s.name).sort()).toEqual(allMenuScopes);
    });
  });

  describe('Get active menu items', () => {
    it('should get visible menu items', () => {
      const { topMenuActive, subMenuActive } = service.getActiveMenuItems(
        MENU_ITEMS(false, true, true),
        MENU_ITEMS(false, true, true).find(
          (item) => item.label === 'ETS Administration'
        ).subMenus,
        '/ets-administration/issue-allowances'
      );

      expect(topMenuActive).toEqual(HeaderItem.ETS_ADMIN);
      expect(subMenuActive).toEqual('/ets-administration/issue-allowances');
    });

    it('should get visible menu items when route does not contain top menu route', () => {
      const { topMenuActive, subMenuActive } = service.getActiveMenuItems(
        MENU_ITEMS(false, true, true),
        MENU_ITEMS(false, true, true).find((item) => item.label === 'Accounts')
          .subMenus,
        '/account-list'
      );

      expect(topMenuActive).toEqual(HeaderItem.ACCOUNTS);
      expect(subMenuActive).toEqual('/account-list');
    });
  });

  describe('Check navigated route is a menu route', () => {
    it('should keep menu route', () => {
      const isMenuRoute = service.isNavigatedRouteAMenuRoute(
        MENU_ITEMS(false, true, true),
        MENU_ITEMS(false, true, true).find((item) => item.label === 'Accounts')
          .subMenus,
        '/account-list'
      );

      expect(isMenuRoute).toBeTruthy();
    });

    it('should not keep non-menu route', () => {
      const isMenuRoute = service.isNavigatedRouteAMenuRoute(
        MENU_ITEMS(false, true, true),
        MENU_ITEMS(false, true, true).find((item) => item.label === 'Accounts')
          .subMenus,
        '/unrelated-route'
      );

      expect(isMenuRoute).toBeFalsy();
    });
  });
});
