import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearAuthorisedRepresentativesUpdateRequest } from '@authorised-representatives/actions/authorised-representatives.actions';

@Injectable({
  providedIn: 'root',
})
export class ClearUpdateAuthorisedRepresentativesGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearAuthorisedRepresentativesUpdateRequest());
    return true;
  }
}
