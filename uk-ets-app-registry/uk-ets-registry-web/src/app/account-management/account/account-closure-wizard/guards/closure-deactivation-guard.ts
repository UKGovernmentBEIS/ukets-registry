import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearAccountClosureState } from '@account-management/account/account-closure-wizard/actions';

@Injectable({
  providedIn: 'root',
})
export class ClosureDeactivationGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ) {
    this.store.dispatch(clearAccountClosureState());
    return true;
  }
}
