import {
  ActivatedRouteSnapshot,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { Store } from '@ngrx/store';
import { clearBulkArWizard } from '@registry-web/bulk-ar/actions/bulk-ar.actions';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ClearBulkArGuard {
  constructor(private router: Router, private store: Store) {}

  canDeactivate(
    component: any,
    currentRoute: ActivatedRouteSnapshot,
    currentState: RouterStateSnapshot,
    nextState?: RouterStateSnapshot
  ): boolean {
    this.store.dispatch(clearBulkArWizard());
    return true;
  }
}
