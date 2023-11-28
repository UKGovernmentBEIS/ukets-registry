import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import {
  selectBrowserCookiesEnabled,
  selectRegistryConfigurationProperty,
  selectCookiesAccepted,
} from '@shared/shared.selector';
import { ActivatedRoute, Router } from '@angular/router';
import { select, Store } from '@ngrx/store';
import { acceptAllCookies } from '@shared/shared.action';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-cookies-pop-up-container',
  template: `
    <app-cookies-pop-up
      [cookiesExpirationTime]="cookiesExpirationTime$ | async"
      [areCookiesAccepted]="areCookiesAccepted$ | async"
      [areBrowserCookiesEnabled]="areBrowserCookiesEnabled$ | async"
      (cookiesAcceptedEmitter)="cookiesAccepted($event)"
    ></app-cookies-pop-up>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CookiesPopUpContainerComponent implements OnInit {
  cookiesExpirationTime$: Observable<string>;
  areCookiesAccepted$: Observable<boolean>;
  areBrowserCookiesEnabled$: Observable<boolean>;

  constructor(
    private _router: Router,
    private route: ActivatedRoute,
    private store: Store
  ) {}

  ngOnInit() {
    this.cookiesExpirationTime$ = this.store.pipe(
      select(selectRegistryConfigurationProperty, {
        property: 'business.property.cookies.expiration.time',
      })
    );
    this.areCookiesAccepted$ = this.store.pipe(select(selectCookiesAccepted));
    this.areBrowserCookiesEnabled$ = this.store.pipe(
      select(selectBrowserCookiesEnabled)
    );
  }

  cookiesAccepted(expired) {
    this.store.dispatch(acceptAllCookies({ expirationTime: expired }));
  }
}
