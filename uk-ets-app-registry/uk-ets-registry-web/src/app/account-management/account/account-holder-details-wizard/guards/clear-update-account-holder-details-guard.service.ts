import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearAccountHolderDetailsUpdateRequest } from '../actions/account-holder-details-wizard.action';

@Injectable({
  providedIn: 'root',
})
export class ClearUpdateAccountHolderDetailsGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearAccountHolderDetailsUpdateRequest());
    return true;
  }
}
