import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearTrustedAccountListUpdateRequest } from '@trusted-account-list/actions/trusted-account-list.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearUpdateTrustedAccountGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearTrustedAccountListUpdateRequest());
    return true;
  }
}
