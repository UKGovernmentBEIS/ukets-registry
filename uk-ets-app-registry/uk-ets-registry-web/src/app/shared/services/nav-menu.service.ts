import { Injectable } from '@angular/core';
import { MENU_ITEMS, MenuItem, MenuScope } from '@shared/model/navigation-menu';
import { from, Observable, of } from 'rxjs';
import { filter, map, mergeMap, toArray } from 'rxjs/operators';
import { AuthApiService } from '@registry-web/auth/auth-api.service';
import { HeaderItem } from '@shared/header/header-item.enum';

@Injectable({ providedIn: 'root' })
export class NavMenuService {
  constructor(private authApiService: AuthApiService) {}

  /**
   *
   *  For each menu-related scope or permission, checks if the user has it (async operation).
   *  TODO: find a way to use forkJoin to parallelize requests.
   *
   */
  loadMenuPermissions(
    isUserAuthenticated: boolean,
    allowAccountOpeningOnlyToRAs: boolean,
    isUserEnrolled: boolean,
    isAdmin: boolean
  ): Observable<string[]> {
    const allUniqueScopes: MenuScope[] = this.getAllUniqueScopesForMenu(
      allowAccountOpeningOnlyToRAs,
      isUserEnrolled,
      isAdmin
    );
    return from(allUniqueScopes).pipe(
      mergeMap((scope) =>
        this.hasScope(isUserAuthenticated, scope).pipe(
          filter((hasScope) => hasScope),
          map(() => scope.name)
        )
      ),
      toArray()
    );
  }

  /**
   * Given a route, returns the active menu/submenu item pair.
   */
  getActiveMenuItems(
    menuItems: MenuItem[],
    subMenuItems: MenuItem[],
    currentRouteUrl: string
  ) {
    let topMenuActive: HeaderItem = menuItems.find((item) =>
      currentRouteUrl.startsWith(item.routerLink)
    )?.activeMenuItem;

    const subMenuItem = subMenuItems.find((item) =>
      currentRouteUrl.includes(item.routerLink)
    );
    const subMenuActive: string = subMenuItem?.routerLink;
    // some submenus are not under their respective parent menus in the router hierarchy.
    // For those, we need to retrieve the parent menu from the submenu.
    // TODO: maybe we could do this directly
    if (topMenuActive === undefined) {
      topMenuActive = menuItems.find((item) =>
        item.subMenus?.some((subMenu) => subMenu.routerLink === subMenuActive)
      )?.activeMenuItem;
    }
    return { topMenuActive, subMenuActive };
  }

  /**
   * Filter that keep only menu-related routes.
   */
  isNavigatedRouteAMenuRoute(
    menuItems: MenuItem[],
    subMenuItems: MenuItem[],
    currentRouteUrl: string
  ) {
    return menuItems.some(
      (menuItem) =>
        currentRouteUrl.startsWith(menuItem.routerLink) ||
        subMenuItems.some((subMenuItem) =>
          currentRouteUrl.includes(subMenuItem.routerLink)
        )
    );
  }

  /**
   * Kinda dirty workaround, in the case when navigating to pages that start
   * with a sub-menu root route URL and need to hide the sub-menu items.
   *
   * @param routeUrl the route URL string.
   */
  routeUrlBelongIn(routeUrl: string): boolean {
    const routeUrlsUnderSubMenu = [
      '/kp-report-publication/section-details',
      '/ets-report-publication/section-details',
    ];
    return routeUrlsUnderSubMenu.some((s) => routeUrl.includes(s));
  }
  /**
   * Collects all unique scopes needed for the menu to be dynamically generated.
   *  1. The scopes/permissions that a menu needs to be visible (flattenedMenuItemScopes).
   *  2. The scopes/permissions that the a sub-menu needs to be visible (flattenedSubMenuItemScopes).
   *  3. The scopes/permissions that a menu needs to set its  router link (flattenedRouterLinkScopes).
   *
   *  @param allowAccountOpeningOnlyToRAs checks if we are going to allow admins to create account requests
   *  @param isUserEnrolled checks whether user is enrolled or not
   *  @param isAdmin checks whether user is admin
   */
  getAllUniqueScopesForMenu(
    allowAccountOpeningOnlyToRAs: boolean,
    isUserEnrolled: boolean,
    isAdmin: boolean
  ): MenuScope[] {
    const flattenedMenuItemScopes: MenuScope[] = ([] as MenuScope[]).concat(
      ...MENU_ITEMS(allowAccountOpeningOnlyToRAs, isUserEnrolled, isAdmin)
        .filter((menuItem) => menuItem.protectedScopes?.length > 0)
        .map((menuItem) => menuItem.protectedScopes)
    );

    const flattenedSubMenuItems: MenuItem[] = ([] as MenuItem[]).concat(
      ...MENU_ITEMS(allowAccountOpeningOnlyToRAs, isUserEnrolled, isAdmin)
        .filter((menuItem) => menuItem.subMenus?.length > 0)
        .map((menuItem) => menuItem.subMenus)
    );

    const flattenedSubMenuItemScopes: MenuScope[] = ([] as MenuScope[]).concat(
      ...flattenedSubMenuItems
        .filter((subMenuItem) => subMenuItem.protectedScopes?.length > 0)
        .map((subMenuItem) => subMenuItem?.protectedScopes)
    );

    const flattenedRouterLinkScopes: MenuScope[] = ([] as string[])
      .concat(
        ...MENU_ITEMS(allowAccountOpeningOnlyToRAs, isUserEnrolled, isAdmin)
          .filter((menuItem) => !!menuItem.defaultRouterLink)
          .map((menuItem) => menuItem?.routerLinkForScope?.scope)
      )
      .map((name) => ({ name }));

    const allScopes = [
      ...flattenedMenuItemScopes,
      ...flattenedSubMenuItemScopes,
      ...flattenedRouterLinkScopes,
    ];
    // this filter operation returns only distinct scopes (by name)
    return allScopes.filter(
      (menuScope, index, originalArray) =>
        originalArray.findIndex((s) => s.name === menuScope.name) === index
    );
  }

  /**
   * This method reproduces the protect pipe logic.
   */
  private hasScope(isUserAuthenticated: boolean, scope: MenuScope) {
    if (isUserAuthenticated) {
      if (scope) {
        return this.authApiService.hasScope(
          scope.name,
          scope.isPermission,
          scope.clientId
        );
      } else {
        return of(true);
      }
    } else {
      return of(false);
    }
  }
}
