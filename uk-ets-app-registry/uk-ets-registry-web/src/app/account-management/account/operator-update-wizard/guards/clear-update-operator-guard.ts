import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearOperatorUpdateRequest } from '@operator-update/actions/operator-update.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearUpdateOperatorGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearOperatorUpdateRequest());
    return true;
  }
}
