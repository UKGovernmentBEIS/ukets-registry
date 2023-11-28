import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { NavigationExtras, Router } from '@angular/router';
import { take } from 'rxjs/operators';
import {
  selectGoBackNavigationExtras,
  selectGoBackRoute,
} from '@shared/shared.selector';
import { GoBackNavigationExtras } from '.';

@Component({
  selector: 'app-back-button',
  templateUrl: './back-button.component.html',
})
export class BackButtonComponent {
  goBackRoute$: Observable<string> = this.store.select(selectGoBackRoute);

  goBackNavigationExtras$: Observable<GoBackNavigationExtras> =
    this.store.select(selectGoBackNavigationExtras);

  constructor(private store: Store, private _router: Router) {}

  goBack(route: string) {
    const extras: NavigationExtras = { skipLocationChange: false };
    this.goBackNavigationExtras$
      .pipe(take(1))
      .subscribe((goBackNavigationExtras) => {
        if (route) {
          if (goBackNavigationExtras && goBackNavigationExtras.queryParams) {
            extras.queryParams = goBackNavigationExtras.queryParams;
          }
          if (
            goBackNavigationExtras &&
            goBackNavigationExtras.skipLocationChange
          ) {
            extras.skipLocationChange =
              goBackNavigationExtras.skipLocationChange;
          }
          this._router.navigate([route], extras);
        }
      });
  }
}
