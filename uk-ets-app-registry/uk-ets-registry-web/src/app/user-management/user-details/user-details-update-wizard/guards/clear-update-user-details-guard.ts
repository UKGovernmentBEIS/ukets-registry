import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearUserDetailsUpdateRequest } from '@user-update/action/user-details-update.action';
import { canGoBack } from '@shared/shared.action';

@Injectable({
  providedIn: 'root',
})
export class ClearUpdateUserDetailsGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearUserDetailsUpdateRequest());
    this.store.dispatch(
      canGoBack({
        goBackRoute: null,
      })
    );
    return true;
  }
}
