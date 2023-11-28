import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearUpdateExclusionStatus } from '@registry-web/account-management/account/exclusion-status-update-wizard/actions/update-exclusion-status.action';
import { canGoBack } from '@shared/shared.action';

@Injectable({
  providedIn: 'root',
})
export class ClearExclusionStatusGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearUpdateExclusionStatus());
    this.store.dispatch(
      canGoBack({
        goBackRoute: null,
      })
    );
    return true;
  }
}
