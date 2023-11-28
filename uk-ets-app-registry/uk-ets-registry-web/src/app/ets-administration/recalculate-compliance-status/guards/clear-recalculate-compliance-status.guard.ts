import { Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { clearRecalculatioComplianceStatuAllCompliantEntities } from '@recalculate-compliance-status/store/actions';

@Injectable({
  providedIn: 'root',
})
export class ClearRecalculateComplianceStatusGuard {
  constructor(private store: Store) {}

  canDeactivate(
    component: unknown,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ):
    | Observable<boolean | UrlTree>
    | Promise<boolean | UrlTree>
    | boolean
    | UrlTree {
    this.store.dispatch(clearRecalculatioComplianceStatuAllCompliantEntities());
    return true;
  }
}
