import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { IsLoggedInCheck, Login, Logout } from '../../auth/auth.actions';
import { selectUrid } from '../../auth/auth.selector';
import { Header } from 'govuk-frontend';
import { HeaderItem } from './header-item.enum';
import { MENU_ROUTES, MenuItem, NavMenu } from '../model/navigation-menu';
import { prepareNavigationToUserDetails } from '@user-management/user-details/store/actions';
import { Observable } from 'rxjs';
import { selectActiveMenutem, selectNavMenu } from '@shared/shared.selector';
import { exhaustMap, filter, tap } from 'rxjs/operators';
import {
  clearGoBackToListRoute,
  retrieveUserStatus,
} from '@shared/shared.action';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  @Input() isAuthenticated: boolean;
  urid$: Observable<string>;
  navMenu$: Observable<NavMenu>;
  activeMenuItem$: Observable<HeaderItem>;

  readonly menuRoutes = MENU_ROUTES;

  constructor(
    private store: Store,
    private router: Router
  ) {}

  ngOnInit() {
    this.store.dispatch(IsLoggedInCheck());
    const $header = document.querySelector('#header');
    if ($header) {
      new Header($header);
    }

    this.urid$ = this.store.select(selectUrid);
    this.navMenu$ = this.urid$.pipe(
      filter((urid) => urid != null),
      tap(() => {
        this.store.dispatch(retrieveUserStatus());
      }),
      exhaustMap(() => {
        return this.store.pipe(select(selectNavMenu));
      })
    );
    this.activeMenuItem$ = this.store.select(selectActiveMenutem);
  }

  login() {
    // Dispatch the login action
    this.store.dispatch(
      Login({
        redirectUri: location.origin + MENU_ROUTES.DASHBOARD,
      })
    );
  }

  logout() {
    // Dispatch the logout action
    this.store.dispatch(
      Logout({
        redirectUri: location.origin + MENU_ROUTES.DASHBOARD,
      })
    );
  }

  onOpenMyProfile(urid: string) {
    this.store.dispatch(
      prepareNavigationToUserDetails({ urid, backRoute: null })
    );
  }

  onClickedItem(item: MenuItem) {
    const url =
      item.subMenus?.length > 0 ? item.subMenus[0].routerLink : item.routerLink;
    this.router.navigate([url], {
      queryParams: item.queryParams,
    });
    this.store.dispatch(clearGoBackToListRoute());
  }
}
